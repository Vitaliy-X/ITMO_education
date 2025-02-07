{-# LANGUAGE KindSignatures #-}
{-# LANGUAGE DataKinds #-}
{-# LANGUAGE TypeFamilies #-}
{-# LANGUAGE TypeOperators #-}
{-# LANGUAGE UndecidableInstances #-}

module HW6.T2
  ( TSet

  , Contains
  , Add
  , Delete
  ) where

import GHC.TypeLits

type TSet = [Symbol]

type family Contains (name :: Symbol) (set :: TSet) :: Bool where
  Contains n (n ': _) = 'True
  Contains n (_ ': rest) = Contains n rest
  Contains _ _ = 'False
  
type family Delete (name :: Symbol) (set :: TSet) :: TSet where
  Delete _ '[] = '[]
  Delete n (n ': rest) = rest
  Delete n (x ': rest) = x ': Delete n rest

type family Add (v :: Symbol) (set :: TSet) :: TSet where
  Add n s = n ': Delete n s
