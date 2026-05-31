INSERT INTO "user" (id, email, password, role, is_active, created_at, updated_at)
VALUES (:id, :email, :password, :role, :is_active, NOW(), NOW())
