<p align="center"> <img src="ABAMail.webp" width="150"/> </p>

#  ABA Mail App

The **ABA Mail App** is a fully functioning email platform that provides sending, receiving, and organizing mails with labels. It can handle drafts and spam filtering (via Bloom Filter).  

This project includes:
- A **backend** built with **Node.js (Express)** and **C++ (Bloom Filter)**
- An **Android app** client for user interaction
- **MongoDB** as the database (running in Docker)

All setup and usage instructions are available inside the [`wiki`](./wiki) folder:
- [Setup](./wiki/setup.md)
- [Register and Login](./wiki/auth.md)
- [App UI](./wiki/app.md)
- [Mails](./wiki/mails.md)
- [Labels](./wiki/labels.md)
- [Spam Filtering](./wiki/spam.md)

---

## Project Overview

The app is a real-world email environment:
- Users can **register, log in, send, and receive mails**
- Each user has **Inbox, Sent, Drafts, Spam, and custom label folders**
- Emails containing blacklisted URLs are **automatically moved to the Spam folder** using a **Bloom Filter**
- Data persists using **MongoDB**, allowing consistent storage across sessions


---

## ⚙️ For Developers

### Folder Structure

```
Mail
│
├── backend           # Node.js server + C++ Bloom Filter integration
│
├── android_app       # Android application code
│
├── wiki              # Setup and usage instructions
│
├── data              # Stores the blacklisted URLs
│
├── docker-compose.yml
│
└── README.md
```

### Running and stopping the app

1. Run your **backend server** and **Android app** as described in the [Setup](./wiki/setup.md).
2. Connect to MongoDB using **MongoDB Compass**:
   ```
   mongodb://localhost:27018
   ```

3. When you’re done working:
In the terminal where Docker is running, press:
   ```
   Ctrl + C
   ```
5. To clean up all used containers, run:
   ```bash
   docker compose down --remove-orphans
   ```

### Requirements

| Component | Recommended Version | Notes |
|------------|---------------------|-------|
| **Node.js** | v18.x or higher | Required for backend API server |
| **Docker** | v24.x or higher | To run MongoDB container |
| **Android Studio** | Electric Eel (or newer) | For running the Android client |
| **Gradle Plugin** | 8.x | Automatically configured by Android Studio |
| **C++ Compiler** | g++ 11+ | Required for Bloom Filter module |
