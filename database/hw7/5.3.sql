update Students set Debts = coalesce
((select count(distinct pl.CourseId) from Plan pl
where pl.GroupId = Students.GroupId and pl.CourseId not in 
(select m.CourseId from Marks m
where m.StudentId = Students.StudentId and m.Mark is not null)), 0);

