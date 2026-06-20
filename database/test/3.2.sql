delete from Runs where SessionId in (
    select s.SessionId from Sessions s
    join Contests c on c.ContestId = s.ContestId
    where c.ContestName = :ContestName
);
