create table if not exists university(
    id bigserial primary key,
    name_university text not null,
    created_at timestamp not null default now()
);


alter table mahasiswa
drop column if exists universitas;

alter table mahasiswa
add column id_university bigint references university(id) on delete set null;