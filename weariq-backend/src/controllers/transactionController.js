const db = require('../config/db');

exports.getTransactions = async (req, res) => {
  try {
    const { q } = req.query;
    let queryText = `
      SELECT t.*, u.nama_lengkap as cashier_name
      FROM transactions t
      JOIN users u ON t.user_id = u.id
      WHERE 1=1
    `;
    const params = [];

    if (q) {
      params.push(`%${q}%`);
      queryText += ` AND (t.kode_invoice ILIKE $${params.length} OR t.nama_pelanggan ILIKE $${params.length})`;
    }

    queryText += ' ORDER BY t.created_at DESC';

    const txResult = await db.query(queryText, params);
    const transactions = [];

    for (const row of txResult.rows) {
      const itemsResult = await db.query(
        `SELECT ti.*, pv.nama_produk
         FROM transaction_items ti
         JOIN product_variants pv ON ti.variant_id = pv.id
         WHERE ti.transaction_id = $1`,
        [row.id]
      );

      const items = itemsResult.rows.map((item) => ({
        variant_id: item.variant_id.toString(),
        product_name: item.nama_produk,
        quantity: item.qty,
        unit_price: parseFloat(item.harga_satuan),
        subtotal: parseFloat(item.subtotal),
      }));

      transactions.push({
        id: row.id.toString(),
        invoice_number: row.kode_invoice,
        cashier_id: row.user_id.toString(),
        cashier_name: row.cashier_name,
        customer_name: row.nama_pelanggan,
        total_amount: parseFloat(row.total_harga),
        payment_method: row.metode_pembayaran,
        amount_paid: parseFloat(row.uang_bayar || 0),
        change: parseFloat(row.kembalian || 0),
        note: row.catatan || '',
        created_at: row.created_at,
        items,
      });
    }

    return res.json(transactions);
  } catch (error) {
    console.error('Error in getTransactions:', error);
    return res.status(500).json({ message: 'Gagal memuat transaksi.', error: error.message });
  }
};

exports.createTransaction = async (req, res) => {
  const client = await db.pool.connect();
  try {
    const {
      invoice_number,
      cashier_id,
      customer_name,
      total_amount,
      payment_method,
      amount_paid,
      change,
      note,
      items,
    } = req.body;

    const userId = cashier_id || (req.user ? req.user.id : 1);
    const invNumber = invoice_number || `INV-${Date.now()}`;
    const payMethod = payment_method || 'TUNAI';

    if (!items || items.length === 0) {
      return res.status(400).json({ message: 'Keranjang belanja tidak boleh kosong.' });
    }

    // Mulai transaksi atomik PostgreSQL
    await client.query('BEGIN');

    // 1. Validasi stok untuk setiap item
    let calculatedTotal = 0;
    let totalItemsCount = 0;

    for (const item of items) {
      const variantRes = await client.query(
        'SELECT stok, nama_produk FROM product_variants WHERE id = $1 FOR UPDATE',
        [item.variant_id]
      );

      if (variantRes.rows.length === 0) {
        await client.query('ROLLBACK');
        return res.status(404).json({ message: `Produk ID ${item.variant_id} tidak ditemukan.` });
      }

      const variant = variantRes.rows[0];
      if (variant.stok < item.quantity) {
        await client.query('ROLLBACK');
        return res.status(400).json({
          message: `Stok "${variant.nama_produk}" tidak mencukupi (sisa: ${variant.stok}, dipesan: ${item.quantity}).`,
        });
      }

      calculatedTotal += item.unit_price * item.quantity;
      totalItemsCount += item.quantity;
    }

    const finalTotal = total_amount !== undefined ? total_amount : calculatedTotal;
    const finalPaid = amount_paid !== undefined ? amount_paid : finalTotal;
    const finalChange = change !== undefined ? change : (finalPaid - finalTotal);

    // 2. Simpan ke tabel transactions
    const txInsert = await client.query(
      `INSERT INTO transactions
       (kode_invoice, user_id, nama_pelanggan, total_item, total_harga, metode_pembayaran, uang_bayar, kembalian, catatan)
       VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)
       RETURNING *`,
      [
        invNumber,
        userId,
        customer_name || 'Pelanggan Umum',
        totalItemsCount,
        finalTotal,
        payMethod,
        finalPaid,
        finalChange,
        note || null,
      ]
    );

    const newTx = txInsert.rows[0];

    // 3. Simpan item transaksi dan potong stok atomik
    const savedItems = [];
    for (const item of items) {
      const itemSubtotal = item.subtotal || (item.unit_price * item.quantity);

      await client.query(
        `INSERT INTO transaction_items (transaction_id, variant_id, qty, harga_satuan, subtotal)
         VALUES ($1, $2, $3, $4, $5)`,
        [newTx.id, item.variant_id, item.quantity, item.unit_price, itemSubtotal]
      );

      await client.query(
        'UPDATE product_variants SET stok = stok - $1, updated_at = CURRENT_TIMESTAMP WHERE id = $2',
        [item.quantity, item.variant_id]
      );

      savedItems.push({
        variant_id: item.variant_id.toString(),
        product_name: item.product_name,
        quantity: item.quantity,
        unit_price: item.unit_price,
        subtotal: itemSubtotal,
      });
    }

    await client.query('COMMIT');

    return res.status(201).json({
      id: newTx.id.toString(),
      invoice_number: newTx.kode_invoice,
      cashier_id: newTx.user_id.toString(),
      total_amount: parseFloat(newTx.total_harga),
      payment_method: newTx.metode_pembayaran,
      created_at: newTx.created_at,
      items: savedItems,
    });
  } catch (error) {
    await client.query('ROLLBACK');
    console.error('Error in createTransaction:', error);
    return res.status(500).json({ message: 'Gagal memproses transaksi kasir.', error: error.message });
  } finally {
    client.release();
  }
};

exports.deleteTransaction = async (req, res) => {
  const client = await db.pool.connect();
  try {
    const { id } = req.params;

    await client.query('BEGIN');

    // Ambil item transaksi untuk mengembalikan stok
    const itemsRes = await client.query(
      'SELECT variant_id, qty FROM transaction_items WHERE transaction_id = $1',
      [id]
    );

    if (itemsRes.rows.length === 0) {
      await client.query('ROLLBACK');
      return res.status(404).json({ message: 'Transaksi tidak ditemukan.' });
    }

    // Kembalikan stok produk
    for (const item of itemsRes.rows) {
      await client.query(
        'UPDATE product_variants SET stok = stok + $1, updated_at = CURRENT_TIMESTAMP WHERE id = $2',
        [item.qty, item.variant_id]
      );
    }

    // Hapus transaksi (item akan terhapus otomatis via ON DELETE CASCADE)
    await client.query('DELETE FROM transactions WHERE id = $1', [id]);

    await client.query('COMMIT');
    return res.json({ message: 'Transaksi berhasil dibatalkan dan stok dikembalikan.' });
  } catch (error) {
    await client.query('ROLLBACK');
    console.error('Error in deleteTransaction:', error);
    return res.status(500).json({ message: 'Gagal membatalkan transaksi.', error: error.message });
  } finally {
    client.release();
  }
};
