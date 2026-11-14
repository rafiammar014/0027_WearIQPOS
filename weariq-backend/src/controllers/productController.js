const db = require('../config/db');

exports.getProductVariants = async (req, res) => {
  try {
    const { q, category_id, ukuran, size } = req.query;
    let queryText = 'SELECT * FROM product_variants WHERE 1=1';
    const params = [];

    if (q) {
      params.push(`%${q}%`);
      queryText += ` AND (nama_produk ILIKE $${params.length} OR kode_sku ILIKE $${params.length} OR warna ILIKE $${params.length})`;
    }

    if (category_id) {
      params.push(category_id);
      queryText += ` AND category_id = $${params.length}`;
    }

    const filterSize = ukuran || size;
    if (filterSize) {
      params.push(filterSize);
      queryText += ` AND ukuran = $${params.length}`;
    }

    queryText += ' ORDER BY id ASC';

    const result = await db.query(queryText, params);
    const mapped = result.rows.map((row) => ({
      id: row.id.toString(),
      product_id: row.category_id.toString(),
      sku: row.kode_sku,
      name: row.nama_produk,
      size: row.ukuran,
      color: row.warna,
      price: parseFloat(row.harga_jual),
      stock: parseInt(row.stok, 10),
      image_url: row.local_image_uri || null,
      cost_price: parseFloat(row.harga_modal),
      min_stock: parseInt(row.batas_stok_min, 10),
    }));

    return res.json(mapped);
  } catch (error) {
    console.error('Error in getProductVariants:', error);
    return res.status(500).json({ message: 'Gagal memuat varian produk.', error: error.message });
  }
};

exports.createProductVariant = async (req, res) => {
  try {
    const { product_id, category_id, sku, name, size, color, price, cost_price, stock, min_stock, image_url } = req.body;
    const catId = category_id || product_id;

    if (!catId || !sku || !name || !size || !color || price === undefined) {
      return res.status(400).json({ message: 'Kolom wajib (kategori, SKU, nama, ukuran, warna, harga jual) tidak boleh kosong.' });
    }

    const checkSku = await db.query('SELECT id FROM product_variants WHERE kode_sku = $1', [sku]);
    if (checkSku.rows.length > 0) {
      return res.status(409).json({ message: `Kode SKU "${sku}" sudah digunakan.` });
    }

    const insertResult = await db.query(
      `INSERT INTO product_variants
       (category_id, kode_sku, nama_produk, ukuran, warna, harga_modal, harga_jual, stok, batas_stok_min, local_image_uri)
       VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10)
       RETURNING *`,
      [
        catId,
        sku,
        name,
        size,
        color,
        cost_price || 0,
        price,
        stock || 0,
        min_stock || 5,
        image_url || null,
      ]
    );

    const row = insertResult.rows[0];
    return res.status(201).json({
      id: row.id.toString(),
      product_id: row.category_id.toString(),
      sku: row.kode_sku,
      name: row.nama_produk,
      size: row.ukuran,
      color: row.warna,
      price: parseFloat(row.harga_jual),
      stock: parseInt(row.stok, 10),
      image_url: row.local_image_uri,
    });
  } catch (error) {
    console.error('Error in createProductVariant:', error);
    return res.status(500).json({ message: 'Gagal menambah produk.', error: error.message });
  }
};

exports.updateProductVariant = async (req, res) => {
  try {
    const { id } = req.params;
    const { name, size, color, price, cost_price, stock, min_stock, image_url } = req.body;

    const result = await db.query(
      `UPDATE product_variants
       SET nama_produk = COALESCE($1, nama_produk),
           ukuran = COALESCE($2, ukuran),
           warna = COALESCE($3, warna),
           harga_jual = COALESCE($4, harga_jual),
           harga_modal = COALESCE($5, harga_modal),
           stok = COALESCE($6, stok),
           batas_stok_min = COALESCE($7, batas_stok_min),
           local_image_uri = COALESCE($8, local_image_uri),
           updated_at = CURRENT_TIMESTAMP
       WHERE id = $9
       RETURNING *`,
      [name, size, color, price, cost_price, stock, min_stock, image_url, id]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ message: 'Varian produk tidak ditemukan.' });
    }

    const row = result.rows[0];
    return res.json({
      id: row.id.toString(),
      product_id: row.category_id.toString(),
      sku: row.kode_sku,
      name: row.nama_produk,
      size: row.ukuran,
      color: row.warna,
      price: parseFloat(row.harga_jual),
      stock: parseInt(row.stok, 10),
      image_url: row.local_image_uri,
    });
  } catch (error) {
    console.error('Error in updateProductVariant:', error);
    return res.status(500).json({ message: 'Gagal memperbarui produk.', error: error.message });
  }
};

exports.deleteProductVariant = async (req, res) => {
  try {
    const { id } = req.params;
    const result = await db.query('DELETE FROM product_variants WHERE id = $1 RETURNING id', [id]);
    if (result.rows.length === 0) {
      return res.status(404).json({ message: 'Produk tidak ditemukan.' });
    }
    return res.json({ message: 'Produk berhasil dihapus.' });
  } catch (error) {
    console.error('Error in deleteProductVariant:', error);
    return res.status(500).json({ message: 'Gagal menghapus produk.', error: error.message });
  }
};
