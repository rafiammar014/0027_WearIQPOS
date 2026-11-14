const express = require('express');
const router = express.Router();
const aiController = require('../controllers/aiController');
const { verifyToken } = require('../middleware/authMiddleware');

router.get('/', verifyToken, aiController.getAiInsights);
router.post('/generate', verifyToken, aiController.generateInsight);
router.delete('/:id', verifyToken, aiController.deleteInsight);

module.exports = router;
