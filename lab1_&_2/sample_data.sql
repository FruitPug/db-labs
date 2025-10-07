INSERT INTO users (user_id, name, email, password_hash, role)
VALUES
    (gen_random_uuid(), 'Alice', 'alice@example.com', 'hash_alice', 'user'),
    (gen_random_uuid(), 'Bob', 'bob@example.com', 'hash_bob', 'user'),
    (gen_random_uuid(), 'Walter', 'walter@example.com', 'hash_walter', 'user'),
    (gen_random_uuid(), 'Jessy', 'jessy@example.com', 'hash_jessy', 'user');

INSERT INTO books (book_id, title, rating, review_count, published_year)
VALUES
    (gen_random_uuid(), 'The Great Adventure', 5, 1, 2010),
    (gen_random_uuid(), 'Data Structures in Depth', 4, 1, 2018),
    (gen_random_uuid(), 'Cooking with Love', 3, 1, 2021),
    (gen_random_uuid(), 'History of Europe', 0, 0, 1995);

INSERT INTO authors (author_id, name)
VALUES
    (gen_random_uuid(), 'John Writer'),
    (gen_random_uuid(), 'Mary Cook'),
    (gen_random_uuid(), 'Evelyn Historian');

INSERT INTO genres (genre_id, name)
VALUES
    (gen_random_uuid(), 'Adventure'),
    (gen_random_uuid(), 'Technology'),
    (gen_random_uuid(), 'Cooking'),
    (gen_random_uuid(), 'History');

INSERT INTO reviews (review_id, rating, comment, user_id, book_id)
VALUES
    (gen_random_uuid(), 5, 'Loved it! Very engaging.',
     (SELECT user_id FROM users WHERE email='alice@example.com'),
     (SELECT book_id FROM books WHERE title='The Great Adventure')),
    (gen_random_uuid(), 4, 'Well written and useful examples.',
     (SELECT user_id FROM users WHERE email='bob@example.com'),
     (SELECT book_id FROM books WHERE title='Data Structures in Depth')),
    (gen_random_uuid(), 3, 'Recipes are good but a bit basic.',
     (SELECT user_id FROM users WHERE email='walter@example.com'),
     (SELECT book_id FROM books WHERE title='Cooking with Love'));

INSERT INTO user_book_statuses (user_id, book_id, status)
VALUES
    ((SELECT user_id FROM users WHERE email='alice@example.com'),
     (SELECT book_id FROM books WHERE title='The Great Adventure'),
     'finished'),
    ((SELECT user_id FROM users WHERE email='alice@example.com'),
     (SELECT book_id FROM books WHERE title='Data Structures in Depth'),
     'reading'),
    ((SELECT user_id FROM users WHERE email='bob@example.com'),
     (SELECT book_id FROM books WHERE title='Cooking with Love'),
     'planning'),
    ((SELECT user_id FROM users WHERE email='jessy@example.com'),
     (SELECT book_id FROM books WHERE title='History of Europe'),
     'reading');

INSERT INTO book_authors (book_id, author_id)
VALUES
    ((SELECT book_id FROM books WHERE title='The Great Adventure'),
     (SELECT author_id FROM authors WHERE name='John Writer')),
    ((SELECT book_id FROM books WHERE title='Cooking with Love'),
     (SELECT author_id FROM authors WHERE name='Mary Cook')),
    ((SELECT book_id FROM books WHERE title='History of Europe'),
     (SELECT author_id FROM authors WHERE name='Evelyn Historian'));

INSERT INTO book_genres (book_id, genre_id)
VALUES
    ((SELECT book_id FROM books WHERE title='The Great Adventure'),
     (SELECT genre_id FROM genres WHERE name='Adventure')),
    ((SELECT book_id FROM books WHERE title='Data Structures in Depth'),
     (SELECT genre_id FROM genres WHERE name='Technology')),
    ((SELECT book_id FROM books WHERE title='Cooking with Love'),
     (SELECT genre_id FROM genres WHERE name='Cooking')),
    ((SELECT book_id FROM books WHERE title='History of Europe'),
     (SELECT genre_id FROM genres WHERE name='History'));

INSERT INTO friendships (user_id, friend_id)
VALUES
    ((SELECT user_id FROM users WHERE email='alice@example.com'),
     (SELECT user_id FROM users WHERE email='bob@example.com')),
    ((SELECT user_id FROM users WHERE email='bob@example.com'),
     (SELECT user_id FROM users WHERE email='jessy@example.com')),
    ((SELECT user_id FROM users WHERE email='jessy@example.com'),
     (SELECT user_id FROM users WHERE email='alice@example.com')),
    ((SELECT user_id FROM users WHERE email='walter@example.com'),
     (SELECT user_id FROM users WHERE email='alice@example.com'));