select s.StudentId, s.StudentName, s.GroupId from
(
  select p.CourseId from Plan p
  join Lecturers l on p.LecturerId = l.LecturerId
  where l.LecturerName = :LecturerName
) lec
join Marks m on lec.CourseId = m.CourseId
join Students s on m.StudentId = s.StudentId
where m.Mark = :Mark;

