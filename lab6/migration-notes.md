### add-user-profile
- Додано модель UserProfile (таблиця user_profiles).
- Зв’язок 1–1 з User через userId (FK на users.user_id).

Prisma model (фрагмент):
```prisma
model UserProfile {
  id      String  @id @default(dbgenerated("gen_random_uuid()")) @db.Uuid
  user_id String  @db.Uuid
  user    users   @relation(fields: [user_id], references: [user_id])
  bio     String?
}
```