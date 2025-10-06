const Labels = require('../models/labels');
const Mail = require('../models/mails');
const User = require('../models/users');

// Retrieves all labels for the authenticated user
exports.getAllLabels = async (req, res) => {
    // Authenticate user
    const user = req.user;
    if (!user) return;
    try {
        // Get all labels for the user
        const labels = await Labels.getAllLabels(user._id);
        if (labels === -5) {
            return res.status(404).json({ error: "User not found." });
        }

        res.status(200).json(labels);
    } catch (err) {
        console.error("Error in getAllLabels:", err);
        res.status(500).json({ error: "Internal server error" });
    }
};

// Retrieves a specific label by its ID for the authenticated user
exports.getLabelById = async (req, res) => {
    // Authenticate user
    const user = req.user;
    if (!user) return;
    try {
        const labelId = req.params.id;
        // Get the label by ID
        const label = await Labels.getLabelById(labelId, user._id);

        if (label === -1) {
            return res.status(404).json({ error: "Label not found." });
        }
        if (label === -5) {
            return res.status(404).json({ error: "User not found." });
        }

        res.status(200).json(label);
    } catch (err) {
        console.error("Error in getLabelById:", err);
        res.status(500).json({ error: "Internal server error" });
    }
}

// Creates a new label for the authenticated user
exports.createLabel = async (req, res) => {
    // Authenticate user
    const user = req.user;
    if (!user) return;

    const { labelName } = req.body;
    if (!labelName) {
        return res.status(400).json({ error: "Must enter label name" });
    }
    try {
        const result = await Labels.createLabel(labelName, user._id);
        if (result === -1) {
            return res.status(404).json({ error: "User not found." });
        }
        if (result === -2) {
            return res.status(400).json({ error: "Label name already exists." });
        }

        res.set('Location', `/api/labels/${result._id}`);
        res.status(201).json({ label: result });
    } catch (err) {
        console.error('Error creating label:', err);
        res.status(500).json({ error: 'Internal server error' });
    }
};

// Updates the name of an existing label for the authenticated user
exports.updateLabel = async (req, res) => {
    // Authenticate user
    const user = req.user;
    if (!user) return;

    const { labelName } = req.body;
    if (!labelName) {
        return res.status(400).json({ error: "Must enter label name" });
    }
    try {
        const labelId = req.params.id;
        // Update the label
        const label = await Labels.updateLabel(labelId, labelName, user._id);

        if (label === -1) {
            return res.status(404).json({ error: "Label not found." });
        }
        if (label === -2) {
            return res.status(400).json({ error: "Label name already exists." });
        }

        res.status(200).json({ label });
    } catch (err) {
        console.error("Error in updateLabel:", err);
        res.status(500).json({ error: "Internal server error" });
    }
};

// Deletes a label by ID for the authenticated user
exports.deleteLabel = async (req, res) => {
    // Authenticate user
    const user = req.user;
    if (!user) return;
    try {
        const labelId = req.params.id;
        // Delete the label
        const result = await Labels.deleteLabel(labelId, user._id);

        if (result === -1) {
            return res.status(404).json({ error: "Label not found." });
        }
        if (result === -5) {
            return res.status(404).json({ error: "User not found." });
        }

        res.status(204).send();
    } catch (err) {
        console.error("Error in deleteLabel:", err);
        res.status(500).json({ error: "Internal server error" });
    }
};

// Adds a label to a user's mail
exports.addLabelToMail = async (req, res) => {
    // Authenticate user
    const user = req.user;
    if (!user) return;
    // Extract labelId and mailId from request body
    const { labelId, mailId } = req.body;
    if (!labelId) {
        return res.status(400).json({ error: "Must enter label ID" });
    }
    if (!mailId) {
        return res.status(400).json({ error: "Must enter mail ID" });
    }
    try {
        const result = await Labels.addLabelToMail(labelId, mailId, user._id);

        if (result === -5) {
            return res.status(401).json({ error: "User not found." });
        }
        if (result === -1) {
            return res.status(404).json({ error: "Label not found." });
        }
        if (result === -2) {
            return res.status(404).json({ error: "Mail not found." });
        }
        if (result === -3) {
            return res.status(400).json({ error: "The label already added to this mail." });
        }

        res.status(200).json({ mailId, addedLabelId: labelId });
    } catch (err) {
        console.error("Error in addLabelToMail:", err);
        res.status(500).json({ error: "Internal server error" });
    }
};


exports.removeLabelFromMail = async (req, res) => {
    // Authenticate user
    const user = req.user;
    if (!user) return;
    // Extract labelId and mailId from request parameters
    const mailId = req.params.mailId;
    const labelId = req.params.labelId;
    try {
        // Remove the label from the mailLabels
        const result = await Labels.removeLabelFromMail(labelId, mailId, user._id);

        if (result === -5) {
            return res.status(401).json({ error: "User not found." });
        }
        if (result === -1) {
            return res.status(404).json({ error: "Label not found." });
        }
        if (result === -2) {
            return res.status(404).json({ error: "Mail not found." });
        }
        if (result === -3) {
            return res.status(404).json({ error: "Mail not labeled." });
        }
        if (result === -4) {
            return res.status(404).json({ error: "Label not found on this mail." });
        }

        res.status(200).json({ message: "Label removed from mail." });
    } catch (err) {
        console.error("Error in removeLabelFromMail:", err);
        res.status(500).json({ error: "Internal server error" });
    }
};

// Retrieves all user's labels (objects) associated with a specific mail 
// If the mail has no labels, returns an empty array
exports.getLabelsForMail = async (req, res) => {
    const user = req.user;
    if (!user) return;
    // Extract mailId from request parameters
    const mailId = req.params.mailId;
    try {
        // Get all labels for the mail
        const result = await Labels.getLabelsForMail(mailId, user._id);

        if (result === -5) {
            return res.status(401).json({ error: "User not found." });
        }

        if (result === -1) {
            return res.status(404).json({ error: "Mail not found." });
        }

        res.status(200).json(result);
    } catch (err) {
        console.error("Error in getLabelsForMail:", err);
        res.status(500).json({ error: "Internal server error" });
    }
};

// Retrieves all user's mails (objects) associated with a specific label
exports.getMailsForLabel = async (req, res) => {
    const user = req.user;
    if (!user) return;

    const labelId = req.params.labelId;
    try {
        // Get all mails for the label
        const result = await Labels.getMailsForLabel(labelId, user._id);

        if (result === -5) {
            return res.status(401).json({ error: "User not found." });
        }

        if (result === -1) {
            return res.status(404).json({ error: "Label not found." });
        }

        res.status(200).json(result);
    } catch (err) {
        console.error("Error in getMailsForLabel:", err);
        res.status(500).json({ error: "Internal server error" });
    }
};