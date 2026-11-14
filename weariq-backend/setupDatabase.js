const { Client } = require('pg');
const fs = require('fs');
const path = require('path');
require('dotenv').config();

async function setup() {
  const user = process.env.DB_USER || 'postgres';
  const password = process.env.DB_PASSWORD || '';
  const host = process.env.DB_HOST || 'localhost';
  const port = parseInt(process.env.DB_PORT || '5432', 10);
  const targetDb = process.env.DB_NAME || 'weariq_pos_db';

  console.log(`Menghubungkan ke PostgreSQL sebagai user '${user}'...`);

  // 1. Hubungkan ke database default 'postgres' untuk membuat database target
  const adminClient = new Client({
    user,
    password,
    host,
    port,
    database: 'postgres',
  });

  try {
    await adminClient.connect();
    console.log('✅ Berhasil terhubung ke server PostgreSQL.');

    const checkDb = await adminClient.query(
      "SELECT 1 FROM pg_database WHERE datname = $1",
      [targetDb]
    );

    if (checkDb.rows.length === 0) {
      console.log(`Membuat database '${targetDb}'...`);
      await adminClient.query(`CREATE DATABASE "${targetDb}"`);
      console.log(`✅ Database '${targetDb}' berhasil dibuat!`);
    } else {
      console.log(`ℹ️ Database '${targetDb}' sudah ada.`);
    }
  } catch (err) {
    console.error('❌ Gagal memeriksa/membuat database:', err.message);
    process.exit(1);
  } finally {
    await adminClient.end();
  }

  // 2. Hubungkan ke database target 'weariq_pos_db' dan jalankan init.sql
  console.log(`Menghubungkan ke database '${targetDb}' untuk menjalankan skrip tabel...`);
  const appClient = new Client({
    user,
    password,
    host,
    port,
    database: targetDb,
  });

  try {
    await appClient.connect();
    const sqlPath = path.join(__dirname, 'db', 'init.sql');
    const sqlContent = fs.readFileSync(sqlPath, 'utf8');

    console.log(`Mengeksekusi berkas init.sql...`);
    await appClient.query(sqlContent);
    console.log(`✅ Semua tabel dan data awal berhasil dibuat di database '${targetDb}'!`);
  } catch (err) {
    console.error('❌ Gagal mengeksekusi init.sql:', err.message);
    process.exit(1);
  } finally {
    await appClient.end();
  }

  console.log('🎉 Setup Database Selesai 100%!');
}

setup();
