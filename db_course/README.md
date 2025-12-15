# Project and Task Tracker

## Опис проєкту

**Project and Task Tracker** — це backend-додаток для управління проєктами, задачами та учасниками команди.
Система дозволяє створювати проєкти, призначати користувачів, керувати задачами, тегами та коментарями, а також виконувати аналітичні запити для оцінки активності та продуктивності.

Предметна область: **управління проєктами та задачами**.

Проєкт розроблено з фокусом на:
* правильне проєктування реляційної бази даних (3НФ),
* транзакційну бізнес-логіку,
* обробку конкурентних оновлень,
* повноцінне тестування (integration).

---

## Технологічний стек

### Backend

* **Java 17**
* **Spring Boot**
* **Spring Data JPA (Hibernate)**

### База даних

* **PostgreSQL**

### Міграції БД

* **Liquibase** 

### Тестування

* **JUnit 5**
* **AssertJ**
* **Spring Boot Test**
* **Testcontainers**

### Контейнеризація

* **Docker**
* **Docker Compose**

---

## Інструкції з налаштування

### Передумови

Переконайтесь, що встановлено:
* Java 17
* Maven 3.9+
* Docker + Docker Compose

---

## Запуск додатку

### Запуск через Docker Compose

```bash
docker compose build
docker compose up
```

Додаток буде доступний за адресою:

```
http://localhost:8080
```

PostgreSQL:

```
localhost:5432
db: db_course
user: user
password: pass
```

---

### Запуск локально (без контейнера)

```bash
mvn spring-boot:run
```

> У цьому випадку PostgreSQL повинен бути запущений окремо
> або використовуватись Testcontainers для тестів.

---

## Запуск тестів

### Запустити всі тести

```bash
mvn verify
```

### Запустити конкретний тестовий клас

```bash
mvn verify -Dit.test=ProjectIT
```

### Запустити конкретний тестовий метод

```bash
mvn verify -Dit.test=ProjectIT#createProject
```

> Інтеграційні тести використовують **Testcontainers**, тому Docker повинен бути запущений.

---

## Огляд структури проєкту

```text
src
├── main
│   ├── java/com/example/db_course
│   │   ├── controller     # REST API (HTTP layer)
│   │   ├── dto            # DTO для запитів/відповідей
│   │   ├── entity         # JPA сутності
│   │   ├── mapper         # Перетворення Entity ↔ DTO
│   │   ├── repository     # Доступ до БД (JPA)
│   │   └── service        # Бізнес-логіка, транзакції
│   └── resources
│       ├── db/changelog   # Liquibase SQL міграції
│       └── application*.properties
│
└── test
    ├── java/com/example/db_course
    │   ├── integration    # Інтеграційні тест
    │   ├── unit           # Юніт-тести
    │   └── EntityCreator.java
```

---

## Приклади API

### Створення проєкту

```http
POST http://localhost:8080/projects
Content-Type: application/json

{
  "name": "Test Project",
  "description": "Project description"
}
```

---

### Створення проєкт з власником

```http
POST http://localhost:8080/projects/with-owner
Content-Type: application/json

{
  "name": "Test Project",
  "description": "Project description",
  "ownerId": 1
}
```

---

### Отримання проєктів з фільтрацією та пагінацією

```http
GET http://localhost:8080/projects?status=ACTIVE&page=0&size=10
```

---

### М'яке видалення проєкту

```http
DELETE http://localhost:8080/projects/{id}
```

---

### Жорстке видалення проєкту

```http
DELETE http://localhost:8080/projects/{id}/hard
```

---

### Перепризначення задачі

```http
PATCH http://localhost:8080/tasks/assignee
Content-Type: application/json

{
  "taskId": 1,
  "newAssigneeUserId": 2
}
```

---

## Примітки

* М'яке видалення реалізоване через `is_deleted` + `deleted_at`
* Каскадне soft delete реалізовано на рівні сервісів:
    * project → tasks
    * task → comments
* Конкурентні оновлення обробляються через **optimistic locking** (`@Version`)
* Hard delete використовує **DB-level CASCADE**