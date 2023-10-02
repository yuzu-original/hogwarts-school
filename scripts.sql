--find students by age
select
	*
from
	student s
where
    age between 10 and 20;

--get all students names
select
	"name"
from
	student s;

--find students by name
select
	*
from
	student s
where
	"name" like '%o%';

--find students with age < id
select
	*
from
	student s
where
	age < id;

--get all students sorted by age
select
	*
from
	student s
order by
	age;