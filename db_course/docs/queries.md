## Запит 1: Кількість задач по проєктах і статусах

**Бізнес-питання:**
Як розподіляються задачі по статусах у кожному проєкті (для моніторингу прогресу)?

### SQL-запит

```sql
select
    p.id as project_id,
    t.status as status,
    count(*) as task_count
from projects p
         join tasks t on t.project_id = p.id
where p.is_deleted = false
  and t.is_deleted = false
group by p.id, t.status
order by p.id, t.status
```

### Пояснення

* **JOIN** таблиць `projects` та `tasks` по `t.project_id = p.id`
* **Фільтрація** soft-deleted записів (`is_deleted = FALSE`)
* **GROUP BY** по `project_id` та `status`
* **COUNT(*)** підраховує кількість задач у кожній групі
* **ORDER BY** для стабільного сортування результатів

### Приклад виводу

| project_id | status      | task_count |
|-----------:|-------------|-----------:|
|          1 | TODO        |          5 |
|          1 | IN_PROGRESS |          2 |
|          1 | DONE        |          7 |
|          2 | TODO        |          1 |
|          2 | DONE        |          3 |

---

## Запит 2: Топ виконавців за кількістю завершених задач (DONE) з рейтингом

**Бізнес-питання:**
Хто з користувачів найбільш продуктивний (за кількістю задач у статусі `DONE`)?

### SQL-запит

```sql
select
    u.id as user_id,
    u.email as email,
    count(t.id) as done_count,
    dense_rank() over (order by count(t.id) desc) as rank
from users u
         join tasks t on t.assignee_id = u.id
         join projects p on p.id = t.project_id
where u.is_deleted = false
  and p.is_deleted = false
  and t.is_deleted = false
  and t.status = 'DONE'
group by u.id, u.email
order by done_count desc, u.id
limit ? offset ?
```

### Пояснення

* **JOIN** 3 таблиць: `users`, `tasks`, `projects`
* Фільтрація:

    * тільки активні (не soft-deleted) `users/projects/tasks`
    * тільки задачі зі статусом `DONE`
* **COUNT(t.id)** — кількість завершених задач на кожного виконавця
* **Віконна функція `DENSE_RANK()`**:

    * присвоює рейтинг у порядку спадання `done_count`
    * користувачі з однаковим `done_count` отримають однаковий `rank`
* **limit ? offset ?** — реалізація серверної пагінації

### Приклад виводу

| user_id | email                                 | done_count | rank |
|--------:|---------------------------------------|-----------:|-----:|
|      10 | [a@company.com](mailto:a@company.com) |         15 |    1 |
|      11 | [b@company.com](mailto:b@company.com) |         12 |    2 |
|      12 | [c@company.com](mailto:c@company.com) |         12 |    2 |
|      13 | [d@company.com](mailto:d@company.com) |          8 |    3 |