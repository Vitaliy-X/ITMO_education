update Students set Marks = 
(select count(Mark) from Marks where Marks.StudentId = Students.StudentId)
where StudentId = :StudentId;

