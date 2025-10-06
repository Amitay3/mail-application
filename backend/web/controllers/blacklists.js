const Blacklist = require('../models/blacklists');
// const Authenticator = require('../middleware/auth');

// Handles adding a new URL to the blacklist
exports.addUrl = async (req, res) => {
    // Authenticate user
    const user = req.user;
    if (!user) return;
    
    const { url } = req.body;

    if (!url) {
        return res.status(404).json({ error: "No url provided." });
    }

    try {
        // Add the URL to the blacklist model
        const newUrl = await Blacklist.addUrlToBlacklist(url);

        if (!newUrl) {
            return res.status(400).json({ error: "Bad request" });
        }

        // Set Location header to the new resource
        res.set('Location', `/api/blacklist/${newUrl.id}`);
        res.status(201).send();
    } catch (err) {
        res.status(500).json({ message: "Internal server error", details: err.message });
    }
};

// Handles deleting a URL from the blacklist by ID
exports.deleteUrl = async (req, res) => {
    // Authenticate user
    const user = req.user;
    if (!user) return;

    const id = req.params.id;

    try {
        // Attempt to delete by ID
        const result = await Blacklist.deleteUrlFromBlacklist(id);

        if (result === -1) {
            return res.status(404).json({ error: "URL was not found." });
        }

        return res.status(204).send();
    } catch (err) {
        res.status(500).json({ error: "Internal server error" });
    }
};

// Handles deleting a URL from the blacklist by name
exports.deleteUrlByName = async (req, res) => {
    // Authenticate user
    const user = req.user;
    if (!user) return;

    const url = req.params.name;

    try {
        // Attempt to delete by ID
        const result = await Blacklist.deleteUrlFromBlacklistByName(url);

        if (result === -1) {
            return res.status(404).json({ error: "URL was not found." });
        }

        return res.status(204).send();
    } catch (err) {
        res.status(500).json({ error: "Internal server error" });
    }
};

