select distinct s.TeamId from Sessions s
join Runs r on r.SessionId = s.SessionId
where s.ContestId = :ContestId and r.Letter = :Letter and r.Accepted = 1;
