const express = require('express');
const router = express.Router();
const categoryController = require('../controllers/categoryController');
const { verifyToken, requireOwner } = require('../middleware/authMiddleware');

router.get('/', categoryController.getCategories);
router.post('/', verifyToken, requireOwner, categoryController.createCategory);
router.put('/:id', verifyToken, requireOwner, categoryController.updateCategory);
router.delete('/:id', verifyToken, requireOwner, categoryController.deleteCategory);

module.exports = router;
