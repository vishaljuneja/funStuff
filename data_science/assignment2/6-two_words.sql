select count(*) from (select * from 
(select * from frequency  where term="transactions") as a
inner join 
(select * from frequency where term="world") as b 
on a.docid=b.docid);

select count(*) from (select docid from frequency where term="transactions" 
INTERSECT 
select docid from frequency where term="world";