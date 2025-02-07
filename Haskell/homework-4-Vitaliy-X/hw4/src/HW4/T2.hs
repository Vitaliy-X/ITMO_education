{-# LANGUAGE DerivingStrategies #-}
{-# LANGUAGE GeneralisedNewtypeDeriving #-}
{-# LANGUAGE BlockArguments #-}

module HW4.T2
  ( ParseError (..)
  , runP
  , pChar
  , parseError
  , parseExpr
  ) where

import Numeric.Natural (Natural)
import Control.Applicative
import Control.Monad
import Data.Char (isDigit, isSpace)

import HW4.Types
import HW4.T1 (ExceptState(..))

newtype ParseError = ErrorAtPos Natural
  deriving Show

newtype Parser a = P (ExceptState ParseError (Natural, String) a)
  deriving newtype (Functor, Applicative, Monad)

runP :: Parser a -> String -> Except ParseError a
runP (P parser) s = case runES parser (0, s) of
  Success (x :# _) -> Success x
  Error err -> Error err

-- Just an example of parser that may be useful
-- in the implementation of 'parseExpr'
pChar :: Parser Char
pChar = P $ ES $ \(pos, s) ->
  case s of
    []     -> Error (ErrorAtPos pos)
    (c:cs) -> Success (c :# (pos + 1, cs))

parseError :: Parser a
parseError = P $ ES $ \(pos, _) -> Error (ErrorAtPos pos)

instance Alternative Parser where
  empty = parseError
  (P parserA) <|> (P parserB) = P $ ES $ \state ->
      case runES parserA state of
        Error _ -> runES parserB state
        success -> success

-- No metohds
instance MonadPlus Parser

-- LL(1) grammar
-- Expr
-- └── E
--     ├── T
--     │   ├── F
--     │   │   ├── double number
--     │   │   └── (Expr)
--     │   └── T'
--     │       ├── *FT'
--     │       ├── /FT'
--     │       └── ε
--     └── E'
--         ├── +TE'
--         ├── -TE'
--         └── ε
parseExpr :: String -> Except ParseError Expr
parseExpr = runP (expressionParser <* skipWhiteSpaces <* endOfInput)

endOfInput :: Parser ()
endOfInput = P $ ES (\state ->
  case snd state of
    [] -> Success (() :# state)
    _  -> Error (ErrorAtPos (fst state)))

skipWhiteSpaces :: Parser String
skipWhiteSpaces = many $ matchFunc isSpace

applyFunc :: Expr -> (Expr -> Expr) -> Expr
applyFunc expr func = func expr

expressionParser :: Parser Expr
expressionParser = parseE

parseE :: Parser Expr
parseE = liftA2 applyFunc parseT parseE'

parseE' :: Parser (Expr -> Expr)
parseE' = skipWhiteSpaces *> (parseRest <|> pure id)
  where
    parseRest = liftA3 buildExpression (matchChar '+' <|> matchChar '-') parseT parseE'

parseT :: Parser Expr
parseT = liftA2 applyFunc parseF parseT'

parseT' :: Parser (Expr -> Expr)
parseT' = skipWhiteSpaces *> (parseRest <|> pure id)
  where
    parseRest = liftA3 buildExpression (matchChar '*' <|> matchChar '/') parseF parseT'

parseF :: Parser Expr
parseF = skipWhiteSpaces *> (parseNumber <|> parseBrackets)
  where
    parseNumber = Val <$> parseDouble
    parseBrackets = (matchChar '(' *> expressionParser) <* skipWhiteSpaces <* matchChar ')'

parseDouble :: Parser Double
parseDouble = skipWhiteSpaces *> liftA2 combineDouble parseIntegerString parseFraction

parseFraction :: Parser String
parseFraction = (matchChar '.' *> parseIntegerString) <|> pure []

parseIntegerString :: Parser String
parseIntegerString = some $ matchFunc isDigit

combineDouble :: String -> String -> Double
combineDouble intPart fracPart = read (intPart ++ "." ++ fracPart)

buildExpression :: Char -> Expr -> (Expr -> Expr) -> Expr -> Expr
buildExpression op leftExpr rightFunc prevExpr = rightFunc $
  case op of
    '+' -> Op (Add prevExpr leftExpr)
    '-' -> Op (Sub prevExpr leftExpr)
    '*' -> Op (Mul prevExpr leftExpr)
    '/' -> Op (Div prevExpr leftExpr)
    _ -> error "Unknown operator"

matchFunc :: (Char -> Bool) -> Parser Char
matchFunc predicate = mfilter predicate pChar

matchChar :: Char -> Parser Char
matchChar c = matchFunc (== c)
