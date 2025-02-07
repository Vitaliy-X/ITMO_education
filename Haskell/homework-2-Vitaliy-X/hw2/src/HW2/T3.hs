module HW2.T3
  ( epart
  , mcat
  ) where

import Data.Maybe (fromMaybe)

mcat :: Monoid a => [Maybe a] -> a
mcat = foldMap (fromMaybe mempty)

epart :: (Monoid a, Monoid b) => [Either a b] -> (a, b)
epart = foldMap f
  where
    f (Left left) = (left, mempty)
    f (Right right) = (mempty, right)
