module HW6.T1
  ( BucketsArray
  , CHT (..)
  
  , newCHT
  , getCHT
  , putCHT
  , sizeCHT

  , initCapacity
  , loadFactor
  ) where

import Control.Concurrent.Classy (MonadConc, STM, atomically)
import Control.Concurrent.Classy.STM (MonadSTM, TArray, TVar, newTVar, readTVar, writeTVar)
import Data.Array.MArray (newArray, readArray, writeArray, getElems)
import Data.Hashable (Hashable, hash)
import Control.Monad (when, forM_)
import Data.Array.Base (getNumElements)
import Data.Maybe (isNothing)

initCapacity :: Int
initCapacity = 16

loadFactor :: Double
loadFactor = 0.75

type Bucket k v = [(k, v)]
type BucketsArray stm k v = TArray stm Int (Bucket k v)

data CHT stm k v = CHT
  { chtBuckets :: TVar stm (BucketsArray stm k v)
  , chtSize    :: TVar stm Int
  }

newCHT :: MonadConc m => m (CHT (STM m) k v)
newCHT = atomically $ do
  initialArray <- newArray (0, initCapacity - 1) []
  bucketVar <- newTVar initialArray
  sizeVar <- newTVar 0
  return CHT {chtBuckets = bucketVar, chtSize = sizeVar}

getCHT
  :: ( MonadConc m
     , Hashable k
     )
  => k
  -> CHT (STM m) k v
  -> m (Maybe v)
getCHT key cht = atomically $ do
  bucketsVar <- readTVar (chtBuckets cht)
  numBuckets <- getNumElements bucketsVar
  bucketIndex <- readArray bucketsVar (hash key `mod` numBuckets)
  return $ lookup key bucketIndex

putCHT
   :: ( MonadConc m
      , Hashable k
      )
   => k
   -> v
   -> CHT (STM m) k v
   -> m ()
putCHT k v cht = atomically $ do
  buckets <- readTVar (chtBuckets cht)
  currentSize <- readTVar (chtSize cht)
  capacity <- getNumElements buckets
  let shouldResize = fromIntegral currentSize + 1 >= fromIntegral capacity * loadFactor
  when shouldResize $ do
    resizedBuckets <- resizeBuckets buckets
    writeTVar (chtBuckets cht) resizedBuckets
  
  updatedBuckets <- readTVar (chtBuckets cht)
  modifyBucket k v updatedBuckets cht
  return ()

resizeBuckets
  :: (MonadSTM stm, Hashable k)
  => BucketsArray stm k v
  -> stm (BucketsArray stm k v)
resizeBuckets buckets = do
  currentCapacity <- getNumElements buckets
  let newCapacity = currentCapacity * 2
  newBucketsArray <- newArray (0, newCapacity - 1) []
  oldBuckets <- getElems buckets
  let allElements = concat oldBuckets
  forM_ allElements $ \(k', v') -> do
    let newIndex = hash k' `mod` newCapacity
    currentBucket <- readArray newBucketsArray newIndex
    writeArray newBucketsArray newIndex ((k', v') : currentBucket)
  return newBucketsArray

modifyBucket
  :: (MonadSTM stm, Hashable k)
  => k
  -> v
  -> BucketsArray stm k v
  -> CHT stm k v
  -> stm ()
modifyBucket key value buckets cht = do
  currentCapacity <- getNumElements buckets
  let bucketIndex = hash key `mod` currentCapacity
  currentBucket <- readArray buckets bucketIndex
  let updatedBucket = case lookup key currentBucket of
        Just _  -> map (\(k', v') -> if k' == key then (k', value) else (k', v')) currentBucket
        Nothing -> (key, value) : currentBucket
  writeArray buckets bucketIndex updatedBucket
  when (isNothing (lookup key currentBucket)) $ do
    currentSize <- readTVar (chtSize cht)
    writeTVar (chtSize cht) (currentSize + 1)

-- sizeCHT :: MonadConc m => CHT (STM m) k v -> m Int
sizeCHT :: MonadConc m => CHT (STM m) k v -> m Int
sizeCHT c = atomically $ readTVar $ chtSize c
