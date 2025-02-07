{-# LANGUAGE LambdaCase #-}

module HW0.T5
  ( Nat
  , nFromNatural
  , nmult
  , nplus
  , ns
  , nToNum
  , nz
  ) where

import Numeric.Natural

type Nat a = (a -> a) -> a -> a

nz :: Nat a
nz _ a = a

ns :: Nat a -> Nat a
ns n f a = f (n f a)

nplus :: Nat a -> Nat a -> Nat a
nplus a b f = a f . b f

nmult :: Nat a -> Nat a -> Nat a
nmult a b = a . b

nFromNatural :: Natural -> Nat a
nFromNatural = \case
  0 -> nz
  a -> ns $ nFromNatural $ a - 1

nToNum :: Num a => Nat a -> a
nToNum a = a (+ 1) 0
