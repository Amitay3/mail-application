const Mail = require('../models/mails');
const User = require('../models/users');

// Creates a new mail (either a draft or sent mail depending on isDraft flag)
exports.createMail = async (req, res) => {
    // Authenticate user
    const user = req.user;
    if (!user) return;

    const { recipient, subject, content, isDraft } = req.body;
    // Default to draft if isDraft is not provided
    const draftFlag = isDraft !== undefined ? isDraft : true;

    // Ensure all required fields are present
    if ((!recipient || !subject || !content) && !isDraft) {
        return res.status(400).json({ error: 'Missing required fields.' });
    }

    try {
        // Create the mail object and validate for malicious URLs if not a draft
        const newMail = await Mail.createMail(user.mailAddress, recipient, subject, content, draftFlag);
        if (newMail === -1) {
            return res.status(404).json({ error: "Recipient not found." });
        }
        if (newMail === -5) {
            return res.status(404).json({ error: "Sender not found." });
        }
        if (newMail === -6) {
            return res.status(400).json({ error: "Draft not created" });
        }        
        // Set Location header with new mail ID
        // res.set('Location', `/api/mails/${newMail.id}`);
        res.status(201).json(newMail);
    } catch (err) {
        console.error("Mail creation failed:", err);
        res.status(500).json({ error: 'Internal server error', details: err.message });
    }
};

// Retrieves a single mail by its ID if the user owns it
exports.getMailById = async (req, res) => {
    try {
        // Authenticate user
        const user = req.user;
        if (!user) {
            return res.status(401).json({ error: "Unauthorized" });
        }

        const requestedId = req.params.id;

        const mail = await Mail.getMailById(requestedId, user._id);

        if (mail === -1 || !mail) {
            return res.status(404).json({ error: "Mail not found." });
        }

        res.status(200).json(mail);
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: "Server error." });
    }
};


// Returns the last 50 mails the user has of any kind
exports.returnLast50Mails = async (req, res) => {
    // Authenticate user
    const user = req.user;
    if (!user) return;

    const last50Mails = await Mail.returnLast50Mails(user._id);
    res.status(200).json(last50Mails);
};

// Updates a draft mail's fields (subject/content/recipient). Cannot update sent mail.
exports.updateMail = async (req, res) => {
    // Authenticate user
    const user = req.user;
    if (!user) return;

    const mailId = req.params.id;
    const updates = req.body;

    // Reject empty update request
    if (!updates || Object.keys(updates).length === 0) {
        return res.status(400).json({ error: "At least one change is required." });
    }

    try {
        // Attempt to apply updates (only works for drafts owned by user)
        const result = await Mail.updateMail(mailId, updates, user._id);
        if (result === -3) {
            return res.status(400).json({ error: "Mail already sent." });
        }
        if (result === -1) {
            return res.status(404).json({ error: "Recipient not found." });
        }
        if (result === -4) {
            return res.status(404).json({ error: "Mail not found in drafts." });
        }
        if (result === -5) {
            return res.status(404).json({ error: "Sender not found." });
        }

        res.status(200).json({ mailId: result.id, isDraft: result.isDraft });
    } catch (err) {
    console.error("Update mail failed:", err);
    res.status(500).json({ error: "Internal server error" });
}

};

// Deletes a mail if it belongs to the user
exports.deleteMail = async (req, res) => {
    // Authenticate user
    const user = req.user;
    if (!user) return;

    const mailId = req.params.id;

    const result = await Mail.deleteMail(mailId, user._id);

    if (result === -1) {
        return res.status(404).json({ error: "Mail not found." });
    }

    // Deletion successful
    res.status(204).send();
};

// Searches user’s mails by query (subject or content)
exports.searchQuery = async (req, res) => {
    // Authenticate user
    const user = req.user;
    if (!user) return;

    // Extract query from request parameters
    const query = req.params.query;
    if (!query) {
        return res.status(400).json({ error: "Query string is missing." });
    }

    const mails = await Mail.searchQuery(query, user._id);
    if (mails === -1) {
        return res.status(404).json({error: "user not found"});
    }
    res.status(200).json(mails);
};


exports.returnLast50Inbox = async (req,res) => {
    const user = req.user;
    if (!user) return;

    const last50Mails = await Mail.returnLast50MailsByFolder(user._id, 'inbox');
    res.status(200).json(last50Mails);
}

exports.returnLast50Sent = async (req,res) => {
    const user = req.user;
    if (!user) return;

    const last50Mails = await Mail.returnLast50MailsByFolder(user._id, 'sent');
    res.status(200).json(last50Mails);
}

exports.returnLast50Spam = async (req,res) => {
    const user = req.user;
    if (!user) return;

    const last50Mails = await Mail.returnLast50MailsByFolder(user._id, 'spam');
    res.status(200).json(last50Mails);
}

exports.returnLast50Drafts = async (req,res) => {
    const user = req.user;
    if (!user) return;

    const last50Mails = await Mail.returnLast50MailsByFolder(user._id, 'drafts');
    res.status(200).json(last50Mails);
}

exports.addMailToSpam = async (req, res) => {
    const user = req.user;
    if (!user) return res.status(401).json({ error: "Unauthorized" });

    const mailId = req.params.id;

    try {
        const result = await Mail.addMailToSpam(user._id, mailId);

        if (result === -1) {
            return res.status(404).json({ error: "Mail not found or invalid." });
        }
        if (result === -3) {
            return res.status(400).json({ error: "Mail is a draft and cannot be moved to spam." });
        } 

        res.status(201).send();
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: "Internal server error." });
    }
};

exports.removeMailFromSpam = async (req, res) => {
    const user = req.user;
    if (!user) return -1;

    const mailId = req.params.id;

    try {
        const result = await Mail.removeMailFromSpam(user._id, mailId);

        if (result === -1) {
            return res.status(404).json({ error: "Mail not found or invalid." });
        }

        res.status(204).send();
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: "Internal server error." });
    }
}


