## ERD (Entity–Relationship Diagram)

```plantuml
erDiagram
  USERS ||--o{ PROJECT_MEMBERS : "member_of"
  PROJECTS ||--o{ PROJECT_MEMBERS : "has_members"

  USERS ||--o{ TASKS : "creates"
  USERS ||--o{ TASKS : "assigned_to"
  PROJECTS ||--o{ TASKS : "contains"

  TASKS ||--o{ TASK_COMMENTS : "has_comments"
  USERS ||--o{ TASK_COMMENTS : "writes"

  TASKS ||--o{ TASK_TAGS : "tagged_with"
  TAGS  ||--o{ TASK_TAGS : "applied_to"
```

---

## Опис таблиць

### Таблиця: `users`

Призначення: Зберігає облікові записи користувачів системи.

Стовпці:

| Стовпець   | Тип          | Обмеження                | Опис                                    |
|------------|--------------|--------------------------|-----------------------------------------|
| id         | BIGSERIAL    | PK                       | Ідентифікатор користувача               |
| email      | VARCHAR(255) | UNIQUE, NOT NULL         | Email (унікальний)                      |
| full_name  | VARCHAR(255) | NOT NULL                 | Ім’я користувача                        |
| role       | VARCHAR(50)  | NOT NULL, CHECK IN (...) | Роль (наприклад `DEVELOPER`, `MANAGER`) |
| created_at | TIMESTAMP    | NOT NULL DEFAULT NOW()   | Час створення                           |
| updated_at | TIMESTAMP    | NOT NULL DEFAULT NOW()   | Час оновлення                           |
| is_deleted | BOOLEAN      | NOT NULL DEFAULT FALSE   | Soft delete прапорець                   |
| deleted_at | TIMESTAMP    | NULL                     | Час м’якого видалення                   |

Індекси:

* `idx_users_email` на `email` (швидкий пошук користувача по email)

Зв’язки:

* 1:N з `tasks` (як `creator_id`)
* 1:N з `tasks` (як `assignee_id`)
* 1:N з `task_comments` (як автор коментаря)
* M:N з `projects` через `project_members`

---

### Таблиця: `projects`

Призначення: Проєкти, в яких ведеться робота над задачами.

Стовпці:

| Стовпець    | Тип          | Обмеження                | Опис                                            |
|-------------|--------------|--------------------------|-------------------------------------------------|
| id          | BIGSERIAL    | PK                       | Ідентифікатор проєкту                           |
| name        | VARCHAR(255) | NOT NULL                 | Назва проєкту                                   |
| description | TEXT         | NULL                     | Опис                                            |
| status      | VARCHAR(50)  | NOT NULL, CHECK IN (...) | Статус проєкту (наприклад `ACTIVE`, `ARCHIVED`) |
| created_at  | TIMESTAMP    | NOT NULL DEFAULT NOW()   | Час створення                                   |
| updated_at  | TIMESTAMP    | NOT NULL DEFAULT NOW()   | Час оновлення                                   |
| is_deleted  | BOOLEAN      | NOT NULL DEFAULT FALSE   | Soft delete прапорець                           |
| deleted_at  | TIMESTAMP    | NULL                     | Час м’якого видалення                           |

Індекси:

* `idx_projects_status` на `status` (фільтрація по статусу)

Зв’язки:

* 1:N з `tasks`
* M:N з `users` через `project_members`

---

### Таблиця: `project_members`

Призначення: Зв’язок “користувач ↔ проєкт” + роль користувача в проєкті.

Стовпці:

| Стовпець   | Тип         | Обмеження                     | Опис                                       |
|------------|-------------|-------------------------------|--------------------------------------------|
| id         | BIGSERIAL   | PK                            | Ідентифікатор запису                       |
| project_id | BIGINT      | FK → `projects(id)`, NOT NULL | Проєкт                                     |
| user_id    | BIGINT      | FK → `users(id)`, NOT NULL    | Користувач                                 |
| role       | VARCHAR(50) | NOT NULL, CHECK IN (...)      | Роль в проєкті (`OWNER`, `CONTRIBUTOR`, …) |
| joined_at  | TIMESTAMP   | NOT NULL DEFAULT NOW()        | Дата входу в проєкт                        |

Додаткові обмеження:

* `UNIQUE(project_id, user_id)` — користувач не може повторно бути доданий до того ж проєкту

Індекси:

* `idx_project_members_project_user` на `(project_id, user_id)` (швидкі перевірки membership)

Зв’язки:

* FK до `projects`
* FK до `users`

---

### Таблиця: `tasks`

Призначення: Задачі в межах проєктів. Підтримує optimistic locking через `version`.

Стовпці:

| Стовпець    | Тип          | Обмеження                     | Опис                                            |
|-------------|--------------|-------------------------------|-------------------------------------------------|
| id          | BIGSERIAL    | PK                            | Ідентифікатор задачі                            |
| project_id  | BIGINT       | FK → `projects(id)`, NOT NULL | Проєкт                                          |
| creator_id  | BIGINT       | FK → `users(id)`, NOT NULL    | Хто створив                                     |
| assignee_id | BIGINT       | FK → `users(id)`, NULL        | Кому призначено                                 |
| title       | VARCHAR(255) | NOT NULL                      | Назва                                           |
| description | TEXT         | NULL                          | Опис                                            |
| status      | VARCHAR(50)  | NOT NULL, CHECK IN (...)      | Статус (`TODO`, `IN_PROGRESS`, `DONE`, …)       |
| priority    | VARCHAR(50)  | NOT NULL, CHECK IN (...)      | Пріоритет (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`) |
| due_date    | DATE         | NULL                          | Дедлайн                                         |
| version     | BIGINT       | NOT NULL DEFAULT 0            | Версія для optimistic locking                   |
| created_at  | TIMESTAMP    | NOT NULL DEFAULT NOW()        | Час створення                                   |
| updated_at  | TIMESTAMP    | NOT NULL DEFAULT NOW()        | Час оновлення                                   |
| is_deleted  | BOOLEAN      | NOT NULL DEFAULT FALSE        | Soft delete прапорець                           |
| deleted_at  | TIMESTAMP    | NULL                          | Час м’якого видалення                           |

Індекси:

* `idx_tasks_project` на `project_id` (вибірка задач проєкту)
* `idx_tasks_assignee` на `assignee_id` (вибірка задач виконавця)
* `idx_tasks_status` на `status` (фільтри по статусу)

Зв’язки:

* FK до `projects`
* FK до `users` (creator, assignee)
* 1:N з `task_comments`
* M:N з `tags` через `task_tags`

---

### Таблиця: `task_comments`

Призначення: Коментарі до задач.

Стовпці:

| Стовпець   | Тип       | Обмеження                  | Опис                    |
|------------|-----------|----------------------------|-------------------------|
| id         | BIGSERIAL | PK                         | Ідентифікатор коментаря |
| task_id    | BIGINT    | FK → `tasks(id)`, NOT NULL | Задача                  |
| author_id  | BIGINT    | FK → `users(id)`, NOT NULL | Автор                   |
| body       | TEXT      | NOT NULL                   | Текст коментаря         |
| created_at | TIMESTAMP | NOT NULL DEFAULT NOW()     | Час створення           |
| updated_at | TIMESTAMP | NOT NULL DEFAULT NOW()     | Час оновлення           |
| is_deleted | BOOLEAN   | NOT NULL DEFAULT FALSE     | Soft delete прапорець   |
| deleted_at | TIMESTAMP | NULL                       | Час м’якого видалення   |

Індекси:

* `idx_task_comments_task_id` на `task_id` (швидка вибірка коментарів задачі)

Зв’язки:

* FK до `tasks`
* FK до `users`

---

### Таблиця: `tags`

Призначення: Теги для класифікації задач.

Стовпці:

| Стовпець   | Тип          | Обмеження              | Опис                               |
|------------|--------------|------------------------|------------------------------------|
| id         | BIGSERIAL    | PK                     | Ідентифікатор тегу                 |
| name       | VARCHAR(100) | UNIQUE, NOT NULL       | Назва тегу                         |
| color      | VARCHAR(30)  | NULL                   | Колір (наприклад `red`, `#FF0000`) |
| created_at | TIMESTAMP    | NOT NULL DEFAULT NOW() | Час створення                      |
| updated_at | TIMESTAMP    | NOT NULL DEFAULT NOW() | Час оновлення                      |
| is_deleted | BOOLEAN      | NOT NULL DEFAULT FALSE | Soft delete прапорець              |
| deleted_at | TIMESTAMP    | NULL                   | Час м’якого видалення              |

Індекси:

* `idx_tags_name` на `name` (пошук/унікальність)
* `idx_tags_color` на `color` (фільтрація за кольором)

Зв’язки:

* M:N з `tasks` через `task_tags`

---

### Таблиця: `task_tags`

Призначення: Зв’язок M:N між задачами та тегами.

Стовпці:

| Стовпець | Тип    | Обмеження                      | Опис   |
|----------|--------|--------------------------------|--------|
| task_id  | BIGINT | PK (частина), FK → `tasks(id)` | Задача |
| tag_id   | BIGINT | PK (частина), FK → `tags(id)`  | Тег    |

Обмеження:

* `PRIMARY KEY(task_id, tag_id)` — один тег не може бути двічі доданий до однієї задачі

Індекси:

* `idx_task_tags_tag_id` на `tag_id` (швидка вибірка задач по тегу)

Зв’язки:

* FK до `tasks`
* FK до `tags`

---

## Рішення щодо дизайну

### 1) Чому обрана така структура

* Предметна область (проєкти/задачі) має природні зв’язки:

    * проєкт містить задачі (1:N)
    * користувачі мають ролі в проєктах (M:N + атрибут role) → винесено в `project_members`
    * задачі можуть мати багато тегів (M:N) → `task_tags`
    * коментарі належать задачам (1:N) → `task_comments`
* Структура дозволяє будувати як CRUD-операції, так і аналітичні запити (топ виконавців, статистика статусів і т.д.).

### 2) Досягнутий рівень нормалізації

* Схема відповідає **3НФ**:

    * відсутні повторювані групи;
    * M:N зв’язки винесено в окремі таблиці;
    * атрибути, що залежать від зв’язку (роль користувача в проєкті), винесені в `project_members`.

### 3) Компроміси

* **Soft-cascade** реалізований на рівні сервісів (а не через тригери).
* Хоч нормалізація до 3НФ і зменшила надмірність та аномалії, це сталося коштом зменшення швидкодії.

### 4) Стратегія індексування

Індекси додані під найбільш типові запити:

* `users.email` — швидкий пошук користувача (унікальний, часто використовується)
* `tasks.project_id` — отримання задач проєкту
* `tasks.assignee_id` — задачі конкретного виконавця
* `project_members(project_id, user_id)` — швидка перевірка, чи є користувач учасником проєкту
* `task_comments.task_id` — швидка вибірка коментарів задачі
* `task_tags.tag_id` — вибірка задач по тегу