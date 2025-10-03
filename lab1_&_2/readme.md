### Лабораторна робота 1

#### Сутності:
- User
- Book 
- Review 
- UserBookStatus 
- Author 
- BookAuthorTable 
- Genre 
- BookGenreTable 
- FriendshipTable

#### Опис
Соціальна платформа для оцінювання книг (IMDB для книг), де користувачі можуть читати, рецензувати та відстежувати книги. Кожна книга може мати кількох авторів та жанрів, користувачі можуть назначати книгам статуси (читаю, у планах, прочитав), писати рецензії та додавати інших користувачів у друзі.

#### Діаграма
![diagram.png](pictures/diagram.png)

### Лабораторна робота 2
#### Код:
```postgresql
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TYPE user_role AS ENUM ('user', 'admin');
CREATE TYPE reading_status AS ENUM ('reading', 'planning', 'finished');

CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    role user_role NOT NULL DEFAULT 'user',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE books (
    book_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title TEXT NOT NULL,
    rating INT NOT NULL DEFAULT 0 CHECK (rating BETWEEN 0 AND 5),
    review_count INT NOT NULL DEFAULT 0 CHECK (review_count >= 0),
    published_year SMALLINT CHECK (published_year >= 0 AND published_year <= 9999)
);

CREATE TABLE reviews (
    review_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    user_id UUID NOT NULL,
    book_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_book FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE
);

CREATE TABLE user_book_statuses (
    user_id UUID NOT NULL,
    book_id UUID NOT NULL,
    status reading_status NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id, book_id),
    CONSTRAINT fk_ubs_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_ubs_book FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE
);

CREATE TABLE authors (
    author_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL
);

CREATE TABLE book_authors (
    book_id UUID NOT NULL,
    author_id UUID NOT NULL,
    PRIMARY KEY (book_id, author_id),
    CONSTRAINT fk_ba_book FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    CONSTRAINT fk_ba_author FOREIGN KEY (author_id) REFERENCES authors(author_id) ON DELETE CASCADE
);

CREATE TABLE genres (
    genre_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE book_genres (
    book_id UUID NOT NULL,
    genre_id UUID NOT NULL,
    PRIMARY KEY (book_id, genre_id),
    CONSTRAINT fk_bg_book FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    CONSTRAINT fk_bg_genre FOREIGN KEY (genre_id) REFERENCES genres(genre_id) ON DELETE CASCADE
);

CREATE TABLE friendships (
    user_id UUID NOT NULL,
    friend_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id, friend_id),
    CONSTRAINT fk_friend_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_friend_friend FOREIGN KEY (friend_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT chk_no_self_follow CHECK (user_id <> friend_id)
);
```
#### Зразки даних:
```postgresql
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
```

#### Таблиці:
**users:**  
![img.png](pictures/img.png)

**books:**  
![img_1.png](pictures/img_1.png)

**reviews:**  
![img_2.png](pictures/img_2.png)

**user_book_statuses:**  
![img_3.png](pictures/img_3.png)

**authors:**  
![img_4.png](pictures/img_4.png)

**book_authors:**  
![img_5.png](pictures/img_5.png)

**genres:**  
![img_6.png](pictures/img_6.png)

**book_genres:**  
![img_7.png](pictures/img_7.png)

**friendships:**  
![img_8.png](pictures/img_8.png)