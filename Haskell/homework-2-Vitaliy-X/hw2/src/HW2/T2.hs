module HW2.T2
  ( joinWith
  , splitOn
  ) where

import Data.List.NonEmpty (NonEmpty(..))

splitOn :: Eq a => a -> [a] -> NonEmpty [a]
splitOn sep = foldr f ([] :| [])
  where
    f c (x :| xs)
      | c == sep = [] :| (x : xs)
      | otherwise = (c : x) :| xs

joinWith :: a -> NonEmpty [a] -> [a]
joinWith sep (x :| xs) = x ++ foldr (\s acc -> sep : s ++ acc) [] xs
