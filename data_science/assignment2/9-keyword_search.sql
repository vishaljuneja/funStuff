create view corpus as SELECT * FROM frequency
UNION
SELECT 'q' as docid, 'washington' as term, 1 as count 
UNION
SELECT 'q' as docid, 'taxes' as term, 1 as count
UNION 
SELECT 'q' as docid, 'treasury' as term, 1 as count;

select max(s) from(select s.docid, sum(f.count * s.count) as s from (
(select * from corpus where docid='q') as f
inner join
frequency as s
on
f.term=s.term)
group by s.docid);

select docid, max(s) from (select docid, sum(count) as s from (select
from frequency where term="washington" union select * from frequency where ter
"taxes" union select * from frequency where term="treasury") group by docid);