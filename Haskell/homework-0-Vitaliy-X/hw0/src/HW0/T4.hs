module HW0.T4
  ( fac
  , fib
  , map'
  , repeat'
  ) where

import Numeric.Natural (Natural)
import Data.Function (fix)

repeat' :: a -> [a]
repeat' a = fix (a :)

map' :: (a -> b) -> [a] -> [b]
map' = fix (\rec f l -> case l of
  [] -> []
  (x : xs) -> f x : rec f xs)

fib :: Natural -> Natural
fib = fix (\_ n -> fibRec 0 1 n)
  where
    fibRec :: Natural -> Natural -> Natural -> Natural
    fibRec a b n =
      case n of
        0 -> a
        1 -> b
        _ -> fibRec b (a + b) (n - 1)

fac :: Natural -> Natural
fac = fix (\rec x -> if x <= 1 then 1 else x * rec (x - 1))
