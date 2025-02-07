module HW6.Test (
    
) where

newtype Sum a = Sum {getSum :: a}
newtype Mul a = Mul {getMul :: a}

instance Num a => Semigroup (Sum a) where
    Sum x <> Sum y = Sum (x + y)

instance Num a => Semigroup (Mul a) where
    Mul x <> Mul y = Mul (x * y)