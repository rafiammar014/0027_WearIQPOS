const express = require('express');
const router = express.Router();
const transactionController = require('../controllers/transactionController');
const { verifyToken, requireOwner } = require('../middleware/authMiddleware');

router.get('/', verifyToken, transactionController.getTransactions);
router.post('/', verifyToken, transactionController.createTransaction);
router.delete('/:id', verifyToken, requireOwner, transactionController.deleteTransaction);

module.exports = router;
