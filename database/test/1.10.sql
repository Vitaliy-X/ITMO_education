select p.ContestId, p.Letter from Problems p
where not exists (
    select 1 from Sessions s
    where s.ContestId = p.ContestId and not exists (
          select 1 from Runs r
          where r.SessionId = s.SessionId and r.Letter = p.Letter
    )
)
