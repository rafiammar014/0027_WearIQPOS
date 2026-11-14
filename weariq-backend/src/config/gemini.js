const { GoogleGenAI } = require('@google/genai');
require('dotenv').config();

const apiKey = process.env.GEMINI_API_KEY;
if (!apiKey) {
  console.warn('⚠️ GEMINI_API_KEY belum dikonfigurasi di file .env');
}

const ai = new GoogleGenAI({ apiKey: apiKey || '' });

module.exports = ai;
