update Students set Marks = 
(select count(distinct CourseId) from Marks m
where m.StudentId = Students.StudentId and m.Mark is not null);

