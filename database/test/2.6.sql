select p.ProblemName from Problems p
where not exists (
    select 1 from Sessions s
    join Runs r on r.SessionId = s.SessionId
    where s.ContestId = p.ContestId and r.Letter = p.Letter and r.Accepted = 1
);
