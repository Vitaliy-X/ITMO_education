update Marks m set Mark = 
(select nm.Mark from NewMarks nm 
where nm.StudentId = m.StudentId and nm.CourseId = m.CourseId)
where exists (select 1 from NewMarks nm2
where nm2.StudentId = m.StudentId and nm2.CourseId = m.CourseId);

