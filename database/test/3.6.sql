insert into Runs (RunId, SessionId, Letter, SubmitTime, Accepted)
select r.SessionId, r.Letter, max(r.SubmitTime) + 1, 1 from Runs r
    join Sessions s on r.SessionId = s.SessionId
    where s.ContestId = :ContestId and (r.SessionId, r.Letter) in (
        select r2.SessionId, r2.Letter from Runs r2
        join Sessions s2 on r2.SessionId = s2.SessionId
            where s2.ContestId = :ContestId
        group by r2.SessionId, r2.Letter
        having sum(r2.Accepted) = 0
    )
group by r.SessionId, r.Letter;
