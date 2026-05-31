SELECT id, user_id, nim, nama, no_hp, gender, universitas
FROM mahasiswa
WHERE user_id = :userId
