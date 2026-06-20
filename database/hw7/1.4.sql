delete from Students where StudentId not in 
(select distinct StudentId from Marks);

