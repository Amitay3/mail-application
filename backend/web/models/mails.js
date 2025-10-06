const User = require('./users');
// const Mail = require('./mails');
const urlChecker = require('../middleware/urlChecker');
const { generateId } = require('../middleware/generateId');
const Blacklist = require('./blacklists');
const { getLabelsForMail, removeLabelFromMail } = require('./labels');
const mongoose = require('mongoose');


// const allMails = [];
// let idCounter  = 0;

// Define mail schema
const mailSchema = new mongoose.Schema({
    sender: { type: String, required: true }, // sender's email address
    recipient: { type: String, required: true }, // recipient's email address
    subject: { type: String, default: "" },
    content: { type: String, default: "" },
    timestamp: { type: Date, default: Date.now },
    isDraft: { type: Boolean, default: true }
});
// Create model from schema
const Mail = mongoose.model("Mail", mailSchema);

// Creates a new mail object, validates sender and recipient, checks for malicious URLs
// Returns -1 if recipient not found, otherwise returns the created mail object
// Returns -5 if sender not found, -6 if all fields are empty
async function createMail(senderEmail, recipientEmail, subject, content, isDraft = true) {
    // normalize recipient to lowercase
    const normalizedRecipient = recipientEmail?.toLowerCase();
    // Check for completely empty mail
    if (!recipientEmail && !subject && !content) {
        return -6;
    }
    // Check if sender exists
    const sender = await User.findOne({ mailAddress: senderEmail?.toLowerCase() });
    if (!sender) {
        return -5;
    }

    // Construct mail object
    const mailData = {
        sender: sender.mailAddress,
        recipient: normalizedRecipient,
        subject: subject || "",
        content: content || "",
        isDraft
    };

    // Draft mail: only for sender
    if (isDraft) {
        const draftMail = await Mail.create(mailData);
        // Push _ids to sender drafts and all mails
        sender.drafts.push(draftMail._id);
        sender.allMails.push(draftMail._id);
        await sender.save();

        return draftMail;
    }

    // Check if recipient exists
    const recipient = await User.findOne({ mailAddress: normalizedRecipient });
    if (!recipient) {
        return -1;
    }

    // Check for URLs
    const urls = [
        ...(urlChecker.checkEmailForUrls(subject) || []),
        ...(urlChecker.checkEmailForUrls(content) || [])
    ];

    // Blacklist check loop
    let blacklisted = false;
    for (let url of urls) {
        const isBlacklisted = await urlChecker.checkUrlWithServer("GET", url);
        if (isBlacklisted) {
            // Flag as blacklisted
            blacklisted = true;
            break;
        }
    }

    // If self mail
    if (recipientEmail.toLowerCase() === senderEmail.toLowerCase()) {
        const selfMail = await Mail.create(mailData);

        if (blacklisted) {
            // Add spam + allMails
            sender.spam.push(selfMail._id);
            sender.allMails.push(selfMail._id);
        } else {
            // Normal self mail
            sender.sent.push(selfMail._id);
            sender.inbox.push(selfMail._id);
            sender.allMails.push(selfMail._id);
        }

        await sender.save();
        return selfMail;
    }



    // Create duplicate mails for sender and recipient
    const senderMail = await Mail.create(mailData);
    const recipientMail = await Mail.create({ ...mailData });


    if (blacklisted) {
        // Add to sender sent and to recipient spam (_ids)
        sender.sent.push(senderMail._id);
        sender.allMails.push(senderMail._id);
        recipient.spam.push(recipientMail._id);
        // Push to recipient's allMails only if not a self mail
        if (recipientMail._id.toString() !== senderMail._id.toString()) {
            recipient.allMails.push(recipientMail._id);
        }
        await sender.save();
        await recipient.save();
        // Return the sender's mail
        return senderMail;
    }

    // Normal send: add to sender's sent and recipient's inbox
    sender.sent.push(senderMail._id);
    sender.allMails.push(senderMail._id);
    recipient.inbox.push(recipientMail._id);
    recipient.allMails.push(recipientMail._id);
    await sender.save();
    await recipient.save();

    // Return the sender's mail
    return senderMail;
}

// Retrieves mail by ID for a specific user
async function getMailById(requestedId, userId) {
    if (!mongoose.isValidObjectId(userId)) return -1;
    const user = await User.findById(userId);
    if (!user) return -1;

    const hasMail = user.allMails.some(id => id.toString() === requestedId);
    if (!hasMail) return -3;

    const mail = await Mail.findById(requestedId).exec();
    return mail;
}


// Returns the last 50 mails sorted by timestamp descending for a given user
async function returnLast50Mails(userId) {
    const user = await User.findById(userId);
    if (!user) return -1;

    // Fetch all mails using their ObjectIds
    const mails = await Mail.find({ _id: { $in: user.allMails } })
        .sort({ timestamp: -1 })
        .limit(50)
        .exec();


    return mails;
}

// Deletes a mail by mailId for the given user from all folders
// Returns -1 if user or mail not found, otherwise true
async function deleteMail(mailId) {
    if (!mongoose.isValidObjectId(mailId)) return -1;
    const deletedMail = await Mail.findByIdAndDelete(mailId);
    if (!deletedMail) return -1;

    // Remove the mail _id from all users' mail arrays
    await User.updateMany(
        {},
        {
            $pull: {
                inbox: mailId,
                sent: mailId,
                drafts: mailId,
                spam: mailId,
                allMails: mailId,
                mailsLabels: { mailId: mailId }
            }
        }
    );

    return true;
}

// Updates a draft mail for a user with new details
// Returns -1 if recipient not found, -3 if mail already sent, 
// -4 if mail not found in drafts, -5 if user not found, otherwise returns updated mail
async function updateMail(mailId, updates, userId) {
    // Find sender user
    const sender = await User.findById(userId);
    if (!sender) return -5;

    if (!mongoose.Types.ObjectId.isValid(mailId)) return -4;
    const objectId = new mongoose.Types.ObjectId(mailId);

    const mail = await Mail.findOne({
        $and: [{ _id: objectId }, { _id: { $in: sender.drafts } }]
    });
    if (!mail) return -4;

    // Mail already sent
    if (!mail.isDraft) return -3;

    // Update fields if provided
    if (updates.recipient !== undefined) {
        mail.recipient = updates.recipient.toLowerCase();
    }
    if (updates.subject !== undefined) {
        mail.subject = updates.subject;
    }
    if (updates.content !== undefined) {
        mail.content = updates.content;
    }

    // Always refresh timestamp
    mail.timestamp = new Date();

    // If sending (not a draft anymore)
    if (updates.isDraft === false) {
        // Verify recipient exists
        const recipient = await User.findOne({ mailAddress: mail.recipient });
        if (!recipient) return -1;

        // Check for URLs (subject + content)
        const urls = [
            ...(urlChecker.checkEmailForUrls(mail.subject) || []),
            ...(urlChecker.checkEmailForUrls(mail.content) || [])
        ];
        let blacklisted = false;
        for (let url of urls) {
            if (await urlChecker.checkUrlWithServer("GET", url)) {
                blacklisted = true;
                break;
            }
        }

        // If self mail
        if (mail.recipient.toLowerCase() === sender.mailAddress.toLowerCase()) {
            if (blacklisted) {
                // Put in spam
                sender.spam.push(mail._id);
            } else {
                // Normal self send
                sender.sent.push(mail._id);
                sender.inbox.push(mail._id);
            }
            // Remove from drafts
            sender.drafts.pull(mailId);
            // Mark original mail as sent
            mail.isDraft = false;
            await mail.save();

            await sender.save();
            return mail;
        }


        // Mark original mail as sent
        mail.isDraft = false;
        await mail.save();

        // Create a duplicate for recipient
        const recipientMail = await Mail.create({
            sender: mail.sender,
            recipient: mail.recipient,
            subject: mail.subject,
            content: mail.content,
            timestamp: mail.timestamp,
            isDraft: false
        });

        // Remove from drafts
        sender.drafts.pull(mailId);

        if (blacklisted) {
            // Put in spam
            sender.sent.push(mail._id);
            sender.allMails.push(mail._id);
            recipient.spam.push(recipientMail._id);
            recipient.allMails.push(recipientMail._id);
        } else {
            // Normal send without sender allMails (already there)
            sender.sent.push(mail._id);
            recipient.inbox.push(recipientMail._id);
            recipient.allMails.push(recipientMail._id);
        }

        await sender.save();
        await recipient.save();
        return mail;
    }

    // Still a draft
    await mail.save();
    return mail;
}


// Searches mails in user's allMails matching query in sender, recipient, subject, or content
// Case-insensitive
async function searchQuery(query, userId) {
    const mails = [];
    const seen = new Set();
    const lowerQuery = query.toLowerCase();

    // Fetch the user with allMails and spam populated
    const user = await User.findById(userId)
        .populate('allMails')
        .populate('spam')
        .exec();

    if (!user) return -1;

    // Create a Set of spam mail IDs for fast lookup
    const spamIds = new Set(user.spam.map(mail => mail.id));

    for (let mail of user.allMails) {
        const id = mail._id.toString();

        // Skip if this mail is in the spam folder
        if (spamIds.has(id)) continue;

        if (!seen.has(id) &&
            (
                mail.sender.toLowerCase().includes(lowerQuery) ||
                mail.recipient.toLowerCase().includes(lowerQuery) ||
                mail.subject.toLowerCase().includes(lowerQuery) ||
                mail.content.toLowerCase().includes(lowerQuery)
            )) {
            mails.push(mail);
            seen.add(id);
        }
    }

    return mails.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));
}


// Function that returns last 50 mails of a given user filtered by a specific folder
async function returnLast50MailsByFolder(userId, folder) {
    const user = await User.findById(userId);
    if (!user) return -1;

    if (!['inbox', 'sent', 'spam', 'drafts'].includes(folder)) {
        return -2;
    }

    const mails = await Mail.find({ _id: { $in: user[folder] } })
        .sort({ timestamp: -1 })
        .limit(50)
        .exec();

    return mails;
}

// Adds a mail to spam for a user, removes it from sent/inbox, and adds URLs to blacklist
// Returns -1 if user or mail not found, -3 if mail is a draft, otherwise returns true
async function addMailToSpam(userId, mailId) {
    const user = await User.findById(userId);
    if (!user) return -1;

    const mail = await Mail.findById(mailId);
    if (!mail) return -1;
    if (user.drafts.some(id => id.equals(mail._id))) return -3;

    await User.updateOne(
        { _id: userId },
        {
            $pull: { inbox: mailId, sent: mailId },
            $push: { spam: mailId }
        }
    );

    // Remove all labels from the mail
    const mailLabels = await getLabelsForMail(mailId, userId);
    if (Array.isArray(mailLabels) && mailLabels.length > 0) {
        for (const label of mailLabels) {
            await removeLabelFromMail(label._id, mailId, userId);
        }
    }


    // Extract URLs
    const subjectUrls = urlChecker.checkEmailForUrls(mail.subject) || [];
    const contentUrls = urlChecker.checkEmailForUrls(mail.content) || [];
    const urls = [...new Set([...subjectUrls, ...contentUrls].map(url => url.toLowerCase()))];

    // Add URLs to blacklist
    for (let url of urls) {
        await Blacklist.addUrlToBlacklist(url);
    }

    return true;
}

async function removeMailFromSpam(userId, mailId) {
    const user = await User.findById(userId);
    if (!user) return -1;

    // validate mailId
    if (!mongoose.Types.ObjectId.isValid(mailId)) {
        console.warn('removeMailFromSpam: invalid mailId format:', mailId);
        return -1;
    }
    const objectId = new mongoose.Types.ObjectId(mailId);


    // Find mail in user's spam
    const mail = await Mail.findOne({
        $and: [
            { _id: objectId },
            { _id: { $in: user.spam } }
        ]
    });

    if (!mail) return -1;
    if (user.drafts.some(id => id.equals(mail._id))) return -3;

    // Remove mail from spam
    await User.updateOne({ _id: userId }, { $pull: { spam: objectId } });

    if (mail.sender === user.mailAddress) {
        await User.updateOne({ _id: userId }, { $push: { sent: objectId } });
    }
    if (mail.recipient === user.mailAddress) {
        await User.updateOne({ _id: userId }, { $push: { inbox: objectId } });
    }

    // Extract URLs
    const subjectUrls = urlChecker.checkEmailForUrls(mail.subject) || [];
    const contentUrls = urlChecker.checkEmailForUrls(mail.content) || [];
    const urls = [...new Set([...subjectUrls, ...contentUrls].map(url => url.toLowerCase()))];

    console.log('URLs to remove from blacklist (from loaded mail):', urls);

    for (let url of urls) {
        try {
            const res = await Blacklist.deleteUrlFromBlacklistByName(url);
            console.log(`Tried delete for "${url}" -> result:`, res);
        } catch (err) {
            console.error(`Error deleting url ${url}`, err);
        }
    }
    return true;
}

// exports the Mail model
module.exports = Mail;
module.exports = {
    createMail,
    getMailById,
    returnLast50Mails,
    deleteMail,
    updateMail,
    searchQuery,
    returnLast50MailsByFolder,
    addMailToSpam,
    removeMailFromSpam
};
