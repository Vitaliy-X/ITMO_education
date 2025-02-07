{-# LANGUAGE LambdaCase #-}

module HW5.Parser (
    HW5.Parser.parse,
)
where

import Control.Monad.Combinators.Expr (Operator (InfixL, InfixN, InfixR), makeExprParser)
import qualified Data.ByteString
import Data.Char (isAlpha, isAlphaNum)
import qualified Data.Text
import Data.Void (Void)
import Data.Word (Word8)
import HW5.Base (HiAction (..), HiExpr (..), HiFun (..), HiValue (..))
import Numeric (readHex)
import Text.Megaparsec
import Text.Megaparsec.Char (char, hexDigitChar, space1, string)
import Text.Megaparsec.Char.Lexer (charLiteral, scientific, signed, skipBlockComment, skipLineComment, space, symbol)

parse :: String -> Either (ParseErrorBundle String Void) HiExpr
parse = runParser (expressionParser <* eof) ""

type Parser = Parsec Void String

-- implementation of combinatorial parser
expressionParser :: Parser HiExpr
expressionParser = makeExprParser parseHiExpr operatorTable
  where
    parseHiExpr :: Parser HiExpr
    parseHiExpr = do
        _ <- ignoreSpaces
        result <- parseDot =<< parseArguments =<< (try parseValue <|> parseBrackets)
        _ <- ignoreSpaces
        return result

    parseBrackets :: Parser HiExpr
    parseBrackets = do
        _ <- char '('
        result <- expressionParser
        _ <- char ')'
        return result

ignoreSpaces :: Parser ()
ignoreSpaces =
    space
        space1
        (skipLineComment "//")
        (skipBlockComment "/*" "*/")

operatorTable :: [[Operator Parser HiExpr]]
operatorTable =
    [[ leftAssoc "*" HiFunMul
    , InfixL . try $ binaryOperator HiFunDiv <$ (symbol ignoreSpaces "/" <* notFollowedBy (char '='))
    ],
    [ leftAssoc "+" HiFunAdd
    , leftAssoc "-" HiFunSub
    ],
    [ nonAssoc "==" HiFunEquals
    , nonAssoc "/=" HiFunNotEquals
    , nonAssoc "<=" HiFunNotGreaterThan
    , nonAssoc ">=" HiFunNotLessThan
    , nonAssoc "<" HiFunLessThan
    , nonAssoc ">" HiFunGreaterThan
    ],
    [ rightAssoc "||" HiFunOr
    ],
    [ rightAssoc "&&" HiFunAnd
    ]]
  where
    leftAssoc, rightAssoc, nonAssoc :: String -> HiFun -> Operator Parser HiExpr
    leftAssoc = createOperator InfixL
    rightAssoc = createOperator InfixR
    nonAssoc = createOperator InfixN

    createOperator :: (Parser (HiExpr -> HiExpr -> HiExpr) -> Operator Parser HiExpr) -> String -> HiFun -> Operator Parser HiExpr
    createOperator assoc opStr fun = assoc (binaryOperator fun <$ symbol ignoreSpaces opStr)

    binaryOperator :: HiFun -> HiExpr -> HiExpr -> HiExpr
    binaryOperator fun a b = HiExprApply (HiExprValue $ HiValueFunction fun) [a, b]

parseArguments :: HiExpr -> Parser HiExpr
parseArguments expr = try (parseDot =<< ignoreSpaces *> withArgs) <|> pure expr
  where
    withArgs = HiExprApply expr <$> parseArgumentValues '(' expressionParser ')'

parseArgumentValues :: Char -> Parser a -> Char -> Parser [a]
parseArgumentValues open parser close = do
    let parseSeparated = parser `sepBy` (char ',' <* ignoreSpaces)
    char open *> ignoreSpaces *> parseSeparated <* char close

parseValue :: Parser HiExpr
parseValue =
    try parseList
        <|> try parseDict
        <|> HiExprValue
            <$> ( try parseAction
                    <|> try parseBytes
                    <|> try parseFunction
                    <|> try parseNumber
                    <|> try parseBoolean
                    <|> try parseNull
                    <|> try parseString
                )

parseList :: Parser HiExpr
parseList = HiExprApply (HiExprValue $ HiValueFunction HiFunList) <$> parseArgumentValues '[' expressionParser ']'

parseDict :: Parser HiExpr
parseDict = HiExprDict <$> parseArgumentValues '{' parseDictPair '}'
  where
    parseDictPair :: Parser (HiExpr, HiExpr)
    parseDictPair = do
        key <- expressionParser
        _ <- char ':'
        value <- expressionParser
        pure (key, value)

parseAction :: Parser HiValue
parseAction = HiValueAction <$> choice
            [HiActionNow <$ string "now", HiActionCwd <$ string "cwd"]

parseBytes :: Parser HiValue
parseBytes = do
    _ <- string "[#" *> ignoreSpaces
    bytes <- parseByte `sepEndBy` char ' '
    _ <- string "#]"
    return $ HiValueBytes $ Data.ByteString.pack bytes
  where
    parseByte :: Parser Word8
    parseByte = do
        hexDigits <- count 2 hexDigitChar
        case readHex hexDigits of
            [(byte, "")] | byte >= 0 && byte <= 255 -> return byte
            _ -> empty

parseFunction :: Parser HiValue
parseFunction = HiValueFunction <$> choice (Prelude.map (\x -> x <$ string (show x)) [HiFunDiv ..])

parseNumber :: Parser HiValue
parseNumber = HiValueNumber . toRational <$> signed ignoreSpaces scientific

parseBoolean :: Parser HiValue
parseBoolean = choice [HiValueBool True <$ string "true", HiValueBool False <$ string "false"]

parseNull :: Parser HiValue
parseNull = HiValueNull <$ string "null"

parseString :: Parser HiValue
parseString = HiValueString . Data.Text.pack <$> (char '\"' *> manyTill charLiteral (char '\"'))

parseDot :: HiExpr -> Parser HiExpr
parseDot initialExpr = do
    segments <- parseSegments
    exprWithSegments <- case segments of
        Nothing -> handleRun initialExpr
        Just segs -> parseDot $ HiExprApply initialExpr [combineSegments segs]
    parseArguments exprWithSegments

parseSegments :: Parser (Maybe [String])
parseSegments = optional $ char '.' >> sepBy1 parseIdentifier (char '-')

parseIdentifier :: Parser String
parseIdentifier = (:) <$> satisfy isAlpha <*> many (satisfy isAlphaNum)

combineSegments :: [String] -> HiExpr
combineSegments = HiExprValue . HiValueString . Data.Text.pack . foldr1 (\x y -> x ++ "-" ++ y)

handleRun :: HiExpr -> Parser HiExpr
handleRun expr = do
    runMarker <- optional $ char '!'
    maybe (return expr) (const $ parseDot $ HiExprRun expr) runMarker