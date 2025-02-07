module HW3.T4
  ( State (..)
  , Prim (..)
  , Expr (..)
  , mapState
  , wrapState
  , joinState
  , modifyState
  , eval
  ) where

import qualified Control.Monad
import HW3.T1

newtype State s a = S { runS :: s -> Annotated s a }

mapState :: (a -> b) -> State s a -> State s b
mapState f state = S $ \s -> mapAnnotated f (runS state s)

wrapState :: a -> State s a
wrapState a = S (a :#)

joinState :: State s (State s a) -> State s a
joinState state = S $ \s -> let (result :# newState) = runS state s in runS result newState

modifyState :: (s -> s) -> State s ()
modifyState f = S (\s -> () :# f s)

instance Functor (State s) where
  fmap = mapState

instance Applicative (State s) where
  pure = wrapState
  f <*> g = Control.Monad.ap f g

instance Monad (State s) where
  m >>= f = joinState (fmap f m)

data Prim a =
    Add a a
  | Sub a a
  | Mul a a
  | Div a a
  | Abs a
  | Sgn a
  deriving Show

data Expr = Val Double | Op (Prim Expr)
  deriving Show

instance Num Expr where
  x + y = Op (Add x y)
  x - y = Op (Sub x y)
  x * y = Op (Mul x y)
  abs x = Op (Abs x)
  signum x = Op (Sgn x)
  fromInteger x = Val (fromInteger x)

instance Fractional Expr where
  x / y = Op (Div x y)
  fromRational x = Val (fromRational x)

eval :: Expr -> State [Prim Double] Double
eval expression = evalWithMonad expression $ \operation -> do
  modifyState (operation :)
  pure (calculate operation)

calculate :: (Fractional f) => Prim f -> f
calculate operation = case operation of
  Add a b -> a + b
  Sub a b -> a - b
  Mul a b -> a * b
  Div a b -> a / b
  Abs a -> abs a
  Sgn a -> signum a

evalWithMonad :: Expr -> (Prim Double -> State [Prim Double] Double) -> State [Prim Double] Double
evalWithMonad (Val value) _ = pure value
evalWithMonad (Op op) _ = case op of
  Add a b -> binaryOp Add a b
  Sub a b -> binaryOp Sub a b
  Mul a b -> binaryOp Mul a b
  Div a b -> binaryOp Div a b
  Abs x -> unary Abs x
  Sgn x -> unary Sgn x

binaryOp :: (Double -> Double -> Prim Double) -> Expr -> Expr -> State [Prim Double] Double
binaryOp constructor a b = do
  resultA <- eval a
  resultB <- eval b
  let operation = constructor resultA resultB
  modifyState (operation :)
  pure (calculate operation)

unary :: (Double -> Prim Double) -> Expr -> State [Prim Double] Double
unary toPrim expr = do
  result <- eval expr
  let operation = toPrim result
  modifyState (operation :)
  pure (calculate operation)