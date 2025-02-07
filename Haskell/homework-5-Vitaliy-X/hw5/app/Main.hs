module Main (
    main,
)
where

import Control.Monad.IO.Class
import Data.Set
import HW5.Base
import HW5.Evaluator
import HW5.Parser
import HW5.Pretty
import HW5.Action
import System.Console.Haskeline

main :: IO ()
main = runInputT defaultSettings repl
  where
    repl :: InputT IO ()
    repl = do
        userInput <- getInputLine "hi> "
        case userInput of
            Nothing -> return ()
            Just ":q" -> return ()
            Just "" -> repl
            Just input -> handleInput input >> repl

    handleInput :: String -> InputT IO ()
    handleInput input = do
        output <- getExternalPrint
        case parse input of
            Left err -> liftIO $ output $ show err
            Right expr -> evaluateAndPrint expr output

    evaluateAndPrint :: HiExpr -> (String -> IO ()) -> InputT IO ()
    evaluateAndPrint expr output = do
        let result = eval expr
        let permissions = fromList [AllowRead, AllowWrite, AllowTime]
        evalResult <- liftIO $ runHIO result permissions
        liftIO $ case evalResult of
            Left err -> output $ show err
            Right val -> output $ show $ prettyValue val