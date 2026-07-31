# Product Management API (EC8) — Product Catalog Service

API RESTful berbasis **Spring Boot** untuk mengelola katalog produk (Product Catalog Service) milik platform e-commerce/fintech, lengkap dengan:
- **Autentikasi JWT** (register & login)
- **Otorisasi berbasis role** (ADMIN vs USER)
- **Caching** pada endpoint read-heavy (`@Cacheable`, `@CachePut`, `@CacheEvict`)

---

## 🚀 Teknologi

- **Java 17+**
- **Spring Boot 4.1**
- **Spring Security** (JWT, BCrypt, role-based authorization)
- **JJWT 0.13.0** (library JWT)
- **Spring Cache** (ConcurrentMapCache — in-memory provider)
- **Spring Data JPA & Hibernate**
- **MySQL Database**
- **Jakarta Bean Validation**
- **Lombok**
- **H2 Database** (untuk Automated Unit Test)
- **Maven**

---

## 🛠️ Konfigurasi Database

Aplikasi ini menggunakan database **MySQL**. Konfigurasi default terdapat pada file [`src/main/resources/application.yaml`](file:///c:/Users/TS%20Consultant/Desktop/Ian/Proyek/Bootcamp/EC8/ec8/src/main/resources/application.yaml):

```yaml
spring:
  application:
    name: ProductManagementApi
  datasource:
    url: jdbc:mysql://localhost:3306/db_product_management?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: YOUR_MYSQL_PASSWORD
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    open-in-view: false
    database-platform: org.hibernate.dialect.MySQLDialect
server:
  port: 8080

app:
  jwt:
    secret: ec8-product-catalog-jwt-secret-key-2026-change-me-in-production
    expiration-ms: 86400000   # 24 jam
```

> **Catatan:**
> - Tabel `products` **dan** `users` dibuat otomatis oleh Hibernate (`ddl-auto: update`).
> - Ubah `username` dan `password` sesuai kredensial MySQL lokal Anda.
> - `app.jwt.secret` adalah kunci penandatanganan token JWT (minimal 32 karakter). Di produksi, sebaiknya diganti dengan nilai dari environment variable.

---

## 👤 Akun Demo (Seed Data)

Saat aplikasi pertama kali dijalankan, jika tabel `users` masih kosong, otomatis dibuat 2 akun demo:

| Username | Password | Role |
|---|---|---|
| `admin` | `admin123` | `ADMIN` |
| `user` | `user123` | `USER` |

Selain itu, 6 produk sampel juga dibuat otomatis jika tabel `products` kosong, untuk memudahkan demo read-heavy & perbandingan response time.

---

## 💻 Cara Menjalankan Project

### 1. Prerequisites
- Java Development Kit (JDK) 17 atau versi yang lebih baru.
- MySQL Server versi 8.0+.
- Git & Maven Wrapper (sudah disertakan dalam proyek).

### 2. Menjalankan Aplikasi

Buka terminal di direktori utama proyek, lalu jalankan perintah berikut:

- **Windows (Command Prompt / PowerShell):**
  ```cmd
  mvnw.cmd spring-boot:run
  ```
  *atau jika menggunakan Bash/Git Bash:*
  ```bash
  ./mvnw spring-boot:run
  ```

Aplikasi akan berjalan di `http://localhost:8080`.

### 3. Menjalankan Automated Unit Test

Untuk menjalankan semua pengujian unit test (menggunakan H2 In-Memory Database):

```bash
./mvnw test
```

---

## 📡 Daftar Endpoint API & Hak Akses

**Base URL:** `http://localhost:8080`

| No | Method & Endpoint | Akses | Keterangan |
|---|---|---|---|
| 1 | `POST /api/auth/register` | **Public** | Registrasi user baru (role default: `USER`) |
| 2 | `POST /api/auth/login` | **Public** | Login, mengembalikan JWT token |
| 3 | `GET /api/products` | Authenticated (USER, ADMIN) | List produk — **read-heavy, di-cache** |
| 4 | `GET /api/products/{id}` | Authenticated (USER, ADMIN) | Detail produk — **read-heavy, di-cache** |
| 5 | `POST /api/products` | **ADMIN only** | Tambah produk baru |
| 6 | `PUT /api/products/{id}` | **ADMIN only** | Update produk — trigger `@CachePut` |
| 7 | `PATCH /api/products/{id}/sell` | **ADMIN only** | Update stok akibat penjualan (endpoint tambahan) |
| 8 | `DELETE /api/products/{id}` | **ADMIN only** | Hapus produk — trigger `@CacheEvict` |

---

## 🔐 Autentikasi & Otorisasi (JWT)

### Alur Penggunaan di Postman

> Alur lengkap sesuai assignment: **register → login (ambil JWT token) → akses endpoint dengan token → percobaan akses endpoint ADMIN menggunakan role USER (harus ditolak, 403)**.

**Persiapan:**
1. Buka **Postman**, buat Collection baru bernama `EC8 Product Catalog` agar semua request tersimpan rapi.
2. Base URL semua request: `http://localhost:8080`.
3. Saat ada request yang memerlukan token, gunakan tab **Authorization** → Type: **Bearer Token**. Ambil screenshot setiap response sebagai bukti untuk laporan.

#### Langkah 1 — Register (Public)

1. Buat request baru: **POST** `http://localhost:8080/api/auth/register`.
2. Klik tab **Body** → pilih **raw** → ubah tipe konten menjadi **JSON**.
3. Isi body:

```json
{
  "username": "budi",
  "email": "budi@example.com",
  "password": "password123"
}
```

4. Klik **Send**.

**Response (201 Created):**
```json
{
  "status": 200,
  "message": "Registrasi berhasil",
  "data": {
    "id": 1,
    "username": "budi",
    "email": "budi@example.com",
    "role": "USER",
    "token": null,
    "tokenType": null
  }
}
```

#### Langkah 2 — Login (mendapatkan JWT token)

1. Buat request baru: **POST** `http://localhost:8080/api/auth/login`.
2. Tab **Body** → **raw** → **JSON**, isi:

```json
{
  "username": "budi",
  "password": "password123"
}
```

3. Klik **Send**.

**Response (200 OK)** — salin nilai `data.token` (atau simpan otomatis, lihat Langkah 3):
```json
{
  "status": 200,
  "message": "Login berhasil",
  "data": {
    "id": 1,
    "username": "budi",
    "email": "budi@example.com",
    "role": "USER",
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJidWRpIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE3NTYwMDAwMDAsImV4cCI6MTc1NjA4NjQwMH0.xxxxxxxxxxxx",
    "tokenType": "Bearer"
  }
}
```

#### Langkah 3 — Simpan token secara otomatis (opsional, disarankan)

Agar tidak perlu menyalin token manual setiap kali, buka tab **Tests** pada request **Login** lalu tambahkan script berikut. Token akan otomatis tersimpan sebagai *collection variable* `token`:

```javascript
const jsonData = pm.response.json();
pm.collectionVariables.set("token", jsonData.data.token);
```

#### Langkah 4 — Akses endpoint dengan token

1. Buat request baru: **GET** `http://localhost:8080/api/products`.
2. Klik tab **Authorization** → Type: **Bearer Token** → isi Token dengan `{{token}}` (Postman otomatis mengambil dari collection variable). Jika tidak memakai variable, tempel token hasil login secara manual.
3. Klik **Send**.

**Response (200 OK):**
```json
{
  "status": 200,
  "message": "Sukses",
  "data": [
    {
      "id": 1,
      "name": "Laptop Gaming Pro",
      "price": 15000000.0,
      "description": "Laptop gaming high-end 16GB RAM",
      "stock": 10
    }
  ]
}
```

#### Langkah 5 — Percobaan akses ADMIN menggunakan role USER (harus ditolak 403)

1. Buat request baru: **POST** `http://localhost:8080/api/products` dengan Authorization Bearer token **USER** (hasil Langkah 2).
2. Tab **Body** → **raw** → **JSON**, isi:

```json
{
  "name": "Produk Ilegal",
  "price": 10000.0,
  "description": "dicoba oleh USER",
  "stock": 1
}
```

3. Klik **Send** — endpoint ini hanya boleh diakses ADMIN, sehingga:

**Response (403 Forbidden):**
```json
{
  "status": 403,
  "error": "FORBIDDEN",
  "message": "Akses ditolak. Anda tidak memiliki role yang cukup"
}
```

4. **Bukti otorisasi berjalan:** login ulang sebagai `admin/admin123` (akun demo, role ADMIN), ulangi request yang sama dengan token ADMIN → **Response (201 Created)**.
5. **Bukti autentikasi berjalan:** kirim `GET /api/products` **tanpa token** (Authorization: No Auth) → **Response (401 Unauthorized)**.

> Perbandingan **response time** sebelum vs sesudah caching diukur dari request `GET /api/products` di Postman (lihat bagian [Caching](#-caching-pada-spring-boot)).

---

## 📦 Detail Endpoint Produk & Format Request/Response

> **Catatan:** Endpoint mutasi (POST/PUT/PATCH/DELETE) hanya bisa diakses role `ADMIN`.
> Endpoint GET bisa diakses USER maupun ADMIN. Semua endpoint memerlukan `Authorization: Bearer <TOKEN>`.

### 1. Tambah Produk Baru (`POST /api/products`) — ADMIN only

**Request Body:**
```json
{
  "name": "Laptop Gaming Pro",
  "price": 15000000.0,
  "description": "High performance gaming laptop",
  "stock": 10
}
```

**Response (201 Created):**
```json
{
  "status": 200,
  "message": "Produk berhasil ditambahkan",
  "data": {
    "id": 1,
    "name": "Laptop Gaming Pro",
    "price": 15000000.0,
    "description": "High performance gaming laptop",
    "stock": 10
  }
}
```

### 2. Edit / Perbarui Produk (`PUT /api/products/{id}`) — ADMIN only, trigger `@CachePut`

**Request Body:**
```json
{
  "name": "Laptop Gaming Pro v2",
  "price": 16500000.0,
  "description": "Updated spec gaming laptop",
  "stock": 15
}
```

**Response (200 OK):**
```json
{
  "status": 200,
  "message": "Produk berhasil diperbarui",
  "data": {
    "id": 1,
    "name": "Laptop Gaming Pro v2",
    "price": 16500000.0,
    "description": "Updated spec gaming laptop",
    "stock": 15
  }
}
```

### 3. Update Stok Penjualan (`PATCH /api/products/{id}/sell`) — ADMIN only

**Request Body:**
```json
{
  "soldQuantity": 3
}
```

**Response (200 OK):**
```json
{
  "status": 200,
  "message": "Stok produk berhasil diperbarui",
  "data": {
    "id": 1,
    "name": "Laptop Gaming Pro v2",
    "price": 16500000.0,
    "description": "Updated spec gaming laptop",
    "stock": 12
  }
}
```

### 4. Hapus Produk (`DELETE /api/products/{id}`) — ADMIN only, trigger `@CacheEvict`

**Response (200 OK):**
```json
{
  "status": 200,
  "message": "Produk berhasil dihapus",
  "data": null
}
```

### 5. Detail Produk by ID (`GET /api/products/{id}`) — di-cache

**Response (200 OK):**
```json
{
  "status": 200,
  "message": "Sukses",
  "data": {
    "id": 1,
    "name": "Laptop Gaming Pro v2",
    "price": 16500000.0,
    "description": "Updated spec gaming laptop",
    "stock": 12
  }
}
```

### 6. Tampilkan Semua Produk (`GET /api/products`) — di-cache

**Response (200 OK):**
```json
{
  "status": 200,
  "message": "Sukses",
  "data": [
    {
      "id": 1,
      "name": "Laptop Gaming Pro v2",
      "price": 16500000.0,
      "description": "Updated spec gaming laptop",
      "stock": 12
    }
  ]
}
```

---

## ⚡ Caching pada Spring Boot

### Konsep

Endpoint `GET /api/products` dan `GET /api/products/{id}` sangat sering diakses (read-heavy), sementara datanya jarang berubah. Tanpa caching, setiap request akan melakukan query ke database. Dengan **caching**, hasil query pertama disimpan di memori (cache), sehingga request berikutnya untuk data yang sama langsung dilayani dari cache **tanpa menyentuh database** — response time menjadi jauh lebih cepat.

### Cache Provider: ConcurrentMapCache

Cache provider yang digunakan adalah **ConcurrentMapCache** (default Spring, in-memory sederhana) — tidak membutuhkan server eksternal. Konfigurasinya ada di [`CacheConfig.java`](src/main/java/com/assignment/ec8/config/CacheConfig.java):

```java
@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(List.of("products", "productById"));
        return cacheManager;
    }
}
```

Dua cache yang terdaftar:
- `products` — menyimpan hasil `GET /api/products` (list)
- `productById` — menyimpan hasil `GET /api/products/{id}` (detail, key = id produk)

### Penerapan Anotasi

| Endpoint / Method | Anotasi | Efek |
|---|---|---|
| `GET /api/products` (`getAllProducts`) | `@Cacheable("products")` | Hasil list disimpan di cache; request berikutnya tidak query DB |
| `GET /api/products/{id}` (`getProductById`) | `@Cacheable("productById", key = "#id")` | Hasil detail disimpan di cache per id |
| `PUT /api/products/{id}` (`updateProduct`) | `@CachePut("productById", key = "#id")` + `@CacheEvict("products")` | Data di cache **selalu diperbarui** mengikuti database; list di-evict agar tidak basi |
| `PATCH /api/products/{id}/sell` (`updateStockOnSale`) | `@CachePut("productById")` + `@CacheEvict("products")` | Stok terbaru langsung di-cache |
| `DELETE /api/products/{id}` (`deleteProduct`) | `@CacheEvict({"productById", "products"})` | Data yang dihapus tidak lagi tersimpan di cache |
| `POST /api/products` (`createProduct`) | `@CacheEvict("products")` | List di-evict agar produk baru muncul |

Implementasi lengkap ada di [`ProductService.java`](src/main/java/com/assignment/ec8/service/ProductService.java).

### Cara Mengukur & Membandingkan Response Time (Sebelum vs Sesudah Caching)

1. **Jalankan aplikasi** (`./mvnw spring-boot:run`) dan login untuk mendapatkan token.
2. Buka **Postman** → request `GET /api/products` dengan token Bearer.
3. **Skenario "sebelum caching"**: matikan caching sementara dengan mengomentari `@Cacheable` pada `getAllProducts()` di `ProductService.java`, restart aplikasi, lalu kirim request yang sama beberapa kali. Catat waktu response (di Postman: tab **Console**/info di bawah response body, atau lihat log).
4. **Skenario "sesudah caching"**: kembalikan `@Cacheable`, restart aplikasi, kirim request yang sama beberapa kali. Request pertama tetap lambat (cache miss → query DB), tetapi request ke-2 dan seterusnya **jauh lebih cepat** (cache hit).
5. Screenshot hasil Postman sebagai bukti perbandingan, lalu dokumentasikan di laporan.

> **Tips:** Karena ConcurrentMapCache berada dalam memori aplikasi, untuk membandingkan secara adil pastikan kedua skenario diuji pada kondisi yang sama (jumlah data, koneksi DB yang sama, dan gunakan beberapa request beruntun).

---

## ⚠️ Response Handling Errors

Semua error dikembalikan dalam format JSON standar:

```json
{
  "status": 400,
  "error": "BAD_REQUEST",
  "message": "Price wajib diisi, Name wajib diisi"
}
```

| HTTP Status | Kapan Terjadi |
|---|---|
| `400 BAD_REQUEST` | Validasi gagal, username/email sudah terdaftar, stok tidak valid |
| `401 UNAUTHORIZED` | Login gagal (username/password salah), token tidak ada/tidak valid/kedaluwarsa |
| `403 FORBIDDEN` | Token valid tetapi role tidak cukup (contoh: USER akses endpoint ADMIN) |
| `404 NOT_FOUND` | Data tidak ditemukan |
| `500 INTERNAL_SERVER_ERROR` | Error tak terduga di server |

---

## 🧪 Automated Unit Test

Suite test mencakup:
- **`ProductControllerTest`** — CRUD produk & validasi (9 test)
- **`AuthControllerTest`** — register (sukses, duplikat, email invalid) & login (sukses, password salah → 401)
- **`SecurityTest`** — 401 tanpa token, 200 dengan USER, 403 akses USER ke endpoint ADMIN, 201 dengan ADMIN
- **`JwtAuthFlowTest`** — alur JWT nyata: register → login → token → akses endpoint protected
- **`CacheTest`** — verifikasi `@Cacheable` mengisi cache, `@CachePut` memperbarui cache, `@CacheEvict` menghapus cache
- **`Ec8ApplicationTests`** — konteks aplikasi berjalan

Jalankan dengan:

```bash
./mvnw test
```
