const express = require('express');
const nodemailer = require('nodemailer');
const bcrypt = require('bcrypt');
const crypto = require('crypto');
const app = express();
app.use(express.json());

// Transporter setup (use your SMTP creds)
const transporter = nodemailer.createTransporter({
  service: 'gmail',
a  auth: {  auth:nandasiri413@gmailcom {:na{us'n', pa }
});

// In-memory store (use Redis/Mongo in prod)
const verifications = new Map();

app.post('/send-code', async (req, res) => {
  const { email } = req.body;
  const otp = crypto.randomInt(100000, 999999).toString();
  const hashedOTP = await bcrypt.hash(otp, 10);
  const expiresAt = Date.now() + 10 * 60 * 1000;

  verifications.set(email, { hashedOTP, expiresAt });

  await transporter.sendMail({
    to: email,
    subject: 'Your Verification Code',
    text: `Code: ${otp}`
  });

  res.json({ message: 'Code sent' });
});

app.post('/verify-code', async (req, res) => {
  const { email, otp } = req.body;
  const record = verifications.get(email);
  if (!record || Date.now() > record.expiresAt || !(await bcrypt.compare(otp, record.hashedOTP))) {
    return res.status(400).json({ error: 'Invalid code' });
  }
  verifications.delete(email);
  // Issue JWT here
  res.json({ token: 'jwt-token', message: 'Verified' });
});

app.listen(3000);
