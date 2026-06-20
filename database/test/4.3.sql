select p.Letter from Problems p
join Runs r on p.Letter = r.Letter
join Sessions s on r.SessionId = s.SessionId
where p.ContestId = :ContestId and s.ContestId = :ContestId and r.Accepted = 1
group by p.Letter
having count(distinct s.TeamId) = 
(
    select max(cnt) from (
        select count(distinct s2.TeamId) as cnt from Problems p2
        join Runs r2 on p2.Letter = r2.Letter
        join Sessions s2 on r2.SessionId = s2.SessionId
        where p2.ContestId = :ContestId and s2.ContestId = :ContestId and r2.Accepted = 1
        group by p2.Letter
    ) x
);
