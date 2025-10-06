const express = require('express');
const router = express.Router();
const controller = require('../controllers/labels');
const auth = require('../middleware/auth');

router.route('/')
    .get(auth.verifyToken,controller.getAllLabels)
    .post(auth.verifyToken,controller.createLabel);

router.route('/:id')
    .get(auth.verifyToken,controller.getLabelById)
    .patch(auth.verifyToken,controller.updateLabel)
    .delete(auth.verifyToken,controller.deleteLabel);

// Label - Mail actions
router.route('/mail')
    // Add label to user's mail
    .post(auth.verifyToken, controller.addLabelToMail);

router.route('/mail/:mailId')
    // Get all labels for a user's mail
    .get(auth.verifyToken, controller.getLabelsForMail); 

router.route('/folder/:labelId')
    // Get all user's mails for a specific label
    .get(auth.verifyToken, controller.getMailsForLabel);

router.route('/mail/:mailId/:labelId')
    // Remove label from user's mail
    .delete(auth.verifyToken, controller.removeLabelFromMail);

module.exports = router;