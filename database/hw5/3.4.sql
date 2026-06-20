select StudentId, StudentName, GroupId from Students 
natural join Marks
natural join Plan
natural join Lecturers
where LecturerName = :LecturerName and Mark = :Mark;

