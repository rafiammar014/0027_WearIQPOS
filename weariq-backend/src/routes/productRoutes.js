const express = require('express');
const router = express.Router();
const productController = require('../controllers/productController');
const { verifyToken, requireOwner } = require('../middleware/authMiddleware');

// Mendukung rute /products dan /products/variants
router.get('/', productController.getProductVariants);
router.get('/variants', productController.getProductVariants);
router.post('/', verifyToken, requireOwner, productController.createProductVariant);
router.post('/variants', verifyToken, requireOwner, productController.createProductVariant);
router.put('/:id', verifyToken, requireOwner, productController.updateProductVariant);
router.put('/variants/:id', verifyToken, requireOwner, productController.updateProductVariant);
router.delete('/:id', verifyToken, requireOwner, productController.deleteProductVariant);
router.delete('/variants/:id', verifyToken, requireOwner, productController.deleteProductVariant);

module.exports = router;
