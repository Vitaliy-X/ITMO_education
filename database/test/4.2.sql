select i.TeamId, count(i.Letter) as Opened
from (
    select distinct s.TeamId, r.Letter, s.ContestId
    from Runs r
    join Sessions s on r.SessionId = s.SessionId
) i
group by i.TeamId;
