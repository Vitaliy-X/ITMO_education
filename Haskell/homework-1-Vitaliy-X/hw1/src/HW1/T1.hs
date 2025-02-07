module HW1.T1
  ( Day (..)
  , afterDays
  , daysToParty
  , isWeekend
  , nextDay
  ) where

import Numeric.Natural (Natural)

data Day
  = Monday
  | Tuesday
  | Wednesday
  | Thursday
  | Friday
  | Saturday
  | Sunday
  deriving Show

nextDay :: Day -> Day
nextDay day = case day of
    Monday -> Tuesday
    Tuesday -> Wednesday
    Wednesday -> Thursday
    Thursday -> Friday
    Friday -> Saturday
    Saturday -> Sunday
    Sunday -> Monday

afterDays :: Natural -> Day -> Day
afterDays 0 current     = current
afterDays count current = afterDays (count-1) (nextDay current)

isWeekend :: Day -> Bool
isWeekend day = case day of
    Saturday -> True
    Sunday -> True
    _ -> False

daysToParty :: Day -> Natural
daysToParty day = case day of
    Friday -> 0
    _ -> daysToParty (nextDay day) + 1