#### 1. Аналіз
Усі таблиці відповідають NF3, хоча таблиця books має декілька аномалій.

##### Таблиця books:
```postgresql
CREATE TABLE books (
book_id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
title varchar(100) NOT NULL,
rating real DEFAULT 0 CHECK (rating >= 0 AND rating <= 5),
review_count integer DEFAULT 0 CHECK (review_count >= 0),
published_year smallint CHECK (published_year >= 0 AND published_year <= 9999)
);
```

Проблема: хоч **rating** та **review_count** строго кажучи не порушують NF3, бо вони все ще залежать виключно від book_id, а не від інших неключових атрибутів, вони є похідними значеннями. Тобто систему можна нормалізувати ще більше.

##### Перероблений дизайн:
```postgresql
ALTER TABLE books DROP COLUMN rating;
ALTER TABLE books DROP COLUMN review_count;
```

#### 2. Функціональні залежності

##### books:

* book_id → title, published_year

* book_id (і таблиця reviews) → rating, review_count

#### 3. Pros and Cons
Pros:
* books тепер містить лише атрибути, властиві book.
* нема ризику аномалій оновлення: додавання/оновлення/видалення відгуку автоматично впливає на агрегати.

Cons:
* агреговані запити є більш обчислювально складними (хоч належних індексів reviews(book_id) має бути достатньо).

#### 4. Нормалізація

* 1NF: Усі поля атомарні, повторювані групи відсутні.
* 2NF: Часткові залежності в таблицях зі складеними ключами відсутні.
* 3NF: Транзитивні залежності між неключовими атрибутами відсутні. rating та review_count були похідними значеннями, були видалені.