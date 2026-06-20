update Runs set Accepted = 1
where Runs.RunId in (
    select Runs.RunId from (
        select SessionId, max(SubmitTime) as SubmitTime from Runs
        group by SessionId
    ) sq
    join Runs on sq.SessionId = Runs.SessionId and sq.SubmitTime = Runs.SubmitTime
);
