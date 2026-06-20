insert into Marks (StudentId, CourseId, Mark)
select nm.StudentId, nm.CourseId, nm.Mark from NewMarks nm where not exists 
(select 1 from Marks m 
where m.StudentId = nm.StudentId and m.CourseId = nm.CourseId);

