const express = require('express');
const router = express.Router();
const controller = require('../controllers/mails');
const auth = require('../middleware/auth');

router.route('/search/:query')
    .get(auth.verifyToken,controller.searchQuery);

router.route('/inbox')
    .get(auth.verifyToken, controller.returnLast50Inbox);

router.route('/sent')
    .get(auth.verifyToken,controller.returnLast50Sent)

router.route('/spam')
    .get(auth.verifyToken, controller.returnLast50Spam);

router.route('/spam/:id')
    .post(auth.verifyToken, controller.addMailToSpam)
    .delete(auth.verifyToken, controller.removeMailFromSpam);

router.route('/drafts')
.get(auth.verifyToken, controller.returnLast50Drafts);

router.route('/')
    .get(auth.verifyToken,controller.returnLast50Mails)
    .post(auth.verifyToken,controller.createMail);

router.route('/:id')
    .get(auth.verifyToken,controller.getMailById)
    .patch(auth.verifyToken,controller.updateMail)
    .delete(auth.verifyToken,controller.deleteMail);
    
module.exports = router;