UPDATE "user"
SET email = :email, updated_at = NOW()
WHERE id = :id
