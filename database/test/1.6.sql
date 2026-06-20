select ProblemName, ContestId from Problems
except
select distinct p.ProblemName, p.ContestId from Problems p
join Runs r on r.Letter = p.Letter and r.Accepted = 1;
