const User = require('../models/users');
const multer = require('multer');
const path = require('path');
const mongoose = require('mongoose');


const storage = multer.memoryStorage();
const upload = multer({ storage });

// Creates a new user if all required fields are provided and email is unique
exports.createUser = async (req, res) => {
    try {
        const { userName, password, verifiedPassword, mailAddress } = req.body;
        let base64Image = 'default';

        if (req.file) {
            const imageBuffer = req.file.buffer;
            const mimeType = req.file.mimetype || 'image/png';
            base64Image = `data:${mimeType};base64,${imageBuffer.toString('base64')}`;
        }

        if (!userName || !password || !verifiedPassword || !mailAddress) {
            return res.status(400).json({ error: 'Missing required fields.' });
        }

        const normalizedMail = mailAddress.toLowerCase();
        // Create user in DB
        const newUser = await User.createUser(userName, password, verifiedPassword, normalizedMail, base64Image);

        // Handle errors
        if (newUser === -1) {
            return res.status(400).json({ error: 'Email address already taken.' });
        }
        if (newUser === -2) {
            return res.status(400).json({ error: 'Email address is not valid. Address needs to end with @abamail.com' });
        }
        if (newUser === -3) {
            return res.status(400).json({ error: 'Password needs to have 8 characters or more, including at least one uppercase letter and one digit.' });
        }
        if (newUser === -4) {
            return res.status(400).json({ error: 'Verified password is different from password' });
        }

        res.set('Location', `/api/users/${newUser.userId}`);
        res.status(201).json(newUser);

    } catch (err) {
        // console.error(err);
        res.status(500).json({ error: 'Server error.', message: err.message });
    }
};


// Retrieves a user by their ID if they exist
exports.getUserById = async (req, res) => {
    try {
        // Authenticate request
        const requester = req.user;
        if (!requester) {
            return res.status(401).json({ error: 'Unauthorized' });
        }

        const requestedId = req.params.id;
        // Find user in DB
        if (!mongoose.Types.ObjectId.isValid(requestedId)) {
            return res.status(404).json({ error: "User not found." });
        }

        const user = await User.findOne({ _id: requestedId });

        if (!user) {
            return res.status(404).json({ error: "User not found." });
        }

        const { userName, mailAddress, image } = user;
        // Return user info
        res.status(200).json({ userName, mailAddress, image });

    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Server error.' });
    }
};
