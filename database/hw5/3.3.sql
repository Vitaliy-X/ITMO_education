select StudentId, StudentName, GroupId from Students
natural join Marks
natural join Plan
where LecturerId = :LecturerId and Mark = :Mark;

