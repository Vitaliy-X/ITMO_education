{-# OPTIONS_GHC -F -pgmF hspec-discover #-}

import Test.Hspec
import HW4.T2 (parseExpr, ParseError(..))
import HW4.Types (Expr(..), Prim(..))
import HW4.T1 (Except(..))

main :: IO ()
main = hspec $ do
  describe "parseExpr" $ do
    it "parses simple expressions" $ do
      parseExpr "3.14 + 1.618 * 2" `shouldBe` Success (Op (Add (Val 3.15) (Op (Mul (Val 1.618) (Val 2.0)))))
      parseExpr "2 * (1 + 3)" `shouldBe` Success (Op (Mul (Val 2.0) (Op (Add (Val 1.0) (Val 3.0)))))
    it "returns error for invalid input" $ do
      parseExpr "24 + Hello" `shouldBe` Error (ErrorAtPos 3)