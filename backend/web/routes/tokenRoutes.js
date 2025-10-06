const express = require('express');
const router = express.Router();
const controller = require('../controllers/tokens');

router.route('/')
    // No token required
    .post(controller.login);

module.exports = router;