const db = require('../config/db');

exports.getCategories = async (req, res) => {
  try {
    const searchQuery = req.query.q ? `%${req.query.q}%` : null;
    let queryText = 'SELECT * FROM categories';
    const params = [];

    if (searchQuery) {
      queryText += ' WHERE nama_kategori ILIKE $1';
      params.push(searchQuery);
    }
    queryText += ' ORDER BY id ASC';

    const result = await db.query(queryText, params);
    const mapped = result.rows.map((row) => ({
      id: row.id.toString(),
      name: row.nama_kategori,
      description: row.deskripsi || '',
    }));

    return res.json(mapped);
  } catch (error) {
    console.error('Error in getCategories:', error);
    return res.status(500).json({ message: 'Gagal memuat kategori.', error: error.message });
  }
};

exports.createCategory = async (req, res) => {
  try {
    const { name, description } = req.body;
    if (!name) {
      return res.status(400).json({ message: 'Nama kategori wajib diisi.' });
    }

    const result = await db.query(
      'INSERT INTO categories (nama_kategori, deskripsi) VALUES ($1, $2) RETURNING *',
      [name, description || null]
    );

    const row = result.rows[0];
    return res.status(201).json({
      id: row.id.toString(),
      name: row.nama_kategori,
      description: row.deskripsi || '',
    });
  } catch (error) {
    console.error('Error in createCategory:', error);
    return res.status(500).json({ message: 'Gagal membuat kategori.', error: error.message });
  }
};

exports.updateCategory = async (req, res) => {
  try {
    const { id } = req.params;
    const { name, description } = req.body;

    const result = await db.query(
      'UPDATE categories SET nama_kategori = $1, deskripsi = $2 WHERE id = $3 RETURNING *',
      [name, description, id]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ message: 'Kategori tidak ditemukan.' });
    }

    const row = result.rows[0];
    return res.json({
      id: row.id.toString(),
      name: row.nama_kategori,
      description: row.deskripsi || '',
    });
  } catch (error) {
    console.error('Error in updateCategory:', error);
    return res.status(500).json({ message: 'Gagal memperbarui kategori.', error: error.message });
  }
};

exports.deleteCategory = async (req, res) => {
  try {
    const { id } = req.params;

    // Cek apakah ada produk terikat
    const checkProducts = await db.query('SELECT id FROM product_variants WHERE category_id = $1 LIMIT 1', [id]);
    if (checkProducts.rows.length > 0) {
      return res.status(400).json({ message: 'Tidak dapat menghapus kategori yang masih memiliki produk.' });
    }

    const result = await db.query('DELETE FROM categories WHERE id = $1 RETURNING id', [id]);
    if (result.rows.length === 0) {
      return res.status(404).json({ message: 'Kategori tidak ditemukan.' });
    }

    return res.json({ message: 'Kategori berhasil dihapus.' });
  } catch (error) {
    console.error('Error in deleteCategory:', error);
    return res.status(500).json({ message: 'Gagal menghapus kategori.', error: error.message });
  }
};
