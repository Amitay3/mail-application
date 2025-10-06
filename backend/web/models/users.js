// // Array to hold all user objects
// const allUsers = [];

// const { generateId } = require('../middleware/generateId')
// let userCount = 0;

const mongoose = require('mongoose');

// Define how a user document looks in MongoDB
const userSchema = new mongoose.Schema({
    // User details
    userName: { type: String, required: true },
    password: { type: String, required: true },
    mailAddress: { type: String, required: true, unique: true },
    image: { type: String, default: 'default' },
    // Mail folders (arrays of Mail ObjectIds)
    inbox: { type: [mongoose.Schema.Types.ObjectId], ref: 'Mail', default: [] },
    sent: { type: [mongoose.Schema.Types.ObjectId], ref: 'Mail', default: [] },
    drafts: { type: [mongoose.Schema.Types.ObjectId], ref: 'Mail', default: [] },
    spam: { type: [mongoose.Schema.Types.ObjectId], ref: 'Mail', default: [] },
    allMails: { type: [mongoose.Schema.Types.ObjectId], ref: 'Mail', default: [] },
    // Labels for user
    labels: {
        type: [
            {
                _id: { type: mongoose.Schema.Types.ObjectId, auto: true },
                name: { type: String, required: true }
            }
        ],
        default: []
    },
    // mailsLabels is an array of mailLabels which contains mail and its labels
    mailsLabels: {
        type: [{
            _id: false,
            mailId: { type: mongoose.Schema.Types.ObjectId, ref: 'Mail', required: true },
            labelIds: { type: [mongoose.Schema.Types.ObjectId], ref: 'Label', default: [] }
        }],
        default: []
    }
});



// return values
// -1 if Email already exists
// -2 if Invalid email (must end with "@abamail.com")
// -3 if Invalid password (must have >=8 chars, 1 uppercase, 1 digit)
// -4 if Verified password does not match password
// else returns the created user document
userSchema.statics.createUser = async function (userName, password, verifiedPassword, mailAddress, base64Image) {
    // Mail address is already normalized in controller
    // Validations
    if (!mailAddress.endsWith('@abamail.com')) {
        return -2;
    }
    if (!/^(?=.*[A-Z])(?=.*\d).{8,}$/.test(password)) {
        return -3;
    }
    if (password !== verifiedPassword) {
        return -4;
    }

    // Check if mail already exists in DB
    const existing = await this.findOne({ mailAddress });
    if (existing) {
        return -1;
    }

    // Save new user to MongoDB
    const newUser = await this.create({
        userName,
        password,
        mailAddress,
        image: base64Image
    });
    return newUser;
};


// Create model from schema
const User = mongoose.model('User', userSchema);

// Authenticates user by mail address and password, returns user object or null if no match
async function authenticateUser(mailAddress, password) {
    mailAddress = mailAddress.toLowerCase();
    // Find user
    const user = await User.findOne({ mailAddress, password });
    return user;
}

// exports the User model
module.exports = User;

// exports the authenticateUser function
module.exports.authenticateUser = authenticateUser;
