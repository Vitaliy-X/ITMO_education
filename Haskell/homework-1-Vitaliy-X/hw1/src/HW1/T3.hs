module HW1.T3
  ( Tree (..)
  , tsize
  , tdepth
  , tmember
  , tinsert
  , tFromList
  ) where

type Meta = Int

data Tree a = Leaf | Branch Meta (Tree a) a (Tree a)
  deriving (Show)

tsize :: Tree a -> Int
tsize Leaf                = 0
tsize (Branch size _ _ _) = size

tdepth :: Tree a -> Int
tdepth Leaf             = 0
tdepth (Branch _ l _ r) = 1 + max (tdepth l) (tdepth r)

tmember :: Ord a => a -> Tree a -> Bool
tmember key tree = case tree of
    Leaf -> False
    Branch _ left value right
        | key == value -> True
        | key < value -> tmember key left
        | otherwise -> tmember key right

mkBranch :: Tree a -> a -> Tree a -> Tree a
mkBranch left value right = Branch newMeta left value right
  where
    newMeta = 1 + tsize left + tsize right

tinsert :: Ord a => a -> Tree a -> Tree a
tinsert a Leaf = mkBranch Leaf a Leaf
tinsert a (Branch meta left value right)
    | a < value = mkBranch (tinsert a left) value right
    | a > value = mkBranch left value (tinsert a right)
    | otherwise = Branch meta left value right

tFromList :: Ord a => [a] -> Tree a
tFromList = foldr tinsert Leaf
