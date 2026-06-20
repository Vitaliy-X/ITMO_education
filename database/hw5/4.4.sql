select s.StudentId, s.StudentName, s.GroupId from Students s
join Groups g on s.GroupId = g.GroupId
join Plan p on g.GroupId = p.GroupId
join Courses c on p.CourseId = c.CourseId
where c.CourseName = :CourseName and s.StudentId not in 
(select k.StudentId from Marks k
join Courses z on k.CourseId = z.CourseId where z.CourseName = :CourseName);

