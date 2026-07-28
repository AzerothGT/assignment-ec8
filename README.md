# Product Management API (EC6)

API RESTful berbasis **Spring Boot** untuk mengelola data produk (Product Management), termasuk penambahan produk, pembaruan data, update stok akibat penjualan, penghapusan, dan pencarian produk.

---

## 🚀 Teknologi

- **Java 17+**
- **Spring Boot 3.4+**
- **Spring Data JPA & Hibernate**
- **MySQL Database**
- **Jakarta Bean Validation** (Programmatic Validation via `RequestValidator`)
- **Lombok**
- **H2 Database** (untuk Pengujian Automated Unit Test)
- **Maven**

---

## 🛠️ Konfigurasi Database

Aplikasi ini menggunakan database **MySQL**. Konfigurasi default terdapat pada file [`src/main/resources/application.yaml`](file:///c:/Users/TS%20Consultant/Desktop/Ian/Proyek/Bootcamp/EC6/ec6/src/main/resources/application.yaml):

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
```

> **Catatan:**
> - Pastikan service **MySQL Server** di komputer Anda sudah berjalan pada port `3306`.
> - Parameter `createDatabaseIfNotExist=true` akan membuat database `db_product_management` secara otomatis jika belum ada.
> - Ubah `username` dan `password` sesuai dengan kredensial MySQL lokal Anda.

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

## 📡 Daftar Endpoint API

**Base URL:** `http://localhost:8080/api/products`

| HTTP Method | Endpoint | Deskripsi |
|---|---|---|
| `POST` | `/api/products` | Menambahkan produk baru |
| `PUT` | `/api/products/{id}` | Mengubah/Memperbarui data produk berdasarkan ID |
| `PATCH` | `/api/products/{id}/sell` | Mengurangi stok produk saat terjadi penjualan |
| `DELETE` | `/api/products/{id}` | Menghapus produk berdasarkan ID |
| `GET` | `/api/products/{id}` | Mendapatkan detail produk berdasarkan ID |
| `GET` | `/api/products` | Mendapatkan daftar semua produk |

---

### Detail Endpoint & Format Request/Response

#### 1. Tambah Produk Baru (`POST /api/products`)

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

---

#### 2. Edit / Perbarui Produk (`PUT /api/products/{id}`)

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

---

#### 3. Update Stok Penjualan (`PATCH /api/products/{id}/sell`)

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

---

#### 4. Hapus Produk (`DELETE /api/products/{id}`)

**Response (200 OK):**
```json
{
  "status": 200,
  "message": "Produk berhasil dihapus",
  "data": null
}
```

---

#### 5. Detail Produk by ID (`GET /api/products/{id}`)

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

---

#### 6. Tampilkan Semua Produk (`GET /api/products`)

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

## ⚠️ Response Handling Errors

Jika terjadi kesalahan validasi atau data tidak ditemukan, API akan mengembalikan format error standar berikut:

```json
{
  "status": 400,
  "error": "BAD_REQUEST",
  "message": "Price wajib diisi, Name wajib diisi"
}
```
