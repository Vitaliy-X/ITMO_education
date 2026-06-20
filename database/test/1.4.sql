select r.RunId, r.SessionId, r.Letter, r.SubmitTime from Runs r
join Sessions s on r.SessionId = s.SessionId
join Contests c on s.ContestId = c.ContestId
where r.Accepted = 0 and c.ContestName = :ContestName;
