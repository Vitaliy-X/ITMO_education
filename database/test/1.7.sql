select distinct t.TeamName from Teams t
join Sessions s on s.TeamId = t.TeamId
where not exists (
    select 1 from Runs r
    where r.SessionId = s.SessionId and r.Accepted = 1
);
