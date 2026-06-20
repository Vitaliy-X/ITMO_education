select distinct s.StudentName, c.CourseName from 
(select distinct s.StudentId, p.CourseId from Students s join Plan p on s.GroupId = p.GroupId
except select distinct k.StudentId, k.CourseId from Marks k) z
join Students s on s.StudentId = z.StudentId
join Courses c on c.CourseId = z.CourseId;

