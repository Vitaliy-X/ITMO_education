{-# LANGUAGE LambdaCase #-}
{-# LANGUAGE TypeApplications #-}

module HW5.Pretty (
    prettyValue,
)
where

import Data.ByteString (unpack)
import HW5.Base (HiAction (..), HiValue (..))
import qualified Data.Map
import qualified Data.ByteString
import qualified Data.Foldable
import Data.Ratio (denominator, numerator)
import Data.Scientific (FPFormat (..), formatScientific, fromRationalRepetendUnlimited)
import qualified Data.Sequence
import qualified Data.Text
import qualified Data.Time
import Data.Word (Word8)
import Numeric (showHex)
import Prettyprinter (Doc, Pretty (pretty), (<+>), comma, hsep, punctuate)
import Prettyprinter.Render.Terminal (AnsiStyle)

prettyValue :: HiValue -> Doc AnsiStyle
prettyValue = \case
    HiValueNumber n -> prettyNumber n
    HiValueFunction f -> prettyFunction f
    HiValueBool b -> prettyBool b
    HiValueNull -> prettyNull
    HiValueString s -> prettyString s
    HiValueAction a -> prettyAction a
    HiValueList l -> prettyList l
    HiValueBytes b -> prettyBytes b
    HiValueTime t -> prettyTime t
    HiValueDict d -> prettyDict d

prettyRational :: Integer -> Integer -> Doc AnsiStyle
prettyRational n d = pretty n <> pretty "/" <> pretty d

prettyNumber :: Rational -> Doc AnsiStyle
prettyNumber r =
  case fromRationalRepetendUnlimited r of
    (_, Just _) -> case quotRem (numerator r) (denominator r) of
       (n, 0) -> pretty n
       (0, d) -> prettyRational d $ denominator r
       (n, d) -> if d > 0
        then pretty n <+> pretty "+" <+> prettyRational d (denominator r)
        else pretty n <+> pretty "-" <+> prettyRational (-d) (denominator r)
    (res, _) -> case quotRem (numerator r) (denominator r) of
      (n, 0) -> pretty n
      _      -> pretty (formatScientific Fixed Nothing res)

prettyBool :: Bool -> Doc AnsiStyle
prettyBool = \case
  True  -> pretty "true"
  False -> pretty "false"

prettyFunction :: Show a => a -> Doc AnsiStyle
prettyFunction f = pretty $ show f

prettyNull :: Doc AnsiStyle
prettyNull = pretty "null"

prettyString :: Data.Text.Text -> Doc AnsiStyle
prettyString = pretty . show

prettyAction :: HiAction -> Doc AnsiStyle
prettyAction = \case
    HiActionRead path -> pretty $ "read(" <> "\"" <> path <> "\"" <> ")"
    HiActionWrite path bytes ->
        pretty "write("
            <> pretty ("\"" <> path <> "\"")
            <> pretty ", "
            <> prettyBytes bytes
            <> pretty ")"
    HiActionMkDir path -> pretty $ "mkdir(" <> "\"" <> path <> "\"" <> ")"
    HiActionChDir path -> pretty $ "cd(" <> "\"" <> path <> "\"" <> ")"
    HiActionCwd -> pretty "cwd"
    HiActionNow -> pretty "now"
    HiActionRand a b -> pretty $ "rand(" <> show a <> ", " <> show b <> ")"
    HiActionEcho t -> pretty $ "echo(" <> show t <> ")"

prettyList :: Data.Sequence.Seq HiValue -> Doc AnsiStyle
prettyList l =
    hsep
        ( pretty "["
            : punctuate comma (Prelude.map prettyValue (Data.Foldable.toList l))
        )
        <> pretty " ]"

prettyBytes :: Data.ByteString.ByteString -> Doc AnsiStyle
prettyBytes =
    pretty
        . ( \bytes ->
                "[# "
                    <> Data.Foldable.foldr'
                        ((<>) . formatHex)
                        "#]"
                        (Data.ByteString.unpack bytes)
          )
  where
    formatHex :: Word8 -> String
    formatHex byte
        | byte < 16 = "0" ++ showHex byte " "
        | otherwise = showHex byte " "

prettyTime :: Data.Time.UTCTime -> Doc AnsiStyle
prettyTime t = pretty $ "parse-time(\"" ++ show t ++ "\")"

prettyDict :: Data.Map.Map HiValue HiValue -> Doc AnsiStyle
prettyDict dict =
    hsep
        ( pretty "{"
            : punctuate
                comma
                (Prelude.map (\(a, b) -> prettyValue a <> pretty ": " <> prettyValue b) $ Data.Map.toList dict)
        )
        <> pretty " }"