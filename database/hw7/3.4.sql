update Students set StudentName = :StudentName where GroupId = 
(select GroupId from Groups where GroupName = :GroupName);

