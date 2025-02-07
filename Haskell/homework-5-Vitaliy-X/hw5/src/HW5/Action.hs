{-# LANGUAGE LambdaCase #-}

module HW5.Action (
    HIO (..),
    HiPermission (..),
    PermissionException (..),
) where

import Control.Applicative (optional)
import Control.Exception (Exception, throwIO)
import HW5.Base (HiAction (..), HiMonad (..), HiValue (..))
import qualified Data.Sequence
import qualified Control.Monad
import qualified Data.ByteString
import Data.Set (Set)
import qualified Data.Text
import Data.Text.Encoding (decodeUtf8')
import Data.Time.Clock.POSIX (getCurrentTime)
import System.Random.Stateful (getStdRandom, uniformR)
import System.Directory (createDirectory, getCurrentDirectory, listDirectory, setCurrentDirectory, doesDirectoryExist)

data HiPermission
    = AllowRead
    | AllowWrite
    | AllowTime
    deriving (Ord, Enum, Eq, Show, Bounded)

newtype PermissionException = PermissionRequired HiPermission deriving (Show)

instance Exception PermissionException

newtype HIO a = HIO {runHIO :: Set HiPermission -> IO a}

instance Functor HIO where
    fmap g hioAction = HIO (fmap g . runHIO hioAction)

instance Applicative HIO where
    pure x = HIO $ const (pure x)
    (<*>) = Control.Monad.ap

instance Monad HIO where
    hio >>= f = HIO $ \permissions -> do
        result <- runHIO hio permissions
        runHIO (f result) permissions

instance HiMonad HIO where
    runAction = \case
        HiActionRead path -> withPermission AllowRead $ do
            isDirectory <- doesDirectoryExist path
            if isDirectory
                then do
                    files <- listDirectory path
                    let hiList = HiValueList . Data.Sequence.fromList $ map (HiValueString . Data.Text.pack) files
                    pure hiList
                else do
                    content <- Data.ByteString.readFile path
                    let result = either (const $ HiValueBytes content) HiValueString $ decodeUtf8' content
                    pure result

        HiActionWrite path content -> withPermission AllowWrite $ do
            Data.ByteString.writeFile path content
            pure HiValueNull

        HiActionChDir path -> withPermission AllowRead $ do
            setCurrentDirectory path
            pure HiValueNull

        HiActionMkDir path -> withPermission AllowWrite $ do
            _ <- optional $ createDirectory path
            pure HiValueNull

        HiActionCwd -> withPermission AllowRead $ do
            currentDir <- getCurrentDirectory
            pure $ HiValueString $ Data.Text.pack currentDir

        HiActionNow -> withPermission AllowTime $ do
            currentTime <- getCurrentTime
            pure $ HiValueTime currentTime

        HiActionRand low high -> HIO $ \_ -> do
            randomValue <- getStdRandom (uniformR (low, high))
            pure $ HiValueNumber $ toRational randomValue

        HiActionEcho message -> withPermission AllowWrite $ do
            putStrLn $ Data.Text.unpack message
            pure HiValueNull

withPermission :: HiPermission -> IO a -> HIO a
withPermission requiredAction ioAction = HIO $ \availablePermissions ->
    if requiredAction `elem` availablePermissions
        then ioAction
        else throwIO $ PermissionRequired requiredAction