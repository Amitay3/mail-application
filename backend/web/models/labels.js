const mongoose = require('mongoose');
const User = require('./users');

// Creates a new label for the given user
// Returns the created label object, -1 if user not found, -2 if label name already exists
async function createLabel(labelName, userId) {
    // Find the user by userId
    const user = await User.findById(userId);
    if (!user) return -1;

    // Check for duplicate (case-insensitive)
    const existing = user.labels.find(l => l.name.toLowerCase() === labelName.toLowerCase());
    if (existing) return -2;

    // Create new label and add it to the user's labels
    const label = { _id: new mongoose.Types.ObjectId(), name: labelName };
    user.labels.push(label);
    await user.save();

    return label;
}

// Retrieves a specific label by its ID for the given user
async function getLabelById(labelId, userId) {
    // Find the user by userId
    const user = await User.findById(userId);
    if (!user) return -5;

    // Find the label with the given labelId
    const label = user.labels.id(labelId);
    if (!label) return -1;

    return label;
}

// Returns all labels belonging to a specific user
async function getAllLabels(userId) {
    // Find the user by userId
    const user = await User.findById(userId);
    if (!user) return -5;

    // Return all labels of the user
    return user.labels;
}

// Deletes a label by ID for the given user
// Also removes this label from all mailsLabels entries
// Returns true if successful, -1 if label not found, -5 if user not found
async function deleteLabel(labelId, userId) {
   // Find the user by userId
    const user = await User.findById(userId);
    if (!user) return -5;

    // Find index of the label to delete
    const index = user.labels.findIndex(l => l._id.toString() === labelId.toString());
    if (index === -1) return -1;

    // Remove this labelId from all mailsLabels entries
    for (let mailLabels of user.mailsLabels) {
        const labelIndex = mailLabels.labelIds.findIndex(id => id.equals(labelId));
        if (labelIndex !== -1) {
            mailLabels.labelIds.splice(labelIndex, 1);
        }
    }
    // Remove label from user's labels array
    user.labels.splice(index, 1);
    await user.save();

    return true;
}

// Updates the name of a label for the given user
// Returns true if successful, -1 if label not found, -2 if name already exists, -5 if user not found
async function updateLabel(labelId, labelName, userId) {
    // Find the user by userId
    const user = await User.findById(userId);
    if (!user) return -5;

    // Find the label with the given labelId to update
    const label = user.labels.id(labelId);
    if (!label) return -1;

    // Check if name already exists
    const existing = user.labels.find(l => l._id.toString() !== labelId.toString() && l.name.toLowerCase() === labelName.toLowerCase());
    if (existing) return -2;

    // Update label name
    label.name = labelName;
    await user.save();
    // Return the updated label
    return label;  
}


// Adds a label to a specific mail for the given user
// Returns true if successful, -1 if label not found, -2 if mail not found
// -3 if label already exists for this mail, -5 if user not found
async function addLabelToMail(labelId, mailId, userId) {
    // Find the user by userId
    const user = await User.findById(userId);
    if (!user) return -5;

    // Find label
    const label = user.labels.id(labelId);
    if (!label) return -1;

    // Check mail existence in user's allMails
    const hasMail = user.allMails.some(m => m.toString() === mailId.toString());
    if (!hasMail) return -2;

    // Find mail-labels entry
    let mailLabels = user.mailsLabels.find(ml => ml.mailId.toString() === mailId.toString());
    if (!mailLabels) {
        // Create new mail-labels entry
        mailLabels = { mailId, labelIds: [labelId] };
        user.mailsLabels.push(mailLabels);
        await user.save();
        return true;
    }

    // Check duplicate
    if (mailLabels.labelIds.some(id => id.toString() === labelId.toString())) {
        return -3;
    }

    // Add label
    mailLabels.labelIds.push(labelId);
    await user.save();
    return true;
}
// Removes a label from a specific mail for the given user
// Returns true if successful, -1 if label not found, -2 if mail not found
// -3 if mailLabels not found, -4 if label not found in mailLabels, -5 if user not found
async function removeLabelFromMail(labelId, mailId, userId) {
    // Find the user by userId
    const user = await User.findById(userId);
    if (!user) return -5;

    // Find label
    const label = user.labels.id(labelId);
    if (!label) return -1;

    // Check mail existence in user's allMails
    const hasMail = user.allMails.some(m => m.toString() === mailId.toString());
    if (!hasMail) return -2;

    // Find mail-labels entry
    const mailLabels = user.mailsLabels.find(ml => ml.mailId.toString() === mailId.toString());
    if (!mailLabels) { 
        return -3;
    }

    // Find index of the label to delete
    const labelIndex = mailLabels.labelIds.findIndex(l => l.toString() === labelId.toString());
    if (labelIndex === -1) { 
        return -4;
    }
    // Remove the label
    mailLabels.labelIds.splice(labelIndex, 1);

    // If no labels remain, remove the entire mailLabels entry
    if (mailLabels.labelIds.length === 0) {
        user.mailsLabels = user.mailsLabels.filter(ml => !ml.mailId.equals(mailId));
    }
    await user.save();

    return true;
}

// Retrieves all labels (objects) for a specific mail of a user
// Returns an array of label objects, -5 if user not found, -1 if mail
// not found, or an empty array if no labels are assigned
async function getLabelsForMail(mailId, userId) {
    // Find the user by userId
    const user = await User.findById(userId);
    if (!user) return -5;
    // Check if the mail exists in the user's allMails
    const mail = user.allMails.some(m => m.toString() === mailId.toString());
    if (!mail) {
        return -1;
    }
    // Find the mailLabels entry for the given mailId
    const mailLabels = user.mailsLabels.find(ml => ml.mailId.toString() === mailId.toString());
    if (!mailLabels) {
        return [];
    }
    // Return full label objects (not just IDs)
    return user.labels.filter(label => mailLabels.labelIds.some(id => id.equals(label._id)));
}

// Retrieves all mails that have a specific label for a user
// Returns an array of mail objects, -5 if user not found, -1 if label not found
async function getMailsForLabel(labelId, userId) {
    // Find the user by userId
    const user = await User.findById(userId).populate('allMails');
    if (!user) return -5;

    // Find the label with the given labelId
    const label = user.labels.id(labelId);
    if (!label) return -1;

    // Get mailIds that include the label
    const mailIds = user.mailsLabels
        .filter(ml => ml.labelIds.some(id => id.equals(labelId)))
        .map(ml => ml.mailId);

    // Return full mail objects array with no duplicates
    return user.allMails
        .filter(m => mailIds.some(id => id.equals(m._id)))
        .sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));
}

module.exports = { deleteLabel, getAllLabels, getLabelById, createLabel, updateLabel, 
                   addLabelToMail, removeLabelFromMail, getLabelsForMail, getMailsForLabel };
