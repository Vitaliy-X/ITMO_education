select StudentId, StudentName, GroupName from Clubs
natural join ClubMembers
natural join Students
natural join Groups
where ClubName = :ClubName;

