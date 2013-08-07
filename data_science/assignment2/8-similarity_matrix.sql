select sum(f.count * s.count) from (
(select * from frequency where docid='10080_txt_crude') as f
inner join
(select * from frequency where docid='17035_txt_earn') as s
on
f.term=s.term);


select sum(a.count * b.count) 
from frequency as a, frequency as b 
where a.term=b.term 
group by a.docid, b.docid
having a.docid='10080_txt_crude' and b.docid='17035_txt_earn';