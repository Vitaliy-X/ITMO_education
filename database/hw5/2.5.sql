select s.StudentId, s.StudentName, g.GroupName from Clubs c
join Students main on c.ClubStudentHeadId = main.StudentId
join ClubMembers m on c.ClubId = m.ClubId
join Students s on m.StudentId = s.StudentId
join Groups g on s.GroupId = g.GroupId
where main.StudentName = :StudentName;

