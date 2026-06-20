select distinct s.ContestId from Universities u
join Teams t on t.UnivId = u.UnivId
join Sessions s on s.TeamId = t.TeamId
where u.UnivName = :UnivName;
