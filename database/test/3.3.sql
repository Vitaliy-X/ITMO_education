insert into Sessions (TeamId, ContestId, Start)
select distinct s.TeamId, :ContestId, current_timestamp from Sessions s
where s.ContestId = :ContestId;
