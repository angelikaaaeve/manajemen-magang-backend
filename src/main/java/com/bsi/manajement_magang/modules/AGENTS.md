# InternFlow Backend API Modules Documentation Index

Welcome! This directory contains the complete Spring Boot backend implementation for **InternFlow** (Sistem Informasi Manajemen Magang). To make it easy for frontend AI developers and team members to build and integrate modules, we have compiled recursive `AGENTS.md` files for each subdirectory module.

---

## 📂 Active Backend Modules

Select a module to view its detailed endpoints, query parameters, request payloads, and response structures:

1. **[Identity & Access Management (IAM)](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/iam/AGENTS.md)**
   - Registration, login, profile queries, and profile update.
2. **[Absensi Mahasiswa (Attendance)](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/data_absensi/AGENTS.md)**
   - Attendance listings, approval verifications, statistics, attachments, and Excel CSV exports.
3. **[Kegiatan Mahasiswa (Logbook)](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/data_kegiatan/AGENTS.md)**
   - Daily logbook operations, status updates, statistics, and file viewing.
4. **[Kelola Mahasiswa (Student Administration)](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/data_mahasiswa/AGENTS.md)**
   - CRUD management for students and their internship periods, along with status metrics.
5. **[Penilaian (Evaluation & Grading)](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/penilaian/AGENTS.md)**
   - Multi-criteria student scoring, mentor updates, statistics, and grade book status.
6. **[Sertifikat (Internship Certificates)](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/sertifikat/AGENTS.md)**
   - Certificate tracking, upload handlers, and completion status.
7. **[Surat Keterangan (Completion Letters)](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/surat_keterangan/AGENTS.md)**
   - Internship official completion letter tracking, uploads, and statuses.
8. **[Dashboard Mahasiswa (Student Dashboard)](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/dashboard_mahasiswa/AGENTS.md)**
   - Dashboard statistics (attendance metrics and remaining internship time).
9. **[Dashboard Mentor (Mentor Dashboard)](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/dashboard_mentor/AGENTS.md)**
   - Mentor dashboard features: student search, student registration, and overall attendance statistics.

---

## 🛠️ General API Integration Guidelines

### 1. Base URL
All API paths listed in the sub-modules are relative to:
```env
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
```

### 2. Authorization Header
All endpoints (except `POST /api/iam/login` and `POST /api/iam/register`) require authentication. Send the JSON Web Token (JWT) as a Bearer token:
```http
Authorization: Bearer <your_jwt_token_here>
```

### 3. Date & Time Formats
- **LocalDate**: `yyyy-MM-dd` (e.g., `2026-05-31`)
- **OffsetDateTime**: ISO-8601 extended format with offset (e.g., `2026-05-31T14:30:00+07:00`)
