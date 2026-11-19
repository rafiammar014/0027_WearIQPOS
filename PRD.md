# PRODUCT REQUIREMENTS DOCUMENT (PRD)
**Project Name:** WearIQ POS (AI-Powered Clothing Store Point of Sale & Inventory)  
**Platform:** Android Native (Kotlin)  
**Architecture:** MVVM (Model-View-ViewModel) + Repository Pattern  
**UI Framework:** 100% Jetpack Compose (Material Design 3) + Navigation Compose  
**Backend & Database:** REST API (Node.js/Express) + PostgreSQL + Google Gemini API (`gemini-2.5-flash`)  
**Repository Target:** `4NIMBelakang_WearIQPOS` (Minimal 30 commit bertahap)

---

## 1. STRICT AI AGENT GUARDRAILS (ATURAN WAJIB KODE & ACUAN PEDOMAN PAM 2026)
1. **100% JETPACK COMPOSE (NO XML / NO VIEWS):** Seluruh layar, tata letak, komponen interaktif, dan dialog wajib dibangun murni menggunakan `@Composable`. Dilarang keras membuat atau menggunakan file layout XML.
2. **NO ROOM DATABASE:** Dilarang menggunakan SQLite/Room di perangkat. Seluruh entitas bisnis dikelola pada PostgreSQL melalui REST API (Retrofit2). Penyimpanan lokal hanya diizinkan untuk `DataStore Preferences` (token & sesi) serta File Storage (foto, struk, CSV).
3. **STRICT MVVM, STATE HOISTING & ONE-TIME EVENTS:** UI (`Screen`) dilarang memanggil Retrofit/Repository secara langsung. Semua state dikelola di `ViewModel` melalui `StateFlow<UiState<T>>` (`Idle`, `Loading`, `Success`, `Error`), sedangkan aksi sekali jalan (navigasi, Snackbar/Toast) wajib menggunakan `Channel` / `SharedFlow` (`UiEvent`) agar tidak terpicu ulang saat konfigurasi berubah.
4. **JWT AUTHENTICATION & REFRESH TOKEN:** Wajib menggunakan OkHttp `Interceptor` (`Authorization: Bearer <access_token>`) dan `Authenticator` untuk otomatis memperbarui token via `POST /api/auth/refresh` saat menerima HTTP `401 Unauthorized` (dengan batas retry maksimal 1x agar tidak terjadi *infinite loop* saat refresh token kedaluwarsa, lalu otomatis logout ke halaman Login).
5. **LOCAL FILE STORAGE (SAVE & READ):** Wajib mengimplementasikan simpan dan baca file lokal (`Context.filesDir`) untuk foto produk katalog, berkas struk digital (PDF/TXT), dan ekspor laporan (CSV). Wajib menyediakan *placeholder fallback* jika path file lokal tidak ditemukan.
6. **EDGE-TO-EDGE, INPUT VALIDATION & ZERO FATAL CRASH:**
    - Wajib menerapkan `statusBarsPadding()`, `navigationBarsPadding()`, dan `imePadding()` pada seluruh layar formulir agar tombol tidak terpotong *navigation bar* atau tertutup *keyboard*.
    - Semua pencarian real-time wajib menerapkan `debounce(300L)` pada `Flow` di `ViewModel`.
    - Konfigurasi `BASE_URL` wajib diletakkan di `BuildConfig` agar mudah berpindah antara Emulator (`10.0.2.2`) dan perangkat fisik (LAN IP).

---

## 2. UI/UX DESIGN SYSTEM (DARK NAVY, CREAM, RED + GLASSMORPHISM)
Terapkan sistem visual editorial modern yang konsisten pada `ui/theme/Color.kt` dan `Theme.kt` (Dark Mode Default, Status Bar Light Content):
- **60% Primary Canvas (`DarkNavy`):**
    - `DarkNavy`: `Color(0xFF0B132B)` (Kanvas latar utama wajib solid di root setiap screen untuk mencegah *ghosting* transisi)
    - `DarkNavySurface`: `Color(0xFF111B36)` (Dasar *bottom bar* dan *modal sheet*)
- **30% Surface & Editorial Contrast (`Glassmorphism` & `Cream`):**
    - `GlassSurface`: `Color(0x14F5EFE6)` dengan *border gradient* `1.dp` (`Cream 25%` -> transparan) untuk kartu Bento, input field, dan chip.
    - `CreamSolid`: `Color(0xFFF5EFE6)` digunakan untuk **Hero Card Dashboard** (teks Dark Navy), indikator *Segmented Pill* aktif, dan teks utama (`TextPrimary`).
    - `CreamMuted`: `Color(0xA6F5EFE6)` (65% alpha) untuk label, ikon input field, dan teks sekunder.
- **10% Accent (`CrimsonRed`):**
    - `CrimsonRed`: `Color(0xFFE63946)` untuk tombol CTA utama (Masuk, Buka Kasir, Bayar, Generate AI), indikator tab aktif, dan *badge* stok kritis.
    - `CrimsonRedLoading`: `Color(0xB3E63946)` saat tombol dalam state loading.
- **Semantic Colors:**
    - `SuccessEmerald`: `Color(0xFF10B981)` (Stok aman, pembayaran lunas, tren naik)
    - `WarningAmber`: `Color(0xFFF59E0B)` (Stok mendekati batas minimum)
    - `ErrorRose`: `Color(0xFFFF6B76)` (Pesan validasi error, stok kritis, pembatalan)
- **Aturan Anti-Template:**
    - Dilarang menggunakan emoji sistem (`👑`, `💼`, dll); gunakan `Icons.Rounded.*`.
    - Layar Login/Daftar menggunakan header rata kiri tanpa kotak pembungkus besar.
    - Dashboard menggunakan **Asymmetric Bento Grid** (Hero Card Cream + 2 Kartu Glass + AI Insight Banner + Ringkasan Transaksi Terakhir).
    - **Floating Glass Bottom Navigation Bar** bersifat persisten di level `Scaffold` untuk 4 tab utama (Beranda, Kasir, Katalog, AI Insights — tab AI Insights disembunyikan untuk role `KASIR`).

---

## 3. ROLE-BASED ACCESS CONTROL (UI & BACKEND ENFORCEMENT)
Otorisasi wajib ditegakkan pada **dua lapis**: penyembunyian menu di UI Compose **dan** middleware `requireRole(['OWNER'])` di Express backend (mengembalikan `403 Forbidden` jika dilanggar).

| Fitur / Hak Akses | Role: OWNER | Role: KASIR |
| :--- | :---: | :---: |
| **Login & Register Akun** | Ya | Ya |
| **Dashboard Omzet & Statistik** | Penuh (Seluruh Toko) | Terbatas (Transaksi Shift Sendiri) |
| **Kelola Pengguna & Staf (CRUDS Users)** | Ya (Penuh) | ❌ Diblokir (`403`) |
| **Kelola Kategori Pakaian (CRUDS Categories)** | Ya (Penuh) | Hanya Lihat (Read Only) |
| **Kelola Produk & Varian (CRUDS Products)** | Ya (Penuh + `harga_modal`) | Lihat & Cari (`harga_modal` dihapus dari JSON) |
| **Manajemen Restock Cepat** | Ya | ❌ Diblokir (`403`) |
| **Transaksi Kasir / POS (CRUDS Transactions)** | Ya (Semua) | Create, Read, Search, Update Catatan |
| **Pembatalan / Hapus Transaksi** | Ya (Otoritas Penuh) | ❌ Diblokir (`403`) |
| **WearIQ AI Insights (Gemini 2.5 Flash)** | Ya (Penuh) | ❌ Diblokir (`403`) |
| **Simpan & Baca Struk Digital (PDF/TXT)** | Ya | Ya |
| **Ekspor & Baca Laporan Penjualan (CSV)** | Ya | ❌ Diblokir (`403`) |

---

## 4. RELATIONAL DATABASE SCHEMA (POSTGRESQL - 5 ENTITAS UTAMA)

### Entitas 1: `users`
- `id`: SERIAL PRIMARY KEY
- `nama_lengkap`: VARCHAR(100) NOT NULL
- `username`: VARCHAR(50) UNIQUE NOT NULL
- `email`: VARCHAR(100) UNIQUE NOT NULL
- `password_hash`: TEXT NOT NULL (bcrypt)
- `role`: VARCHAR(20) NOT NULL CHECK (`role IN ('OWNER', 'KASIR')`)
- `is_active`: BOOLEAN NOT NULL DEFAULT TRUE *(untuk Soft Delete jika user sudah memiliki riwayat transaksi)*
- `refresh_token`: TEXT NULL
- `created_at`: TIMESTAMP DEFAULT CURRENT_TIMESTAMP

### Entitas 2: `categories`
- `id`: SERIAL PRIMARY KEY
- `nama_kategori`: VARCHAR(100) UNIQUE NOT NULL
- `deskripsi`: TEXT NULL
- `created_at`: TIMESTAMP DEFAULT CURRENT_TIMESTAMP

### Entitas 3: `product_variants`
- `id`: SERIAL PRIMARY KEY
- `category_id`: INT NOT NULL REFERENCES `categories(id)` ON DELETE RESTRICT
- `kode_sku`: VARCHAR(50) UNIQUE NOT NULL
- `nama_produk`: VARCHAR(150) NOT NULL
- `ukuran`: VARCHAR(10) NOT NULL (`'XS'`, `'S'`, `'M'`, `'L'`, `'XL'`, `'XXL'`)
- `warna`: VARCHAR(50) NOT NULL
- `harga_modal`: NUMERIC(12,2) NOT NULL CHECK (`harga_modal >= 0`)
- `harga_jual`: NUMERIC(12,2) NOT NULL CHECK (`harga_jual >= 0`)
- `stok`: INT NOT NULL DEFAULT 0 CHECK (`stok >= 0`)
- `batas_stok_min`: INT NOT NULL DEFAULT 5 CHECK (`batas_stok_min >= 0`)
- `local_image_uri`: TEXT NULL
- `is_active`: BOOLEAN NOT NULL DEFAULT TRUE *(mencegah error FK saat menghapus produk yang pernah terjual)*
- `updated_at`: TIMESTAMP DEFAULT CURRENT_TIMESTAMP

### Entitas 4: `transactions` & `transaction_items`
- **Tabel `transactions`:**
    - `id`: SERIAL PRIMARY KEY
    - `kode_invoice`: VARCHAR(50) UNIQUE NOT NULL (Format: `INV-YYYYMMDD-XXX`)
    - `user_id`: INT NOT NULL REFERENCES `users(id)` ON DELETE RESTRICT
    - `nama_pelanggan`: VARCHAR(100) DEFAULT `'Umum'`
    - `total_item`: INT NOT NULL
    - `total_harga`: NUMERIC(12,2) NOT NULL
    - `metode_pembayaran`: VARCHAR(30) NOT NULL CHECK (`metode_pembayaran IN ('TUNAI', 'QRIS', 'DEBIT')`)
    - `uang_bayar`: NUMERIC(12,2) NOT NULL
    - `kembalian`: NUMERIC(12,2) NOT NULL
    - `catatan`: TEXT NULL
    - `created_at`: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- **Tabel `transaction_items`:**
    - `id`: SERIAL PRIMARY KEY
    - `transaction_id`: INT NOT NULL REFERENCES `transactions(id)` ON DELETE CASCADE
    - `variant_id`: INT NOT NULL REFERENCES `product_variants(id)` ON DELETE RESTRICT
    - `qty`: INT NOT NULL CHECK (`qty > 0`)
    - `harga_satuan`: NUMERIC(12,2) NOT NULL
    - `subtotal`: NUMERIC(12,2) NOT NULL

### Entitas 5: `ai_insight_logs`
- `id`: SERIAL PRIMARY KEY
- `user_id`: INT NOT NULL REFERENCES `users(id)` ON DELETE RESTRICT
- `tipe_insight`: VARCHAR(50) NOT NULL (`'RESTOCK_PREDICTION'`, `'BESTSELLER_TREND'`, `'REVENUE_SUMMARY'`)
- `judul_insight`: VARCHAR(150) NOT NULL
- `konteks_data_json`: JSONB NOT NULL
- `hasil_analisis_ai`: TEXT NOT NULL
- `catatan_owner`: TEXT NULL
- `created_at`: TIMESTAMP DEFAULT CURRENT_TIMESTAMP

---

## 5. SPESIFIKASI 5 FITUR CRUDS PENUH & INTEGRITAS DATA
1. **CRUDS Manajemen Pengguna (`Users` - Owner Only):**
    - Create, Read, Update, Delete, Search (nama/username/email).
    - **Proteksi:** Owner tidak bisa menghapus akunnya sendiri yang sedang login (`400 Bad Request`). Jika user pernah memproses transaksi, lakukan *soft delete* (`is_active = false`) agar riwayat transaksi tetap utuh.
2. **CRUDS Kategori Pakaian (`Categories`):**
    - Create, Read (menyertakan `total_produk`), Update, Delete, Search.
    - **Proteksi:** Jika kategori masih memiliki produk aktif, kembalikan `409 Conflict` dengan pesan ramah di UI.
3. **CRUDS Produk & Varian (`Product Variants` + Restock Cepat):**
    - Create (dengan foto lokal), Read (filter kategori, ukuran, & `low_stock`), Update (termasuk aksi *Restock* atomik), Delete (*soft delete* jika pernah terjual, *hard delete* jika belum), Search multi-parameter (nama, SKU, ukuran, warna).
4. **CRUDS Transaksi Kasir (`Transactions`):**
    - **Create (Atomik):** Menggunakan SQL Transaction (`BEGIN ... COMMIT`). Server memvalidasi ketersediaan stok (`stok >= qty`), menghitung ulang `subtotal`, `total_harga`, dan `kembalian` dari harga DB, lalu memotong stok. Jika ada 1 item stoknya kurang, seluruh transaksi di-*rollback* (`409 Conflict`).
    - **Read & Search:** Daftar transaksi + pencarian invoice/nama pelanggan + detail item belanja.
    - **Update:** Mengubah `nama_pelanggan` atau `catatan`.
    - **Delete (Pembatalan Atomik - Owner Only):** Menghapus transaksi dalam SQL Transaction sekaligus mengembalikan (`+ qty`) seluruh stok `product_variants` terkait.
5. **CRUDS Analisis & Prediksi AI (`AI Insight Logs` - Gemini 2.5 Flash):**
    - **Create (Generate):** Backend mengagregasi data 30 hari terakhir (produk terlaris, omzet, stok <= batas minimum), mengirim prompt terstruktur ke `gemini-2.5-flash` (dengan instruksi output teks bersih/terstruktur tanpa simbol markdown berantakan), lalu menyimpan ke DB.
    - **Read, Update (`judul_insight` & `catatan_owner`), Delete, Search.**
    - **OkHttp Timeout:** Konfigurasi `readTimeout(35, TimeUnit.SECONDS)` khusus panggilan AI agar tidak mudah *SocketTimeoutException*.

---

## 6. FITUR PENYIMPANAN FILE LOKAL (SAVE & READ COMPLIANCE)
1. **Foto Katalog Produk:** Disimpan ke `context.filesDir/product_images/<sku>.jpg` via `ActivityResultContracts.GetContent()`. Dibaca menggunakan **Coil** dengan fallback ikon pakaian bila file tidak ditemukan.
2. **Struk Digital Kasir (PDF/TXT):** Di-generate dari layar Detail Transaksi ke `context.filesDir/receipts/<kode_invoice>.pdf` (menggunakan `android.graphics.pdf.PdfDocument`) atau `.txt`, dan dapat dibaca kembali di layar *Receipt Viewer Composable* (beserta daftar struk tersimpan).
3. **Ekspor & Arsip Laporan Penjualan (CSV - Owner Only):** Mengompilasi riwayat transaksi ke `context.filesDir/reports/laporan_<timestamp>.csv` dan menampilkan layar *Arsip Laporan Lokal* untuk membaca serta merender isi tabel CSV langsung di UI Compose.

---

## 7. REST API ENDPOINTS CONTRACT & RESPONSE ENVELOPE
Seluruh endpoint wajib mengembalikan struktur JSON konsisten:
```json
{
  "success": true,
  "message": "Deskripsi singkat status",
  "data": { }
}


## Rekomendasi Tambahan untuk Memperkuat Proyek & Demo

* **Siapkan `seed.js` dengan Data 30 Hari Realistis:** Fitur unggulan Anda adalah **WearIQ AI Insights**. Jika database hanya berisi 1–2 transaksi saat ujian, jawaban Gemini akan terasa generik. Dengan *seeder* berisi pola penjualan nyata (misalnya *Kemeja Linen Putih M* laku keras dan stok sisa 2, sementara *Jaket Denim XL* sepi peminat), analisis prediksi restock dan tren busana dari Gemini akan terlihat sangat meyakinkan di depan penguji.
* **Gunakan *Cart Bottom Sheet* di Layar Kasir (POS):** Daripada memindahkan kasir ke halaman baru (`CartScreen`) setiap kali mengecek keranjang, gunakan *sticky floating checkout bar* di bagian bawah layar POS (menampilkan `"3 Item • Rp 450.000 — Bayar"`) yang membuka `ModalBottomSheet` saat diketuk. Alur kasir jadi jauh lebih cepat dan hemat satu perpindahan layar.
* **Tambahkan Pintu Akses Fitur Khusus Owner di Dashboard:** Karena `Floating Bottom Bar` hanya ideal memuat 4 tab (`Beranda`, `Kasir`, `Katalog`, `AI Insight`), sediakan tombol akses cepat di halaman **Beranda (Khusus Owner)** atau di *Menu Profil/Settings* untuk menuju **Manajemen Staf (`Users`)**, **Kelola Kategori**, dan **Arsip Laporan CSV** agar semua 5 fitur CRUDS dan File Storage mudah dijangkau dalam 1–2 ketukan saat demo.
* **Pecah Target 30+ Commit Berdasarkan Modul:** Agar tidak menumpuk di akhir, jadikan setiap sub