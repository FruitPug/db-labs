select
    b.title,
    count(r.review_id)
from books b
         join reviews r on b.book_id = r.book_id
group by b.title;


select
    b.title,
    avg(r.rating)
from books b
         join reviews r on b.book_id = r.book_id
group by b.title;


select
    a.name,
    count(ba.book_id)
from authors a
         join book_authors ba on a.author_id = ba.author_id
group by a.name;


select
    min(published_year),
    max(published_year)
from books;



select
    u.name,
    b.title,
    r.rating,
    r.comment
from reviews r
         inner join users u on r.user_id = u.user_id
         inner join books b on r.book_id = b.book_id;


select
    u.name,
    bs.status
from users u
         left join user_book_statuses bs on u.user_id = bs.user_id;


select
    a.name,
    b.title
from authors a
         full join book_authors ba on a.author_id = ba.author_id
         full join books b on ba.book_id = b.book_id;



select
    title,
    (select count(*) from reviews r where r.book_id = b.book_id) as review_count
from books b;


select name
from users
where user_id in (select user_id from reviews);


select
    a.name,
    count(ba.book_id)
from authors a
         join book_authors ba on a.author_id = ba.author_id
group by a.name
having count(ba.book_id) > 1;