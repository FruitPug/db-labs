### Лабораторна робота 1

#### 1. Оригінальний та перероблений дизайн таблиць

##### Оригінальна таблиця books:
```postgresql
CREATE TABLE books (
book_id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
title varchar(100) NOT NULL,
rating real DEFAULT 0 CHECK (rating >= 0 AND rating <= 5),
review_count integer DEFAULT 0 CHECK (review_count >= 0),
published_year smallint CHECK (published_year >= 0 AND published_year <= 9999)
);
```

Проблема: **rating** та **review_count** є похідними даними, що порушує 3NF.

##### Перероблений дизайн:
```postgresql
ALTER TABLE books DROP COLUMN rating;
ALTER TABLE books DROP COLUMN review_count;
```

#### 2. Функціональні залежності

##### books:

* book_id → title, published_year

* (Порушення) book_id → rating, review_count (через залежність від reviews)

#### 3. Покрокова нормалізація

* 1NF: Усі поля атомарні.
* 2NF: Первинний ключ - один атрибут (book_id), часткових залежностей немає.
* 3NF: rating і review_count транзитивно залежать від reviews, видалено.