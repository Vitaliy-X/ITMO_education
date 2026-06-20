select SessionId, count(distinct Letter) as Opened from Runs
group by SessionId;
