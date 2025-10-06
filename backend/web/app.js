const express = require('express');
const app = express();
const cors = require('cors');
require('dotenv').config();

const PORT = 8080;

// Mongo DB
const mongoose = require('mongoose');
mongoose.connect(process.env.MONGO_URL, {
  useNewUrlParser: true,
  useUnifiedTopology: true
})
  .then(() => console.log('MongoDB connected'))
  .catch(err => console.error('MongoDB connection error:', err));

const blacklistRoutes = require('./routes/blacklists');
const mailRoutes = require('./routes/mailRoutes');
const userRoutes = require('./routes/userRoutes');
const tokenRoutes = require('./routes/tokenRoutes');
const labelRoutes = require('./routes/labelRoutes');
app.get('/', (req, res) => {
  res.send('API is running');
});

app.use(cors());
app.use(express.json({ limit: '10mb' }));
app.use(express.json());
app.use('/api/blacklist', blacklistRoutes);
app.use('/api/tokens', tokenRoutes);
app.use('/api/mails', mailRoutes);
app.use('/api/users', userRoutes);
app.use('/api/labels', labelRoutes);

app.listen(PORT);

