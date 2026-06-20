delete from Students where StudentId in 
(select StudentId from ClubMembers join Clubs using (ClubId) where ClubName = :ClubName);

