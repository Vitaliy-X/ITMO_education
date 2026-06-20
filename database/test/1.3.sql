select r.RunId, s.TeamId, r.SubmitTime, r.Accepted from Runs r
join Sessions s on r.SessionId = s.SessionId
join Contests c on s.ContestId = c.ContestId
where r.Letter = :Letter and c.ContestName = :ContestName;
