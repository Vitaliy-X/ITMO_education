select r.RunId, r.SessionId, r.Letter, r.SubmitTime, r.Accepted from Runs r
join Sessions s on r.SessionId = s.SessionId
join Teams t on s.TeamId = t.TeamId
join Contests c on s.ContestId = c.ContestId
where t.TeamName = :TeamName and c.ContestName = :ContestName;
