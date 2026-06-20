delete from Students where StudentId in 
(select Students.StudentId from Students natural join Plan 
left join Marks on Students.StudentId = Marks.StudentId
and Plan.CourseId = Marks.CourseId where Marks.Mark is null
group by Students.StudentId having count(*) >= 2);

