select sum(f.value * s.value) from (
(select * from a where row_num=2) as f
inner join
(select * from b where col_num=3) as s
on
f.col_num=s.row_num);


select sum(a.value*b.value) 
from a, b 
where a.col_num=b.row_num 
group by a.row_num, b.col_num
having a.row_num=2 and b.col_num=3;