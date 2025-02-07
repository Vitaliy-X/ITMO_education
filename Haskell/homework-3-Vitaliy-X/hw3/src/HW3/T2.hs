module HW3.T2
  ( distOption
  , wrapOption
  , distPair
  , wrapPair
  , distQuad
  , wrapQuad
  , distAnnotated
  , wrapAnnotated
  , distExcept
  , wrapExcept
  , distPrioritised
  , wrapPrioritised
  , distStream
  , wrapStream
  , distList
  , wrapList
  , distFun
  , wrapFun
  , concatList
  ) where

import HW3.T1

distOption :: (Option a, Option b) -> Option (a, b)
distOption (Some a, Some b) = Some (a, b)
distOption _ = None

wrapOption :: a -> Option a
wrapOption = Some

distPair :: (Pair a, Pair b) -> Pair (a, b)
distPair (P x1 y1, P x2 y2) = P (x1, x2) (y1, y2)

wrapPair :: a -> Pair a
wrapPair a = P a a

distQuad :: (Quad a, Quad b) -> Quad (a, b)
distQuad (Q a1 b1 c1 d1, Q a2 b2 c2 d2) = Q (a1, a2) (b1, b2) (c1, c2) (d1, d2)

wrapQuad :: a -> Quad a
wrapQuad a = Q a a a a

distAnnotated :: Semigroup e => (Annotated e a, Annotated e b) -> Annotated e (a, b)
distAnnotated (a :# e1, b :# e2) = (a, b) :# (e1 <> e2)

wrapAnnotated :: Monoid e => a -> Annotated e a
wrapAnnotated a = a :# mempty

distExcept :: (Except e a, Except e b) -> Except e (a, b)
distExcept (Success a, Success b) = Success (a, b)
distExcept (Error e, _) = Error e
distExcept (_, Error e) = Error e

wrapExcept :: a -> Except e a
wrapExcept = Success

distPrioritised :: (Prioritised a, Prioritised b) -> Prioritised (a, b)
distPrioritised (Low a, Medium b) = Medium (a, b)
distPrioritised (Low a, Low b) = Low (a, b)
distPrioritised (Medium a, Medium b) = Medium (a, b)
distPrioritised (Medium a, Low b) = Medium (a, b)
distPrioritised (Medium a, High b) = High (a, b)
distPrioritised (Low a, High b) = High (a, b)
distPrioritised (High a, High b) = High (a, b)
distPrioritised (High a, Medium b) = High (a, b)
distPrioritised (High a, Low b) = High (a, b)

wrapPrioritised :: a -> Prioritised a
wrapPrioritised = Low

distStream :: (Stream a, Stream b) -> Stream (a, b)
distStream (a :> aRest, b :> bRest) = (a, b) :> distStream (aRest, bRest)

wrapStream :: a -> Stream a
wrapStream a = a :> wrapStream a

distList :: (List a, List b) -> List (a, b)
distList (Nil, _) = Nil
distList (_, Nil) = Nil
distList (a :. rest, list) = makePList a list `concatList` distList (rest, list)
  where
    makePList :: a -> List b -> List (a, b)
    makePList _ Nil = Nil
    makePList item (b :. bs) = (item, b) :. makePList item bs

concatList :: List a -> List a -> List a
concatList Nil b = b
concatList (a :. aTail) b = a :. (aTail `concatList` b)

wrapList :: a -> List a
wrapList a = a :. Nil

distFun :: (Fun i a, Fun i b) -> Fun i (a, b)
distFun (F f, F g) = F (\i -> (f i, g i))

wrapFun :: a -> Fun i a
wrapFun a = F (const a)
