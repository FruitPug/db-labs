select * from books where rating > 3;

select user_id, book_id from user_book_statuses where status = 'reading';


insert into users (user_id, name, email, password_hash)
values (gen_random_uuid(), 'Robert', 'robert@gmail.com', 'hash_robert');

select * from users where name = 'Robert';


update users
set name = 'Mike', email = 'mike@gmail.com', password_hash = 'hash_mike'
where name = 'Jessy';

select * from users where name = 'Mike';


delete from books where title = 'History of Europe';

select title from books;