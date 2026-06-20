update Students set Marks =
(select count(Mark) from Marks where Marks.StudentId = Students.StudentId)
where StudentId in 
(select StudentId from ClubMembers natural join Clubs where ClubName = :ClubName);

