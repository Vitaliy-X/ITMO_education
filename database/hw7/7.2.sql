create view AllMarks as
select s.StudentId, count(mk.Mark) as Marks from Students s left join 
(select StudentId, Mark from Marks
union all
select StudentId, Mark from NewMarks) mk 
on mk.StudentId = s.StudentId and mk.Mark is not null group by s.StudentId;

