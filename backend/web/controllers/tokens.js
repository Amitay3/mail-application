const User = require('../models/users');
const jwt = require('jsonwebtoken');
const key = process.env.JWT_SECRET;

// Handles user login by verifying mail address and password
exports.login = async (req, res) => {
    const { mailAddress, password } = req.body;

    // Check for missing credentials
    if (!mailAddress || !password) {
        return res.status(400).json({ error: "missing required fields." });
    }

    const normalizeMail = mailAddress.toLowerCase();
    try {
        // Authenticate user
        const user = await User.authenticateUser(normalizeMail, password);
        if (!user) {
            return res.status(401).json({ error: "Invalid credentials" });
        }
        // Generate JWT
        const token = jwt.sign({ userId: user._id, mailAddress: user.mailAddress }, key);

        // remove password before sending back
        const { password: _, ...UserWithoutPassword } = user.toObject();
        res.status(201).json({ token, user: UserWithoutPassword });

    } catch (err) {
        res.status(500).json({ error: "Server error", details: err.message });
    }
};
