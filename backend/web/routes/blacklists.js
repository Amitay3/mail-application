const express = require('express');
const router = express.Router();
const controller = require('../controllers/blacklists');
const auth = require('../middleware/auth');

router.route('/')
    .post(auth.verifyToken,controller.addUrl);

router.route('/:id')
    .delete(auth.verifyToken,controller.deleteUrl);

router.route('/delete-by-name/:name')
    .delete(auth.verifyToken, controller.deleteUrlByName)

module.exports = router;