-- ====================================================================
-- DATABASE INITIALIZATION SCRIPT: WearIQ POS
-- PostgreSQL 14+ Compatible
-- ====================================================================

-- 1. Tabel Users
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    nama_lengkap VARCHAR(100) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('OWNER', 'KASIR')),
    refresh_token TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabel Categories
CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    nama_kategori VARCHAR(100) NOT NULL,
    deskripsi TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Tabel Product Variants
CREATE TABLE IF NOT EXISTS product_variants (
    id SERIAL PRIMARY KEY,
    category_id INT NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
    kode_sku VARCHAR(50) UNIQUE NOT NULL,
    nama_produk VARCHAR(150) NOT NULL,
    ukuran VARCHAR(10) NOT NULL CHECK (ukuran IN ('S', 'M', 'L', 'XL', 'XXL')),
    warna VARCHAR(50) NOT NULL,
    harga_modal NUMERIC(12,2) NOT NULL DEFAULT 0,
    harga_jual NUMERIC(12,2) NOT NULL DEFAULT 0,
    stok INT NOT NULL DEFAULT 0,
    batas_stok_min INT NOT NULL DEFAULT 5,
    local_image_uri TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Tabel Transactions
CREATE TABLE IF NOT EXISTS transactions (
    id SERIAL PRIMARY KEY,
    kode_invoice VARCHAR(50) UNIQUE NOT NULL,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    nama_pelanggan VARCHAR(100) DEFAULT 'Pelanggan Umum',
    total_item INT NOT NULL DEFAULT 0,
    total_harga NUMERIC(12,2) NOT NULL DEFAULT 0,
    metode_pembayaran VARCHAR(30) NOT NULL CHECK (metode_pembayaran IN ('TUNAI', 'QRIS', 'DEBIT')),
    uang_bayar NUMERIC(12,2) NOT NULL DEFAULT 0,
    kembalian NUMERIC(12,2) NOT NULL DEFAULT 0,
    catatan TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Tabel Transaction Items
CREATE TABLE IF NOT EXISTS transaction_items (
    id SERIAL PRIMARY KEY,
    transaction_id INT NOT NULL REFERENCES transactions(id) ON DELETE CASCADE,
    variant_id INT NOT NULL REFERENCES product_variants(id) ON DELETE RESTRICT,
    qty INT NOT NULL DEFAULT 1,
    harga_satuan NUMERIC(12,2) NOT NULL DEFAULT 0,
    subtotal NUMERIC(12,2) NOT NULL DEFAULT 0
);

-- 6. Tabel AI Insight Logs
CREATE TABLE IF NOT EXISTS ai_insight_logs (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    tipe_insight VARCHAR(50) NOT NULL,
    judul_insight VARCHAR(150) NOT NULL,
    konteks_data_json JSONB NOT NULL,
    hasil_analisis_ai TEXT NOT NULL,
    catatan_owner TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ====================================================================
-- SEED DATA AWAL (DEFAULT ACCOUNTS & SAMPLE DATA)
-- Password default: "owner123" dan "kasir123"
-- ====================================================================

-- Akun Default:
-- 1. Owner: owner@weariq.com / owner123
-- 2. Kasir: kasir@weariq.com / kasir123
INSERT INTO users (nama_lengkap, username, email, password_hash, role)
VALUES
('Budi Owner', 'owner_budi', 'owner@weariq.com', '$2a$10$wNqMpvV3zW1W9.xS2ZkVOexzTcvq7L0L24dYf18y4fR6W9aT73OaO', 'OWNER'),
('Siti Kasir', 'kasir_siti', 'kasir@weariq.com', '$2a$10$gT9Ym/s1uL5uX6.gH5ZpNeA6Tevn1x0N35dZf29z5gS7X0bU84PbP', 'KASIR')
ON CONFLICT (email) DO NOTHING;

-- Kategori Busana Awal
INSERT INTO categories (nama_kategori, deskripsi)
VALUES
('Kaos & T-Shirt', 'Koleksi kaos polos combed 24s dan 30s premium'),
('Kemeja', 'Kemeja formal, kasual flanel, dan oxford shirt'),
('Celana Chino & Jeans', 'Celana panjang chino slim fit dan denim casual'),
('Outerwear & Jaket', 'Hoodie zipper, sweater crewneck, dan coach jacket')
ON CONFLICT DO NOTHING;

-- Varian Produk Awal
INSERT INTO product_variants (category_id, kode_sku, nama_produk, ukuran, warna, harga_modal, harga_jual, stok, batas_stok_min)
VALUES
(1, 'TSH-BLK-L', 'Kaos Polos Combed 24s Hitam', 'L', 'Hitam Solid', 45000, 89000, 25, 5),
(1, 'TSH-WHT-M', 'Kaos Polos Combed 24s Putih', 'M', 'Putih Cerah', 45000, 89000, 4, 5),
(2, 'KMJ-NVY-XL', 'Kemeja Oxford Button Down Navy', 'XL', 'Navy Blue', 85000, 169000, 15, 3),
(3, 'CHN-KHK-32', 'Celana Chino Slim Fit Khaki', 'L', 'Khaki', 95000, 189000, 8, 4),
(4, 'HD-GRY-L', 'Hoodie Fleece Heavyweight Abu', 'L', 'Abu Misty', 110000, 229000, 2, 5)
ON CONFLICT (kode_sku) DO NOTHING;
