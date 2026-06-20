delete from Runs where SessionId in (
    select s.SessionId from Sessions s
    join Teams t on t.TeamId = s.TeamId
    where t.TeamName = :TeamName
);
