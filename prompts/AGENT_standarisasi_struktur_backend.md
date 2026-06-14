# Backend Module Structure Refactor — Prompt

> **Stack target:** Java + Spring Boot
> **Tujuan:** Merapihkan struktur module backend supaya *separation of concerns* jelas. Tiap layer punya satu tanggung jawab, dan semua schema ngikutin pattern `XRequest`, `XResponse`, `XEntity`.

---

## 1. Konteks & Goal

Lu adalah backend engineer yang lagi refactor sebuah module Spring Boot. Tugas lu **merapihkan struktur**, bukan ngubah behavior. Pisahkan kode jadi 4 layer dengan tanggung jawab yang tegas:

```
Controller  →  Service  →  Repository
                  ↓
               Schema (Request / Response / Entity)
```

Aturan utama: **data ngalir satu arah, dan tiap layer cuma boleh ngobrol sama tetangga langsungnya.** Controller gak boleh nyentuh Repository. Entity gak boleh bocor keluar lewat Controller.

---

## 2. Tanggung Jawab Tiap Layer

| Layer          | Boleh                                                                 | Gak Boleh                                                        |
|----------------|----------------------------------------------------------------------|-----------------------------------------------------------------|
| **Controller** | Terima HTTP request, validasi input (`@Valid`), panggil Service, return `XResponse` | Nyimpen business logic, akses Repository langsung, return Entity |
| **Service**    | Business logic, orchestration, transaction (`@Transactional`), mapping Entity ↔ DTO | Tau soal HTTP (status code, header), query SQL manual            |
| **Repository** | Akses data (`extends JpaRepository`), custom query                   | Business logic, mapping DTO                                      |
| **Schema**     | Definisi bentuk data: `XRequest`, `XResponse`, `XEntity`             | Logic apapun (kecuali validasi annotation di Request)           |

**Prinsip kunci:**
- `Controller` cuma jadi *thin layer* — terima, delegasi, balikin.
- `Service` adalah satu-satunya tempat business logic hidup.
- `Entity` gak pernah keluar dari boundary Service. Yang keluar ke Controller selalu `XResponse`.
- `XRequest` yang masuk dari Controller di-mapping ke Entity di dalam Service.

---

## 3. Naming Convention (WAJIB)

Untuk tiap domain `X` (contoh: `User`, `Student`, `Invoice`):

| Pattern      | Fungsi                                  | Contoh                                      |
|--------------|-----------------------------------------|---------------------------------------------|
| `XRequest`   | DTO input dari client (+ validasi)      | `UserCreateRequest`, `UserUpdateRequest`    |
| `XResponse`  | DTO output ke client (tanpa field sensitif) | `UserResponse`, `UserDetailResponse`    |
| `XEntity`    | JPA entity, kepetaan ke tabel DB        | `UserEntity`                                |

Catatan:
- Boleh ada lebih dari satu `XRequest`/`XResponse` per domain kalau use-case-nya beda (`CreateRequest`, `UpdateRequest`, `LoginRequest`, dst). Tetep ngikutin suffix-nya.
- `XEntity` cukup satu per tabel.
- Field sensitif (password, token, internal flag) **gak boleh** ada di `XResponse`.

---

## 4. Target Folder Structure

Pakai struktur **package-by-feature** (per module), bukan package-by-layer global:

```
src/main/java/com/entropy/app/
└── module/
    └── user/
        ├── controller/
        │   └── UserController.java
        ├── service/
        │   ├── UserService.java          # interface
        │   └── impl/
        │       └── UserServiceImpl.java  # implementasi
        ├── repository/
        │   └── UserRepository.java
        └── schema/
            ├── request/
            │   ├── UserCreateRequest.java
            │   └── UserUpdateRequest.java
            ├── response/
            │   └── UserResponse.java
            └── entity/
                └── UserEntity.java
```

Kalau tim lu lebih suka `dto/` daripada `schema/`, konsisten aja satu nama di seluruh project. Default di sini: `schema/`.

---

## 5. Aturan Refactor (Rules)

1. **Controller gak boleh import Repository.** Kalau ada, pindahin pemanggilannya ke Service.
2. **Service gak boleh return `XEntity`.** Map dulu ke `XResponse` sebelum balikin.
3. **`XRequest` wajib bawa validasi** (`@NotBlank`, `@Email`, `@Size`, dst). Validasi di Controller pakai `@Valid`.
4. **Business logic kosong di Controller.** Method controller idealnya cuma 1–3 baris: validasi → panggil service → bungkus response.
5. **Mapping Entity ↔ DTO** dilakukan di Service (atau di mapper class terpisah, mis. `UserMapper`). Jangan di Controller.
6. **Transaction (`@Transactional`)** ada di Service, bukan di Controller atau Repository.
7. **Konsistenkan response wrapper** kalau project punya (mis. `ApiResponse<T>`). Semua endpoint balikin format yang sama.
8. **Jangan ubah behavior.** Output endpoint sebelum dan sesudah refactor harus identik.

---

## 6. Skeleton Tiap Layer (Reference)

**Controller** — thin, delegasi doang:
```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }
}
```

**Service** — interface + impl, tempat logic & mapping:
```java
public interface UserService {
    UserResponse create(UserCreateRequest request);
    UserResponse getById(Long id);
}

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponse create(UserCreateRequest request) {
        UserEntity entity = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();
        return toResponse(userRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
        return toResponse(entity);
    }

    private UserResponse toResponse(UserEntity entity) {
        return UserResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .build();
    }
}
```

**Repository** — akses data doang:
```java
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
}
```

**Schema** — Request (validasi), Response (output bersih), Entity (DB):
```java
// request/UserCreateRequest.java
@Data
public class UserCreateRequest {
    @NotBlank private String name;
    @Email @NotBlank private String email;
}

// response/UserResponse.java
@Data
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    // NOTE: gak ada password / field sensitif
}

// entity/UserEntity.java
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true)
    private String email;
    private String password; // ada di entity, TAPI gak diekspos di response
}
```

---

## 7. Task buat Agent

Refactor module yang gua kasih dengan langkah ini:

1. **Identifikasi domain** yang ada di module (User, Student, dst).
2. **Bikin folder structure** sesuai Section 4.
3. **Rename / split semua DTO** ngikutin pattern `XRequest`, `XResponse`, `XEntity` (Section 3).
4. **Pindahin business logic** yang nyangkut di Controller ke Service.
5. **Putus akses langsung Controller → Repository**, lewatin Service.
6. **Pastikan Entity gak bocor** ke Controller — semua di-map ke `XResponse`.
7. **Tambahin validasi** di tiap `XRequest`.
8. **Verifikasi behavior gak berubah**: list endpoint + bentuk response sebelum vs sesudah harus sama.

### Output yang gua mau:
- Struktur folder final (tree).
- File hasil refactor, di-pisah per layer.
- Ringkasan perubahan: apa yang dipindah, di-rename, atau di-split — dan kenapa.
- Flag kalau ada bagian yang ambigu / butuh keputusan gua.

---

> **Reminder:** Refactor ini soal *struktur & tanggung jawab*, bukan ngubah fitur. Kalau ragu antara dua pendekatan, pilih yang paling sederhana dan tanya gua.
