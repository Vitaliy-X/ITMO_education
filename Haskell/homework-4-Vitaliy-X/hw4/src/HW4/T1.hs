module HW4.T1
  ( EvaluationError (..)
  , ExceptState (..)
  , mapExceptState
  , wrapExceptState
  , joinExceptState
  , modifyExceptState
  , throwExceptState
  , eval
  , mapExcept
  , mapAnnotated
  ) where

import           Control.Monad
import           HW4.Types

newtype ExceptState e s a = ES { runES :: s -> Except e (Annotated s a) }

mapExceptState :: (a -> b) -> ExceptState e s a -> ExceptState e s b
mapExceptState f es = ES $ \s -> 
  let result = runES es s
  in mapExcept (mapAnnotated f) result

wrapExceptState :: a -> ExceptState e s a
wrapExceptState a = ES $ \s -> let annotated = a :# s in Success annotated

joinExceptState :: ExceptState e s (ExceptState e s a) -> ExceptState e s a
joinExceptState es = ES $ \s ->
  case runES es s of
    Success (innerES :# newState) -> runES innerES newState
    Error err -> Error err

modifyExceptState :: (s -> s) -> ExceptState e s ()
modifyExceptState f = ES $ \s -> let newState = f s in Success (() :# newState)

throwExceptState :: e -> ExceptState e s a
throwExceptState e = ES $ const (Error e)

instance Functor (ExceptState e s) where
  fmap = mapExceptState

instance Applicative (ExceptState e s) where
  pure = wrapExceptState
  p <*> q = Control.Monad.ap p q

instance Monad (ExceptState e s) where
  m >>= f = joinExceptState $ fmap f m

data EvaluationError = DivideByZero
  deriving Show

eval :: Expr -> ExceptState EvaluationError [Prim Double] Double
eval expr = evalWithMonad expr $ \evalOperation -> do
  case evalOperation of
    Div _ 0 -> throwExceptState DivideByZero
    _ -> modifyExceptState $ (:) evalOperation
  pure $ calculate evalOperation

-- ===========

calculate :: (Fractional f) => Prim f -> f
calculate operation = case operation of
  Add a b -> a + b
  Sub a b -> a - b
  Mul a b -> a * b
  Div a b -> a / b
  Abs a -> abs a
  Sgn a -> signum a

evalWithMonad :: Expr -> (Prim Double -> ExceptState EvaluationError [Prim Double] Double) -> ExceptState EvaluationError [Prim Double] Double
evalWithMonad (Val value) _ = wrapExceptState value
evalWithMonad (Op op) evalOp = case op of
  Add a b -> binaryOp Add a b evalOp
  Sub a b -> binaryOp Sub a b evalOp
  Mul a b -> binaryOp Mul a b evalOp
  Div a b -> binaryOp Div a b evalOp
  Abs x -> unary Abs x evalOp
  Sgn x -> unary Sgn x evalOp

binaryOp :: (Double -> Double -> Prim Double) -> Expr -> Expr -> (Prim Double -> ExceptState EvaluationError [Prim Double] Double) -> ExceptState EvaluationError [Prim Double] Double
binaryOp constructor a b evalOp = do
  resultA <- eval a
  resultB <- eval b
  let operation = constructor resultA resultB
  evalOp operation

unary :: (Double -> Prim Double) -> Expr -> (Prim Double -> ExceptState EvaluationError [Prim Double] Double) -> ExceptState EvaluationError [Prim Double] Double
unary toPrim expr evalOp = do
  result <- eval expr
  let operation = toPrim result
  evalOp operation