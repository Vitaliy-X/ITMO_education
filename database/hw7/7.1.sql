create view StudentMarks as
select s.StudentId, count(m.Mark) as Marks from Students s
left join Marks m on m.StudentId = s.StudentId and m.Mark is not null
group by s.StudentId;
