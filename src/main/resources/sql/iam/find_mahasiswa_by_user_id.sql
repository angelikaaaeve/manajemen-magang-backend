SELECT m.id, m.user_id, m.nim, m.nama, m.no_hp, m.gender, m.id_university, univ.name_university AS universitas
FROM mahasiswa m
LEFT JOIN university univ ON m.id_university = univ.id
WHERE m.user_id = :userId

