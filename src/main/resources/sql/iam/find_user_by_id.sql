SELECT id, email, password, role, is_active, created_at, updated_at
FROM "user"
WHERE id = :id
