const db = require('../config/db');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');
require('dotenv').config();

const ACCESS_SECRET = process.env.JWT_ACCESS_SECRET || 'weariq_pos_super_secret_jwt_access_token_key_2026';
const REFRESH_SECRET = process.env.JWT_REFRESH_SECRET || 'weariq_pos_super_secret_jwt_refresh_token_key_2026';
const ACCESS_EXPIRES_IN = process.env.JWT_ACCESS_EXPIRATION || '15m';
const REFRESH_EXPIRES_IN = process.env.JWT_REFRESH_EXPIRATION || '7d';

const generateTokens = (user) => {
  const payload = {
    id: user.id.toString(),
    name: user.nama_lengkap,
    email: user.email,
    role: user.role,
  };

  const accessToken = jwt.sign(payload, ACCESS_SECRET, { expiresIn: ACCESS_EXPIRES_IN });
  const refreshToken = jwt.sign({ id: user.id.toString() }, REFRESH_SECRET, { expiresIn: REFRESH_EXPIRES_IN });

  return { accessToken, refreshToken };
};

exports.register = async (req, res) => {
  try {
    const { nama_lengkap, username, email, password, role } = req.body;
    if (!nama_lengkap || !username || !email || !password) {
      return res.status(400).json({ message: 'Semua kolom registrasi wajib diisi.' });
    }

    const checkUser = await db.query(
      'SELECT id FROM users WHERE email = $1 OR username = $2',
      [email, username]
    );
    if (checkUser.rows.length > 0) {
      return res.status(409).json({ message: 'Email atau username sudah terdaftar.' });
    }

    const salt = await bcrypt.genSalt(10);
    const passwordHash = await bcrypt.hash(password, salt);
    const userRole = role === 'OWNER' ? 'OWNER' : 'KASIR';

    const insertResult = await db.query(
      `INSERT INTO users (nama_lengkap, username, email, password_hash, role)
       VALUES ($1, $2, $3, $4, $5)
       RETURNING id, nama_lengkap, username, email, role, created_at`,
      [nama_lengkap, username, email, passwordHash, userRole]
    );

    const newUser = insertResult.rows[0];
    const { accessToken, refreshToken } = generateTokens(newUser);

    // Simpan refresh_token ke database
    await db.query('UPDATE users SET refresh_token = $1 WHERE id = $2', [refreshToken, newUser.id]);

    return res.status(201).json({
      access_token: accessToken,
      refresh_token: refreshToken,
      user: {
        id: newUser.id.toString(),
        name: newUser.nama_lengkap,
        email: newUser.email,
        role: newUser.role,
      },
    });
  } catch (error) {
    console.error('Error in register:', error);
    return res.status(500).json({ message: 'Terjadi kesalahan pada server saat registrasi.', error: error.message });
  }
};

exports.login = async (req, res) => {
  try {
    const { email, password } = req.body;
    if (!email || !password) {
      return res.status(400).json({ message: 'Email dan password wajib diisi.' });
    }

    const userResult = await db.query(
      'SELECT * FROM users WHERE email = $1 OR username = $1',
      [email]
    );

    if (userResult.rows.length === 0) {
      return res.status(401).json({ message: 'Email atau kata sandi tidak valid.' });
    }

    const user = userResult.rows[0];

    // Cek password hash atau plain text fallback
    let isPasswordValid = false;
    try {
      isPasswordValid = await bcrypt.compare(password, user.password_hash);
    } catch (_) {
      isPasswordValid = false;
    }
    if (!isPasswordValid && password === user.password_hash) {
      isPasswordValid = true;
    }

    if (!isPasswordValid) {
      return res.status(401).json({ message: 'Email atau kata sandi tidak valid.' });
    }

    const { accessToken, refreshToken } = generateTokens(user);

    // Perbarui refresh_token di database
    await db.query('UPDATE users SET refresh_token = $1 WHERE id = $2', [refreshToken, user.id]);

    return res.json({
      access_token: accessToken,
      refresh_token: refreshToken,
      user: {
        id: user.id.toString(),
        name: user.nama_lengkap,
        email: user.email,
        role: user.role,
      },
    });
  } catch (error) {
    console.error('Error in login:', error);
    return res.status(500).json({ message: 'Terjadi kesalahan pada server saat login.', error: error.message });
  }
};

exports.refreshToken = async (req, res) => {
  try {
    const { refresh_token } = req.body;
    if (!refresh_token) {
      return res.status(400).json({ message: 'Refresh token wajib disertakan.' });
    }

    let decoded;
    try {
      decoded = jwt.verify(refresh_token, REFRESH_SECRET);
    } catch (err) {
      return res.status(401).json({ message: 'Refresh token tidak valid atau kedaluwarsa.' });
    }

    const userResult = await db.query('SELECT * FROM users WHERE id = $1', [decoded.id]);
    if (userResult.rows.length === 0) {
      return res.status(401).json({ message: 'Pengguna tidak ditemukan.' });
    }

    const user = userResult.rows[0];
    if (user.refresh_token !== refresh_token) {
      return res.status(401).json({ message: 'Sesi refresh token telah kedaluwarsa atau dicabut.' });
    }

    const { accessToken, refreshToken: newRefreshToken } = generateTokens(user);
    await db.query('UPDATE users SET refresh_token = $1 WHERE id = $2', [newRefreshToken, user.id]);

    return res.json({
      access_token: accessToken,
      refresh_token: newRefreshToken,
    });
  } catch (error) {
    console.error('Error in refreshToken:', error);
    return res.status(500).json({ message: 'Gagal memperbarui token.', error: error.message });
  }
};

exports.logout = async (req, res) => {
  try {
    if (req.user && req.user.id) {
      await db.query('UPDATE users SET refresh_token = NULL WHERE id = $1', [req.user.id]);
    }
    return res.json({ message: 'Logout berhasil.' });
  } catch (error) {
    console.error('Error in logout:', error);
    return res.status(500).json({ message: 'Gagal melakukan logout.', error: error.message });
  }
};
