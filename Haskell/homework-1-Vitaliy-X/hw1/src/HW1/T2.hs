module HW1.T2
  ( N (..)
  , nplus
  , nmult
  , nsub
  , nFromNatural
  , nToNum
  , ncmp
  , nEven
  , nOdd
  , ndiv
  , nmod
  ) where

import Numeric.Natural

data N = Z | S N

nplus :: N -> N -> N
nplus Z a     = a
nplus (S b) a = S (nplus b a)

nmult :: N -> N -> N
nmult Z _     = Z
nmult _ Z     = Z
nmult b (S a) = nplus b (nmult b a)

nsub :: N -> N -> Maybe N
nsub a Z         = Just a
nsub (S b) (S a) = nsub b a
nsub _ _         = Nothing

ncmp :: N -> N -> Ordering
ncmp a b = case subResult of
  Just Z  -> EQ
  Just _  -> GT
  Nothing -> LT
  where subResult = nsub a b

nFromNatural :: Natural -> N
nFromNatural 0 = Z
nFromNatural 1 = S Z
nFromNatural a = S $ nFromNatural (a - 1)

nToNum :: Num a => N -> a
nToNum Z     = 0
nToNum (S a) = nToNum a + 1

nEven :: N -> Bool
nEven = undefined

nOdd :: N -> Bool
nOdd = undefined

ndiv :: N -> N -> N
ndiv = undefined

nmod :: N -> N -> N
nmod = undefined
