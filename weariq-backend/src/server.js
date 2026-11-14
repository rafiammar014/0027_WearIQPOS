const express = require('express');
const cors = require('cors');
require('dotenv').config();

const authRoutes = require('./routes/authRoutes');
const categoryRoutes = require('./routes/categoryRoutes');
const productRoutes = require('./routes/productRoutes');
const transactionRoutes = require('./routes/transactionRoutes');
const aiRoutes = require('./routes/aiRoutes');

const app = express();

// Middleware Global
app.use(cors());
app.use(express.json());

// Request Logger sederhana
app.use((req, res, next) => {
  console.log(`[${new Date().toISOString()}] ${req.method} ${req.originalUrl}`);
  next();
});

// Routing API
app.use('/api/auth', authRoutes);
app.use('/api/categories', categoryRoutes);
app.use('/api/products', productRoutes);
app.use('/api/transactions', transactionRoutes);
app.use('/api/ai-insights', aiRoutes);
app.use('/api/ai', aiRoutes);

// Health check endpoint
app.get('/', (req, res) => {
  res.json({
    status: 'online',
    app: 'WearIQ POS REST API Backend',
    version: '1.0.0',
    timestamp: new Date().toISOString(),
  });
});

// Global 404 handler
app.use((req, res) => {
  res.status(404).json({ message: `Endpoint ${req.method} ${req.originalUrl} tidak ditemukan.` });
});

// Global Error Handler
app.use((err, req, res, next) => {
  console.error('Unhandled server error:', err);
  res.status(500).json({ message: 'Terjadi kesalahan internal server.', error: err.message });
});

const PORT = parseInt(process.env.PORT || '8000', 10);
app.listen(PORT, '0.0.0.0', () => {
  console.log(`====================================================`);
  console.log(`🚀 WearIQ POS Server aktif pada: http://localhost:${PORT}`);
  console.log(`📱 Alamat untuk Android Emulator: http://10.0.2.2:${PORT}/api/`);
  console.log(`====================================================`);
});
