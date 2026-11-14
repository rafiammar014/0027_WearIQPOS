# PRODUCT REQUIREMENTS DOCUMENT (PRD)
**Project Name:** WearIQ POS (AI-Powered Clothing Store Point of Sale & Inventory)  
**Platform:** Android Native (Kotlin)  
**Architecture:** MVVM (Model-View-ViewModel) + Repository Pattern  
**UI Framework:** 100% Jetpack Compose (Material Design 3) + Navigation Compose  
**Backend & Database:** REST API (Node.js/Express) + PostgreSQL + Google Gemini API (`gemini-2.5-flash`)  
**Repository Target:** `4NIMBelakang_WearIQPOS` (Minimal 30 commit bertahap)

---

## 1. STRICT AI AGENT GUARDRAILS (ATURAN WAJIB KODE & ACUAN PEDOMAN PAM 2026)
1. **100% JETPACK COMPOSE (NO XML / NO VIEWS):** Seluruh layar, tata letak, komponen interaktif, dan dialog wajib dibangun murni menggunakan `@Composable`. Dilarang keras membuat atau menggunakan file layout XML (pelanggaran terhadap aturan ini berakibat nilai 0 sesuai pedoman).
2. **NO ROOM DATABASE:** Sesuai Pedoman Proyek Akhir PAM 2026, penggunaan SQLite/Room di perangkat dilarang. Seluruh data entitas bisnis disimpan dan dikelola pada database server PostgreSQL melalui REST API (Retrofit2).
3. **STRICT MVVM & STATE HOISTING:** UI (`Screen`) tidak boleh memanggil Retrofit/Repository secara langsung atau menyimpan *business logic*. Semua *state* dikelola di `ViewModel` menggunakan `StateFlow` dan sealed interface `UiState` (`Loading`, `Success`, `Error`).
4. **JWT AUTHENTICATION & REFRESH TOKEN:** Wajib menggunakan OkHttp `Interceptor` untuk menyisipkan header `Authorization: Bearer <access_token>` dan `Authenticator` untuk otomatis memperbarui token menggunakan `refresh_token` saat menerima HTTP `401 Unauthorized`.
5. **LOCAL FILE STORAGE (SAVE & READ):** Wajib mengimplementasikan fungsionalitas **simpan dan baca** file lokal pada memori perangkat (`Context.filesDir` / `MediaStore`) untuk foto produk katalog serta berkas struk digital (PDF/TXT) dan ekspor laporan (CSV).
6. **INPUT VALIDATION & ZERO FATAL CRASH:** Setiap formulir input wajib memiliki validasi data yang ketat (misal: format email, konfirmasi password, nominal pembayaran >= total belanja, stok >= 0) guna mencegah crash atau bug fatal saat pengujian dan demo.

---

## 2. UI/UX DESIGN SYSTEM (ATURAN WARNA 60 - 30 - 10)
Terapkan palet warna modern, kontras tinggi, dan elegan pada `ui/theme/Color.kt` dan `Theme.kt` (Dark Theme Default):
- **60% Primary Background (`ObsidianBlack`):** `Color(0xFF0B0D12)` -> Digunakan untuk `background` dan kanvas layar utama.
- **30% Secondary Surface (`MidnightNavy`):** `Color(0xFF161F33)` dan `MidnightNavyElevated = Color(0xFF1E2942)` -> Digunakan untuk `surface`, `surfaceVariant`, `Card`, `NavigationBar`, dan `OutlinedTextField`.
- **10% Accent (`ElectricCyan`):** `Color(0xFF38BDF8)` dengan `onPrimary = Color(0xFF06131D)` -> Digunakan untuk tombol aksi utama (CTA), ikon tab aktif, chip filter aktif, indikator AI, dan fokus input.
- **Text & Semantic Colors:**
  - `TextPrimary`: `Color(0xFFF8FAFC)` (Teks judul dan isi utama)
  - `TextSecondary`: `Color(0xFF94A3B8)` (Teks label, caption, dan placeholder)
  - `SuccessEmerald`: `Color(0xFF10B981)` (Indikator stok aman, status pembayaran lunas)
  - `ErrorRose`: `Color(0xFFF43F5E)` (Indikator stok menipis/kritis, tombol pembatalan, pesan validasi error)
  - `WarningAmber`: `Color(0xFFF59E0B)` (Indikator peringatan dan stok mendekati batas minimum)

---

## 3. ROLE-BASED ACCESS CONTROL (AUTHORIZATION)
Aplikasi mendukung 2 peran pengguna (*Role*) yang tersimpan dalam database dan dikembalikan melalui klaim JWT saat autentikasi:

| Fitur / Hak Akses | Role: OWNER | Role: KASIR |
| :--- | :---: | :---: |
| **Login & Register Akun** | Ya | Ya |
| **Dashboard Omzet & Statistik Penjualan** | Ya (Penuh) | Terbatas (Statistik Shift Kasir) |
| **Kelola Pengguna & Staf (CRUDS Users)** | Ya (Penuh) | ❌ Diblokir |
| **Kelola Kategori Pakaian (CRUDS Categories)** | Ya (Penuh) | Hanya Lihat (Read Only) |
| **Kelola Produk & Varian Baju (CRUDS Products)** | Ya (Penuh + Harga Modal) | Hanya Lihat & Cari (Tanpa Harga Modal) |
| **Transaksi Kasir / POS (CRUDS Transactions)** | Ya | Ya (Fitur Utama Kasir) |
| **Pembatalan / Hapus Transaksi** | Ya (Otoritas Penuh) | ❌ Butuh Otorisasi Owner |
| **WearIQ AI Insights (Gemini 2.5 Flash)** | Ya (Penuh) | ❌ Diblokir |
| **Simpan & Baca Struk Digital (PDF/TXT)** | Ya | Ya |
| **Ekspor & Baca Laporan Penjualan (CSV)** | Ya | ❌ Diblokir |

---

## 4. RELATIONAL DATABASE SCHEMA (POSTGRESQL - 5 ENTITAS UTAMA)

### Entitas 1: `users`
Tabel manajemen staf dan pemilik toko:
- `id`: SERIAL PRIMARY KEY
- `nama_lengkap`: VARCHAR(100) NOT NULL
- `username`: VARCHAR(50) UNIQUE NOT NULL
- `email`: VARCHAR(100) UNIQUE NOT NULL
- `password_hash`: TEXT NOT NULL
- `role`: VARCHAR(20) NOT NULL (`'OWNER'` | `'KASIR'`)
- `refresh_token`: TEXT NULL
- `created_at`: TIMESTAMP DEFAULT CURRENT_TIMESTAMP

### Entitas 2: `categories`
Tabel pengelompokan jenis pakaian:
- `id`: SERIAL PRIMARY KEY
- `nama_kategori`: VARCHAR(100) NOT NULL (Contoh: Kemeja, Kaos Polos, Celana Chino, Outerwear, Dress)
- `deskripsi`: TEXT NULL
- `created_at`: TIMESTAMP DEFAULT CURRENT_TIMESTAMP

### Entitas 3: `product_variants`
Tabel varian pakaian (berelasi dengan `categories`):
- `id`: SERIAL PRIMARY KEY
- `category_id`: INT NOT NULL REFERENCES `categories(id)` ON DELETE RESTRICT
- `kode_sku`: VARCHAR(50) UNIQUE NOT NULL (Contoh: `TSH-BLK-L-001`)
- `nama_produk`: VARCHAR(150) NOT NULL
- `ukuran`: VARCHAR(10) NOT NULL (`'S'`, `'M'`, `'L'`, `'XL'`, `'XXL'`)
- `warna`: VARCHAR(50) NOT NULL
- `harga_modal`: NUMERIC(12,2) NOT NULL
- `harga_jual`: NUMERIC(12,2) NOT NULL
- `stok`: INT NOT NULL DEFAULT 0
- `batas_stok_min`: INT NOT NULL DEFAULT 5
- `local_image_uri`: TEXT NULL (Path foto katalog pakaian di penyimpanan lokal perangkat)
- `updated_at`: TIMESTAMP DEFAULT CURRENT_TIMESTAMP

### Entitas 4: `transactions` & `transaction_items`
Tabel transaksi kasir dan rincian belanja pakaian:
- **Tabel `transactions`:**
  - `id`: SERIAL PRIMARY KEY
  - `kode_invoice`: VARCHAR(50) UNIQUE NOT NULL (Contoh: `INV-20261114-001`)
  - `user_id`: INT NOT NULL REFERENCES `users(id)` ON DELETE RESTRICT
  - `nama_pelanggan`: VARCHAR(100) DEFAULT `'Umum'`
  - `total_item`: INT NOT NULL
  - `total_harga`: NUMERIC(12,2) NOT NULL
  - `metode_pembayaran`: VARCHAR(30) NOT NULL (`'TUNAI'`, `'QRIS'`, `'DEBIT'`)
  - `uang_bayar`: NUMERIC(12,2) NOT NULL
  - `kembalian`: NUMERIC(12,2) NOT NULL
  - `catatan`: TEXT NULL
  - `created_at`: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- **Tabel `transaction_items`:**
  - `id`: SERIAL PRIMARY KEY
  - `transaction_id`: INT NOT NULL REFERENCES `transactions(id)` ON DELETE CASCADE
  - `variant_id`: INT NOT NULL REFERENCES `product_variants(id)` ON DELETE RESTRICT
  - `qty`: INT NOT NULL
  - `harga_satuan`: NUMERIC(12,2) NOT NULL
  - `subtotal`: NUMERIC(12,2) NOT NULL

### Entitas 5: `ai_insight_logs`
Tabel rekaman analisis kecerdasan buatan berbasis Google Gemini API:
- `id`: SERIAL PRIMARY KEY
- `user_id`: INT NOT NULL REFERENCES `users(id)` ON DELETE RESTRICT
- `tipe_insight`: VARCHAR(50) NOT NULL (`'RESTOCK_PREDICTION'`, `'BESTSELLER_TREND'`, `'REVENUE_SUMMARY'`)
- `judul_insight`: VARCHAR(150) NOT NULL
- `konteks_data_json`: JSONB NOT NULL (Ringkasan agregasi data stok & penjualan yang dianalisis)
- `hasil_analisis_ai`: TEXT NOT NULL (Uraian rekomendasi naratif dari Gemini 2.5 Flash)
- `catatan_owner`: TEXT NULL (Catatan tindak lanjut manual oleh pemilik toko)
- `created_at`: TIMESTAMP DEFAULT CURRENT_TIMESTAMP

---

## 5. SPESIFIKASI 5 FITUR CRUDS PENUH (CREATE, READ, UPDATE, DELETE, SEARCH)

Semua 5 fitur di bawah ini wajib menyediakan kelima aksi secara lengkap:
1. **CRUDS Manajemen Pengguna (`Users` - Khusus Owner):**
   - **Create:** Mendaftarkan akun kasir atau staf baru dengan username, email, nama lengkap, dan role.
   - **Read:** Menampilkan daftar seluruh staf toko beserta role dan status akun.
   - **Update:** Mengubah profil staf, memperbarui email, dan mengatur ulang role.
   - **Delete:** Menghapus akun staf yang sudah dinonaktifkan.
   - **Search:** Pencarian staf secara *real-time* berdasarkan nama lengkap, username, atau email.

2. **CRUDS Kategori Pakaian (`Categories`):**
   - **Create:** Menambahkan kategori pakaian baru (misal: "Kemeja Formal", "Outerwear").
   - **Read:** Menampilkan katalog kategori pakaian lengkap dengan jumlah varian produk di dalamnya.
   - **Update:** Memperbarui nama dan deskripsi kategori.
   - **Delete:** Menghapus kategori pakaian (dengan proteksi validasi jika masih ada produk terikat).
   - **Search:** Mencari kategori pakaian berdasarkan nama kategori.

3. **CRUDS Produk & Varian Ukuran/Warna (`Product Variants`):**
   - **Create:** Menambahkan varian pakaian baru (nama, SKU, ukuran, warna, harga modal, harga jual, stok, batas minimum) dan memilih foto produk lokal.
   - **Read:** Menampilkan katalog baju dengan visual foto Coil, filter kategori, badge ukuran/warna, dan indikator warna stok (Emerald jika aman, Rose jika stok <= batas minimum).
   - **Update:** Mengubah data produk, menambah/memperbarui stok (restock), menyesuaikan harga jual, atau memperbarui foto pakaian.
   - **Delete:** Menghapus varian pakaian dari katalog.
   - **Search:** Pencarian *multi-parameter* berdasarkan nama pakaian, kode SKU, ukuran, atau warna.

4. **CRUDS Transaksi Penjualan Kasir (`Transactions`):**
   - **Create:** Memilih varian produk ke keranjang belanja POS, menghitung total, input uang bayar, kalkulasi kembalian otomatis, dan menyimpan transaksi secara atomik (backend memotong stok produk secara otomatis).
   - **Read:** Melihat riwayat transaksi penjualan beserta rincian item belanja dan invoice.
   - **Update:** Memperbarui data catatan transaksi atau nama pelanggan pada invoice.
   - **Delete:** Membatalkan/menghapus transaksi penjualan (backend secara otomatis mengembalikan jumlah stok produk yang dibatalkan).
   - **Search:** Mencari riwayat transaksi berdasarkan nomor invoice atau nama pelanggan.

5. **CRUDS Analisis & Prediksi AI (`AI Insight Logs` - Google Gemini API):**
   - **Create (Generate):** Owner memicu analisis baru (Prediksi Restock, Tren Produk Terlaris, atau Ringkasan Omzet). Backend mengagregasi data transaksi & stok 30 hari terakhir, mengirim prompt terstruktur ke Google Gemini API (`gemini-2.5-flash`), dan menyimpan hasilnya.
   - **Read:** Membaca daftar kartu wawasan AI dan membuka rincian teks analisis serta rekomendasi aksi.
   - **Update:** Owner dapat memperbarui `judul_insight` dan menambahkan `catatan_owner` untuk dokumentasi strategi bisnis.
   - **Delete:** Menghapus riwayat insight AI yang sudah tidak relevan.
   - **Search:** Mencari wawasan AI berdasarkan kata kunci judul, tipe insight, atau isi rekomendasi.

---

## 6. FITUR PENYIMPANAN FILE LOKAL (SAVE & READ COMPLIANCE)
Memenuhi persyaratan wajib materi ajar *File Storage* (simpan dan baca file pada penyimpanan internal perangkat):
1. **Foto Katalog Produk (Internal Image Storage):**
   - **Simpan:** Saat menambah/mengedit produk, foto diambil dari galeri/kamera via `ActivityResultContracts.GetContent()`, disalin ke `context.filesDir/product_images/<sku>.jpg`.
   - **Baca:** Aplikasi membaca path file lokal tersebut dan menampilkannya pada komponen `@Composable` menggunakan library **Coil**.
2. **Struk Digital Kasir (PDF / TXT Receipt):**
   - **Simpan:** Pada halaman Detail Transaksi, tombol *"Simpan Struk Digital"* men-generate struk transaksi belanja ke direktori `context.filesDir/receipts/` (berupa PDF via `PdfDocument` atau teks struk terformat).
   - **Baca:** Tombol *"Buka Struk Tersimpan"* membaca isi berkas struk tersebut dan menampilkannya di dalam layar *Receipt Viewer Composable* aplikasi.
3. **Ekspor & Arsip Laporan Penjualan (CSV Export):**
   - **Simpan:** Pada layar Dashboard Owner, tombol *"Ekspor Rekap (.csv)"* mengompilasi riwayat transaksi ke format CSV dan menyimpannya di direktori aplikasi.
   - **Baca:** Layar *Arsip Laporan Lokal* membaca daftar file CSV yang pernah diekspor dan menampilkan isi tabelnya di UI.

---

## 7. REST API ENDPOINTS CONTRACT (BACKEND SPECIFICATION)
- **Autentikasi:**
  - `POST /api/auth/register` -> Registrasi pengguna baru.
  - `POST /api/auth/login` -> Login dengan email & password, mengembalikan `{ accessToken, refreshToken, user }`.
  - `POST /api/auth/refresh` -> Memperbarui `accessToken` menggunakan `refreshToken`.
  - `POST /api/auth/logout` -> Menghapus refresh token pada database server.
- **Pengguna (`users`):**
  - `GET /api/users?q=` -> List staf & pencarian.
  - `POST /api/users` -> Tambah staf baru (Owner only).
  - `PUT /api/users/:id` -> Edit staf (Owner only).
  - `DELETE /api/users/:id` -> Hapus staf (Owner only).
- **Kategori (`categories`):**
  - `GET /api/categories?q=` -> List kategori & pencarian.
  - `POST /api/categories` -> Tambah kategori (Owner only).
  - `PUT /api/categories/:id` -> Edit kategori (Owner only).
  - `DELETE /api/categories/:id` -> Hapus kategori (Owner only).
- **Produk (`product_variants`):**
  - `GET /api/products?q=&category_id=&ukuran=` -> List produk, filter kategori/ukuran, & pencarian.
  - `POST /api/products` -> Tambah varian produk (Owner only).
  - `PUT /api/products/:id` -> Update produk/stok (Owner only).
  - `DELETE /api/products/:id` -> Hapus varian produk (Owner only).
- **Transaksi (`transactions`):**
  - `GET /api/transactions?q=` -> List riwayat transaksi & pencarian invoice.
  - `POST /api/transactions` -> Simpan transaksi kasir baru (potong stok atomik).
  - `PUT /api/transactions/:id` -> Update catatan / nama pelanggan.
  - `DELETE /api/transactions/:id` -> Batalkan transaksi (kembalikan stok produk).
- **AI Insights (`ai_insight_logs`):**
  - `POST /api/ai/generate` -> Pemicu agregasi backend + panggilan Gemini API + insert log.
  - `GET /api/ai/insights?q=&type=` -> List riwayat analisis AI.
  - `PUT /api/ai/insights/:id` -> Edit judul / catatan owner.
  - `DELETE /api/ai/insights/:id` -> Hapus log insight.

---

## 8. STRUKTUR ARSITEKTUR KODE & PACKAGE (MVVM ANDROID)
```text
com.example.finalproject/
├── data/
│   ├── model/                  # Data classes: User, Category, ProductVariant, Transaction, AiInsightLog, DTOs
│   ├── remote/                 # ApiClient, ApiService, AuthInterceptor, TokenAuthenticator
│   └── repository/             # WearIqRepository (Single Source of Truth dari REST API)
├── ui/
│   ├── components/             # Reusable Composables (AppButton, AppTextField, ProductCard, CartItemRow, etc.)
│   ├── navigation/             # Screen (Sealed class rute), WearIqNavGraph (NavHost & Routing)
│   ├── screens/
│   │   ├── auth/               # LoginScreen, RegisterScreen, AuthViewModel
│   │   ├── dashboard/          # DashboardScreen (Owner/Kasir), DashboardViewModel
│   │   ├── catalog/            # CatalogScreen, ProductFormScreen, CategoryScreen, CatalogViewModel
│   │   ├── pos/                # PosScreen, CartScreen, TransactionHistoryScreen, ReceiptScreen, PosViewModel
│   │   ├── restock/            # RestockScreen, RestockViewModel
│   │   ├── ai_insight/         # AiInsightScreen, AiInsightDetailScreen, AiInsightViewModel
│   │   └── users/              # UserManagementScreen, UserViewModel
│   └── theme/                  # Color.kt (60-30-10 Dark Palette), Theme.kt, Type.kt
├── utils/
│   ├── TokenManager.kt         # DataStore Preferences khusus access_token, refresh_token, role
│   └── FileStorageHelper.kt    # Helper simpan & baca foto lokal, PDF struk, dan CSV
└── MainActivity.kt             # Single Activity, enableEdgeToEdge, Scaffold & WearIqNavGraph
```

---

## 9. KETENTUAN TEKNIS & CHECKLIST PENILAIAN (PEDOMAN PAM 2026)
Sebagai pedoman evaluasi diri sebelum presentasi dan demo:
- [x] **Format Repositori:** Nama repositori GitHub sesuai format `4NIMBelakang_NamaProject`.
- [x] **Commit Bertahap (Minimal 30 Commit):** Pengerjaan dilakukan bertahap dan teratur dengan pesan commit deskriptif (Dilarang push "sekali jadi").
- [x] **README.md Proyek:** Terdapat dokumentasi deskripsi aplikasi, panduan instalasi, dan checklist update progress fitur mingguan.
- [x] **Bebas Crash / Bug Fatal:** Menangani seluruh kemungkinan error jaringan, token kedaluwarsa, dan form validation secara elegan.
- [x] **Kesiapan Demo (15-30 Menit):** Aplikasi siap dijalankan di emulator/perangkat sebelum memasuki ruang ujian, mampu memaparkan alur login, 5 fitur CRUDS, dan integrasi AI secara runtut.
