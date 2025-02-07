module HW2.T1
  ( Tree (..)
  , tfoldr
  , treeToList
  ) where

data Tree a = Leaf | Branch Int (Tree a) a (Tree a)
  deriving (Show)

tfoldr :: (a -> b -> b) -> b -> Tree a -> b
tfoldr _ accum Leaf = accum
tfoldr f accum (Branch _ l x r) = tfoldr f (f x $ tfoldr f accum r) l

treeToList :: Tree a -> [a]
treeToList = tfoldr (:) []
