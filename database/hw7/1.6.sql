delete from Students where 
(select count(*) from Marks m where m.StudentId = Students.StudentId) <= 3;

