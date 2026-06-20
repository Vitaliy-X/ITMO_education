select s.StudentId, s.StudentName, s.GroupId from Students s
join Marks m on s.StudentId = m.StudentId
join Plan p on m.CourseId = p.CourseId
where p.LecturerId = :LecturerId and m.Mark = :Mark;

