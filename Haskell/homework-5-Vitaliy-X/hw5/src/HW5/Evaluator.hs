{-# LANGUAGE FlexibleInstances #-}
{-# LANGUAGE LambdaCase #-}
{-# LANGUAGE UndecidableInstances #-}

module HW5.Evaluator (
    eval,
)
where

import Codec.Compression.Zlib (CompressParams (..), bestCompression, compressWith, decompress, defaultCompressParams)
import Codec.Serialise (deserialiseOrFail, serialise)
import Control.Monad.Except (ExceptT (..), runExceptT)
import Control.Monad.Trans.Except (throwE)
import HW5.Base (HiAction (..), HiError (..), HiExpr (..), HiFun (..), HiMonad (..), HiValue (..))
import qualified Data.Bits.Extras
import qualified Data.ByteString
import qualified Data.Foldable
import qualified Data.Map
import Data.ByteString.Lazy (fromStrict, toStrict)
import Data.Maybe (fromMaybe)
import Data.Either (fromRight)
import Data.Ratio (denominator)
import Data.Sequence (Seq (Empty, (:<|), (:|>)), fromList, length, reverse, (><))
import Data.Text.Encoding (decodeUtf8', encodeUtf8)
import Data.Time (addUTCTime, diffUTCTime)
import Data.Word (Word8)
import qualified Data.Sequence
import qualified Data.Text
import Text.Read (readMaybe)
import GHC.Base (stimes)
import GHC.Real (numerator)

eval :: HiMonad m => HiExpr -> m (Either HiError HiValue)
eval = runExceptT . evalExpr

evalExpr :: HiMonad m => HiExpr -> ExceptT HiError m HiValue
evalExpr (HiExprValue value) = handleValue value
evalExpr (HiExprDict dict) = handleExprDict dict
evalExpr (HiExprApply func args) = handleApply func args
evalExpr (HiExprRun expr) = handleRun expr

handleValue :: HiMonad m => HiValue -> ExceptT HiError m HiValue
handleValue value = return value

handleExprDict :: HiMonad m => [(HiExpr, HiExpr)] -> ExceptT HiError m HiValue
handleExprDict dict = HiValueDict . Data.Map.fromList <$> evalListPairs dict
  where
    evalListPairs :: HiMonad m => [(HiExpr, HiExpr)] -> ExceptT HiError m [(HiValue, HiValue)]
    evalListPairs = mapM $ \(k, v) -> do 
      key <- evalExpr k
      value <- evalExpr v
      return (key, value)

handleApply :: HiMonad m => HiExpr -> [HiExpr] -> ExceptT HiError m HiValue
handleApply func args = do
    result <- evalExpr func
    evalList (determineArity result) result args

handleRun :: HiMonad m => HiExpr -> ExceptT HiError m HiValue
handleRun expr = do
    result <- evalExpr expr
    case result of
        HiValueAction action -> ExceptT $ Right <$> runAction action
        _ -> throwE HiErrorInvalidArgument

data Arity
    = Single
    | Fixed
    | Triple
    | Flexible
    | VarArgs
    | InvalidFunction

determineArity :: HiValue -> Arity
determineArity (HiValueFunction fun) = case fun of
    HiFunNot -> Single
    HiFunLength -> Single
    HiFunToUpper -> Single
    HiFunToLower -> Single
    HiFunReverse -> Single
    HiFunTrim -> Single
    HiFunPackBytes -> Single
    HiFunUnpackBytes -> Single
    HiFunZip -> Single
    HiFunUnzip -> Single
    HiFunSerialise -> Single
    HiFunDeserialise -> Single
    HiFunEncodeUtf8 -> Single
    HiFunDecodeUtf8 -> Single
    HiFunRead -> Single
    HiFunMkDir -> Single
    HiFunChDir -> Single
    HiFunParseTime -> Single
    HiFunEcho -> Single
    HiFunCount -> Single
    HiFunKeys -> Single
    HiFunValues -> Single
    HiFunInvert -> Single
    HiFunDiv -> Fixed
    HiFunMul -> Fixed
    HiFunAdd -> Fixed
    HiFunSub -> Fixed
    HiFunAnd -> Fixed
    HiFunOr -> Fixed
    HiFunLessThan -> Fixed
    HiFunGreaterThan -> Fixed
    HiFunEquals -> Fixed
    HiFunNotLessThan -> Fixed
    HiFunNotGreaterThan -> Fixed
    HiFunNotEquals -> Fixed
    HiFunFold -> Fixed
    HiFunRange -> Fixed
    HiFunWrite -> Fixed
    HiFunRand -> Fixed
    HiFunIf -> Triple
    HiFunList -> VarArgs
determineArity (HiValueString _) = Flexible
determineArity (HiValueList _) = Flexible
determineArity (HiValueBytes _) = Flexible
determineArity (HiValueDict _) = Single
determineArity _ = InvalidFunction

evalList :: HiMonad m => Arity -> HiValue -> [HiExpr] -> ExceptT HiError m HiValue
evalList Flexible op (x : y : _) = evalList Fixed op [x, y]
evalList Flexible op [expr] = evalList Single op [expr]
evalList VarArgs (HiValueFunction HiFunList) elems = handleVarArgs elems
evalList Fixed (HiValueFunction HiFunAnd) (cond : [next]) = handleAnd cond next
evalList Fixed (HiValueFunction HiFunOr) (cond : [next]) = handleOr cond next
evalList Single op [expr] = handleSingle op expr
evalList Fixed op (first : [second]) = handleFixed op first second
evalList Triple op (cond : (middle : [final])) = handleTriple op cond middle final
evalList InvalidFunction _ _ = throwE HiErrorInvalidFunction
evalList _ _ _ = throwE HiErrorArityMismatch

handleVarArgs :: HiMonad m => [HiExpr] -> ExceptT HiError m HiValue
handleVarArgs elems = buildHiList elems Data.Sequence.Empty

handleAnd :: HiMonad m => HiExpr -> HiExpr -> ExceptT HiError m HiValue
handleAnd cond next = (`evalAnd` next) =<< evalExpr cond

handleOr :: HiMonad m => HiExpr -> HiExpr -> ExceptT HiError m HiValue
handleOr cond next = (`evalOr` next) =<< evalExpr cond

handleSingle :: HiMonad m => HiValue -> HiExpr -> ExceptT HiError m HiValue
handleSingle op expr = unaryOp op =<< evalExpr expr

handleFixed :: HiMonad m => HiValue -> HiExpr -> HiExpr -> ExceptT HiError m HiValue
handleFixed op first second = do
    result <- evalExpr first
    binaryOp op result =<< evalExpr second

handleTriple :: HiMonad m => HiValue -> HiExpr -> HiExpr -> HiExpr -> ExceptT HiError m HiValue
handleTriple op cond middle final = do
    result <- evalExpr cond
    ternaryOp op result middle final

buildHiList :: HiMonad m => [HiExpr] -> Data.Sequence.Seq HiValue -> ExceptT HiError m HiValue
buildHiList [] result = return $ HiValueList result
buildHiList (first : rest) result = buildHiList rest . (result Data.Sequence.:|>) =<< evalExpr first

evalAnd :: HiMonad m => HiValue -> HiExpr -> ExceptT HiError m HiValue
evalAnd x y = if x == HiValueBool False || x == HiValueNull
    then return x
    else evalExpr y

evalOr :: HiMonad m => HiValue -> HiExpr -> ExceptT HiError m HiValue
evalOr x y = if x == HiValueBool False || x == HiValueNull
    then evalExpr y
    else return x

unaryOp :: HiMonad m => HiValue -> HiValue -> ExceptT HiError m HiValue
unaryOp (HiValueString text) (HiValueNumber num) = getByIndex text num
unaryOp (HiValueList list) (HiValueNumber num) = getByIndex list num
unaryOp (HiValueBytes bytes) (HiValueNumber num) = getByIndex bytes num
unaryOp (HiValueDict dict) value = return $ fromMaybe HiValueNull $ Data.Map.lookup value dict
unaryOp func x = applyUnary func x

applyUnary :: HiMonad m => HiValue -> HiValue -> ExceptT HiError m HiValue
applyUnary (HiValueFunction HiFunSerialise) str = return $ HiValueBytes $ toStrict $ serialise str
applyUnary (HiValueFunction func) (HiValueBytes bytes) = handleBytes func bytes
applyUnary (HiValueFunction func) (HiValueString text) = handleString func text
applyUnary (HiValueFunction func) (HiValueDict dict) = handleDict func dict
applyUnary (HiValueFunction func) (HiValueList list) = handleList func list
applyUnary (HiValueFunction HiFunNot) (HiValueBool bool) = return $ HiValueBool $ not bool
applyUnary _ _ = throwE HiErrorInvalidArgument

binaryOp :: HiMonad m => HiValue -> HiValue -> HiValue -> ExceptT HiError m HiValue
binaryOp (HiValueString text) a b = slice text a b
binaryOp (HiValueList list) a b = slice list a b
binaryOp (HiValueBytes bytes) a b = slice bytes a b
binaryOp (HiValueFunction HiFunWrite) str (HiValueString b) = binaryOp (HiValueFunction HiFunWrite) str $ HiValueBytes $ encodeUtf8 b
binaryOp (HiValueFunction HiFunWrite) (HiValueString t) (HiValueBytes b) = return $ HiValueAction $ HiActionWrite (Data.Text.unpack t) b
binaryOp func a b = applyBinary func a b

applyBinary :: HiMonad m => HiValue -> HiValue -> HiValue -> ExceptT HiError m HiValue
applyBinary (HiValueFunction HiFunAdd) (HiValueString a) (HiValueString b) = return $ HiValueString $ a <> b
applyBinary (HiValueFunction HiFunAdd) (HiValueList a) (HiValueList b) = return $ HiValueList $ a <> b
applyBinary (HiValueFunction HiFunAdd) (HiValueBytes a) (HiValueBytes b) = return $ HiValueBytes $ a <> b
applyBinary (HiValueFunction HiFunAdd) (HiValueTime a) (HiValueNumber b) = return $ HiValueTime $ addUTCTime (fromRational b) a

applyBinary (HiValueFunction HiFunSub) (HiValueTime a) (HiValueTime b) = returnAsHiValue $ diffUTCTime a b

applyBinary (HiValueFunction HiFunMul) (HiValueString a) (HiValueNumber b) = evalTimes a b
applyBinary (HiValueFunction HiFunMul) (HiValueList a) (HiValueNumber b) = evalTimes a b
applyBinary (HiValueFunction HiFunMul) (HiValueBytes a) (HiValueNumber b) = evalTimes a b

applyBinary (HiValueFunction HiFunDiv) (HiValueString a) (HiValueString b) = return $ HiValueString $ a <> Data.Text.pack "/" <> b

applyBinary (HiValueFunction HiFunFold) _ (HiValueList Data.Sequence.Empty) = return HiValueNull
applyBinary (HiValueFunction HiFunFold) fun (HiValueList (h Data.Sequence.:<| t)) = case determineArity fun of
    Fixed -> foldSequence fun h t
    Flexible -> foldSequence fun h t
    _ -> throwE HiErrorInvalidFunction

applyBinary (HiValueFunction HiFunEquals) a b = return $ HiValueBool $ a == b
applyBinary (HiValueFunction HiFunNotEquals) a b = return $ HiValueBool $ a /= b
applyBinary (HiValueFunction HiFunLessThan) a b = return $ HiValueBool $ a < b
applyBinary (HiValueFunction HiFunGreaterThan) a b = return $ HiValueBool $ a > b
applyBinary (HiValueFunction HiFunNotGreaterThan) a b = return $ HiValueBool $ a <= b
applyBinary (HiValueFunction HiFunNotLessThan) a b = return $ HiValueBool $ a >= b

applyBinary (HiValueFunction fun) (HiValueNumber a) (HiValueNumber b) = handleNumber fun a b
applyBinary _ _ _ = throwE HiErrorInvalidArgument

ternaryOp :: HiMonad m => HiValue -> HiValue -> HiExpr -> HiExpr -> ExceptT HiError m HiValue
ternaryOp (HiValueFunction HiFunIf) (HiValueBool x) y z = if x then evalExpr y else evalExpr z
ternaryOp _ _ _ _ = throwE HiErrorInvalidArgument

foldSequence :: HiMonad m => HiValue -> HiValue -> Data.Sequence.Seq HiValue -> ExceptT HiError m HiValue
foldSequence _ value Data.Sequence.Empty = return value
foldSequence f value (first Data.Sequence.:<| second) = do
    ans <- evalList Fixed f [HiExprValue value, HiExprValue first]
    foldSequence f ans second

class Convertible a where
    toHiValue :: a -> HiValue
    returnAsHiValue :: HiMonad m => a -> ExceptT HiError m HiValue
    returnAsHiValue = return . toHiValue

instance {-# OVERLAPPABLE #-} Real a => Convertible a where
    toHiValue = HiValueNumber . toRational

instance Convertible Data.Text.Text where
    toHiValue = HiValueString

instance Convertible (Data.Sequence.Seq HiValue) where
    toHiValue = HiValueList

instance Convertible Data.ByteString.ByteString where
    toHiValue = HiValueBytes

class SequenceShell a where
    getLength :: a -> Rational
    getLength = toRational . getLengthInt
    getLengthInt :: a -> Int
    getIndex :: a -> Int -> HiValue
    takeElements :: Int -> a -> a
    dropElements :: Int -> a -> a
    convertToList :: a -> [HiValue]

instance SequenceShell Data.Text.Text where
    getLengthInt = Data.Text.length
    getIndex txt idx = (HiValueString . Data.Text.singleton) (Data.Text.index txt idx)
    takeElements = Data.Text.take
    dropElements = Data.Text.drop
    convertToList txt = map (HiValueString . Data.Text.singleton) (Data.Text.unpack txt)

instance SequenceShell (Data.Sequence.Seq HiValue) where
    getLengthInt = Data.Sequence.length
    getIndex = Data.Sequence.index
    takeElements = Data.Sequence.take
    dropElements = Data.Sequence.drop
    convertToList = Data.Foldable.toList

instance SequenceShell Data.ByteString.ByteString where
    getLengthInt = Data.ByteString.length
    getIndex bs idx = (HiValueNumber . toRational) $ Data.ByteString.index bs idx
    takeElements = Data.ByteString.take
    dropElements = Data.ByteString.drop
    convertToList bs = map (HiValueNumber . toRational) (Data.ByteString.unpack bs)

evalTimes :: (HiMonad m, Semigroup a, Convertible a) => a -> Rational -> ExceptT HiError m HiValue
evalTimes element factor = do
    count <- getInteger factor
    if count > 0
        then returnAsHiValue $ stimes count element
        else throwE HiErrorInvalidArgument

slice :: (HiMonad m, SequenceShell a, Convertible a) => a -> HiValue -> HiValue -> ExceptT HiError m HiValue
slice s (HiValueNumber start) (HiValueNumber end) = executeSlice s start end
slice s HiValueNull (HiValueNumber end) = executeSlice s 0 end
slice s (HiValueNumber start) HiValueNull = executeSlice s start $ getLength s
slice s HiValueNull HiValueNull = executeSlice s 0 $ getLength s
slice _ _ _ = throwE HiErrorInvalidArgument

executeSlice :: (HiMonad m, SequenceShell a, Convertible a) => a -> Rational -> Rational -> ExceptT HiError m HiValue
executeSlice s start end = do
    startIdx <- getInteger start
    endIdx <- getInteger end
    let adjStart = adjustIndex s startIdx
        adjEnd = adjustIndex s endIdx
    returnAsHiValue $ takeElements (adjEnd - adjStart) $ dropElements adjStart s

adjustIndex :: (SequenceShell a) => a -> Int -> Int
adjustIndex s idx
    | idx < 0 = getLengthInt s + idx
    | otherwise = idx

getByIndex :: (SequenceShell a, HiMonad m) => a -> Rational -> ExceptT HiError m HiValue
getByIndex s idx = do
    index <- getInteger idx
    if index >= 0 && index < getLengthInt s
        then return $ getIndex s index
        else return HiValueNull

getInteger :: HiMonad m => Rational -> ExceptT HiError m Int
getInteger rat = case (numerator rat, denominator rat) of
    (num, 1)
        | toInteger (minBound :: Int) <= num && num <= toInteger (maxBound :: Int) -> return $ fromInteger num
        | otherwise -> throwE HiErrorInvalidArgument
    _ -> throwE HiErrorInvalidArgument

handleBytes :: HiMonad m => HiFun -> Data.ByteString.ByteString -> ExceptT HiError m HiValue
handleBytes = \case
    HiFunDeserialise -> return . fromRight HiValueNull . deserialiseOrFail . fromStrict
    HiFunLength -> return . HiValueNumber . toRational . Data.ByteString.length
    HiFunUnpackBytes -> return . HiValueList . Data.Sequence.fromList . convertToList
    HiFunUnzip -> return . HiValueBytes . toStrict . decompress . fromStrict
    HiFunZip -> return . HiValueBytes . toStrict . compressWith (defaultCompressParams{compressLevel = bestCompression}) . fromStrict
    HiFunDecodeUtf8 -> return . either (const HiValueNull) HiValueString . decodeUtf8'
    HiFunReverse -> return . HiValueBytes . Data.ByteString.reverse
    HiFunCount -> return . countOccurrences . fmap (HiValueNumber . toRational) . Data.ByteString.unpack
    _ -> (\_ -> throwE HiErrorInvalidArgument)

handleDict :: HiMonad m => HiFun -> Data.Map.Map HiValue HiValue -> ExceptT HiError m HiValue
handleDict = \case
    HiFunInvert -> ( \dict -> (return . HiValueDict) $ Data.Map.map HiValueList $
        Data.Map.fromListWith (><) [(v, k :<| Empty) | (k, v) <- Data.Map.toList dict])
    HiFunKeys -> return . HiValueList . Data.Sequence.fromList . Data.Map.keys
    HiFunValues -> return . HiValueList . Data.Sequence.fromList . Data.Map.elems
    _ -> (\_ -> throwE HiErrorInvalidArgument)

handleList :: HiMonad m => HiFun -> Seq HiValue -> ExceptT HiError m HiValue
handleList HiFunPackBytes s = handlePackBytes s
handleList HiFunLength s = handleLength s
handleList HiFunReverse s = handleReverse s
handleList HiFunCount s = handleCount s
handleList _ _ = throwE HiErrorInvalidArgument

handlePackBytes :: HiMonad m => Seq HiValue -> ExceptT HiError m HiValue
handlePackBytes s = createByteList s []
  where
    createByteList :: HiMonad m => Seq HiValue -> [Word8] -> ExceptT HiError m HiValue
    createByteList Data.Sequence.Empty res = (return . HiValueBytes . Data.ByteString.pack) res
    createByteList (h Data.Sequence.:|> (HiValueNumber rat)) res = do
      byteVal <- getInteger rat
      if 0 <= byteVal && byteVal <= 255
        then createByteList h (Data.Bits.Extras.w8 byteVal : res)
        else throwE HiErrorInvalidArgument
    createByteList _ _ = throwE HiErrorInvalidArgument

handleLength :: HiMonad m => Seq HiValue -> ExceptT HiError m HiValue
handleLength s = return $ HiValueNumber $ toRational $ Data.Sequence.length s

handleReverse :: HiMonad m => Seq HiValue -> ExceptT HiError m HiValue
handleReverse s = return $ HiValueList $ Data.Sequence.reverse s

handleCount :: HiMonad m => Seq HiValue -> ExceptT HiError m HiValue
handleCount s = return $ countOccurrences (Data.Foldable.toList s)

handleNumber :: HiMonad m => HiFun -> Rational -> Rational -> ExceptT HiError m HiValue
handleNumber HiFunRand minVal maxVal = handleRand minVal maxVal
handleNumber HiFunRange start end = handleRange start end
handleNumber fun x y = handleArithmetic fun x y

handleRand :: HiMonad m => Rational -> Rational -> ExceptT HiError m HiValue
handleRand minVal maxVal = do
    minInt <- getInteger minVal
    maxInt <- getInteger maxVal
    return $ HiValueAction $ HiActionRand minInt maxInt

handleRange :: HiMonad m => Rational -> Rational -> ExceptT HiError m HiValue
handleRange start end = return $ HiValueList $ Data.Sequence.fromList $ Prelude.map HiValueNumber [start .. end]

handleArithmetic :: HiMonad m => HiFun -> Rational -> Rational -> ExceptT HiError m HiValue
handleArithmetic fun x y = case fun of
    HiFunAdd -> return $ HiValueNumber $ x + y
    HiFunSub -> return $ HiValueNumber $ x - y
    HiFunMul -> return $ HiValueNumber $ x * y
    HiFunDiv
        | y /= 0 -> return $ HiValueNumber $ x / y
        | otherwise -> throwE HiErrorDivideByZero
    _ -> throwE HiErrorInvalidArgument

handleString :: HiMonad m => HiFun -> Data.Text.Text -> ExceptT HiError m HiValue
handleString = \case
    HiFunLength -> return . HiValueNumber . toRational . Data.Text.length
    HiFunToLower -> return . HiValueString . Data.Text.toLower
    HiFunToUpper -> return . HiValueString . Data.Text.toUpper
    HiFunEcho -> return . HiValueAction . HiActionEcho
    HiFunReverse -> return . HiValueString . Data.Text.reverse
    HiFunEncodeUtf8 -> return . HiValueBytes . encodeUtf8
    HiFunParseTime -> return . maybe HiValueNull HiValueTime . readMaybe . Data.Text.unpack
    HiFunTrim -> return . HiValueString . Data.Text.strip
    HiFunRead -> return . HiValueAction . HiActionRead . Data.Text.unpack
    HiFunMkDir -> return . HiValueAction . HiActionMkDir . Data.Text.unpack
    HiFunChDir -> return . HiValueAction . HiActionChDir . Data.Text.unpack
    HiFunCount -> return . countOccurrences . (fmap (\c -> (HiValueString . Data.Text.pack) [c]) . Data.Text.unpack)
    _ -> (\_ -> throwE HiErrorInvalidArgument)

countOccurrences :: [HiValue] -> HiValue
countOccurrences elements =
    HiValueDict $
        Data.Map.map HiValueNumber $
            Data.Map.fromListWith (+) [(el, 1) | el <- elements]