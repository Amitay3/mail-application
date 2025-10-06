const jwt = require('jsonwebtoken');
const User = require('../models/users'); 
const key = process.env.JWT_SECRET || "secret"; 

async function verifyToken(req, res, next) {
    const header = req.headers['authorization'];
    if (!header) {
        return res.status(403).json({ error: 'Token required' });
    }

    const token = header.split(' ')[1];
    try {
        // Verify JWT
        const data = jwt.verify(token, key);

        // Fetch the full user document from MongoDB
        const user = await User.findById(data.userId);
        if (!user) {
            return res.status(404).json({ error: 'User not found' });
        }

        // Attach the user document to the request
        req.user = user;
        next();
    } catch (err) {
        res.status(401).json({ error: 'Invalid token' });
    }
}

module.exports = { verifyToken };