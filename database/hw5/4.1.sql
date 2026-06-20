select s.StudentId, s.StudentName, s.GroupId from Students s
where s.StudentId not in 
(select k.StudentId from Marks k
join Courses c on k.CourseId = c.CourseId where c.CourseName = :CourseName);

