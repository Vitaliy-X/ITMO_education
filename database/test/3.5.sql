update Runs set Accepted = 1
where Runs.RunId in (
    select Runs.RunId from (
        select SessionId, max(SubmitTime) as SubmitTime from Runs
        where Accepted = 0 group by SessionId
    ) sq
    join Runs on sq.SessionId = Runs.SessionId and sq.SubmitTime = Runs.SubmitTime
);
