delete from Students where 
GroupId = (select GroupId from Groups where GroupName = :GroupName);

