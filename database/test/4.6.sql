select s.ContestId, t.TeamName, r.SubmitTime from Runs r
join Sessions s on r.SessionId = s.SessionId
join Teams t on s.TeamId = t.TeamId
where r.Accepted = 1 and (s.ContestId, r.SubmitTime) in (
    select s2.ContestId, max(r2.SubmitTime) from Runs r2
    join Sessions s2 on r2.SessionId = s2.SessionId
    where r2.Accepted = 1
    group by s2.ContestId
);
