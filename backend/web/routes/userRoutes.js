const express = require('express');
const multer = require('multer');
const router = express.Router();
const controller = require('../controllers/users');
const auth = require('../middleware/auth');

const storage = multer.memoryStorage();
const upload = multer({ storage });

router.route('/')
    // No token required
    .post(upload.single('image'), controller.createUser);

router.route('/:id')
    .get(auth.verifyToken,controller.getUserById)

    
module.exports = router;