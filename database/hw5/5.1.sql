select s.StudentName, c.CourseName from 
(select distinct z.StudentId, p.CourseId from Plan p 
join Students z on z.GroupId = p.GroupId) r
join Students s on s.StudentId = r.StudentId
join Courses c on c.CourseId = r.CourseId;

