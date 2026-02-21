# ⚙️ LifeVault API & Gas Relayer (Backend)

Welcome to the backend architecture for the **LifeVault Protocol**. 

This Node.js/Express application serves as the central nervous system of the LifeVault ecosystem. It manages traditional database state (user profiles, campaigns), handles AI verification via Google Gemini, interacts with IPFS for decentralized media storage, and acts as a **Gas Relayer** to sponsor Aptos blockchain transactions for a seamless Web2-to-Web3 onboarding experience.

## 🛠️ Tech Stack

* **Runtime:** Node.js
* **Framework:** Express.js
* **Database:** MongoDB (via Mongoose)
* **Blockchain SDK:** `@aptos-labs/ts-sdk`
* **Decentralized Storage:** Pinata API (IPFS)
* **AI Engine:** Google Gemini API (Vision for Proof-of-Experience)

---

## 📂 Project Architecture

The codebase follows an MVC (Model-View-Controller) inspired service architecture to keep business logic modular:

```text
lifeVault-backEnd/
├── src/
│   ├── config/             # Database connection and environment variables
│   ├── controllers/        # Request/Response logic for API endpoints
│   ├── middleware/         # JWT Authentication and error handling
│   ├── models/             # Mongoose schemas (User, Memory, Quest, Campaign)
│   ├── routes/             # Express route definitions (e.g., /api/memories)
│   ├── services/           # 🧠 Core integrations (Aptos, IPFS, Gemini, Geolocation)
│   ├── utils/              # Helper functions and seeding scripts
│   ├── app.js              # Express app initialization and global middleware
│   └── server.js           # Main entry point and server listener
├── .env                    # Environment variables (Ignored by git)
└── package.json            # Project dependencies and npm scripts

```

---

## 🚀 Local Setup Guide

Follow these steps to run the LifeVault Backend API on your local machine.

### 1. Prerequisites

* [Node.js](https://nodejs.org/) (v18+ recommended)
* A running instance of MongoDB (Local instance or [MongoDB Atlas](https://www.mongodb.com/atlas/database) URI)
* API Keys for Pinata, Google Gemini, and an Aptos Wallet Private Key.

### 2. Install Dependencies

Open your terminal, navigate to the backend directory, and install the required packages:

```bash
npm install

```

### 3. Environment Variables

Create a file named `.env` in the root of the `lifeVault-backEnd` directory. Use the following template and fill in your specific keys:

```env
# Server
PORT=5000
NODE_ENV=development

# Database
MONGODB_URI=mongodb://localhost:27017/lifevault  # Or your MongoDB Atlas URI

# Security
JWT_SECRET=your_super_secret_jwt_key_here
JWT_EXPIRES_IN=7d

# IPFS (Pinata)
PINATA_API_KEY=your_pinata_api_key
PINATA_SECRET_API_KEY=your_pinata_secret_key

# Google Gemini AI (For Quest Verification)
GEMINI_API_KEY=your_google_gemini_api_key

# Aptos Blockchain (Gas Relayer Wallet)
APTOS_NETWORK=testnet
APTOS_PRIVATE_KEY=your_master_wallet_private_key_here
APTOS_MODULE_ADDRESS=0xYOUR_DEPLOYED_SMART_CONTRACT_ADDRESS

```

### 4. Run the Development Server

Start the server using `nodemon` for auto-reloading during development:

```bash
npm run dev

```

*(If the setup is successful, your terminal will log "Server running on port 5000" and "Connected to MongoDB").*

---

## 🌍 Online Deployment (Production)

This backend is optimized for deployment on cloud platforms like **Render**, **Railway**, or **Heroku**.

1. Create a "Web Service" on your chosen cloud provider.
2. Connect this GitHub repository.
3. Ensure the **Root Directory** is set to `lifeVault-backEnd` (or leave blank if this is a standalone repo).
4. Set the **Build Command** to: `npm install`
5. Set the **Start Command** to: `npm start` (This executes `node src/server.js`).
6. Add all the environment variables from your `.env` file into the cloud provider's environment variables dashboard.
7. Deploy! Your API will now be live and accessible to your frontend application.

---

## 🔑 Key Features Overview

* **Gasless Transactions:** Web2 users don't need to hold APT tokens. The `aptosService.js` utilizes a master wallet (via the `APTOS_PRIVATE_KEY`) to sponsor minting transactions, paying the gas fees on behalf of the user.
* **AI Verification (`aiVisionService.js`):** When a user completes a physical quest (e.g., taking a photo at a specific landmark), the Gemini Vision API analyzes the image to verify they are actually there before unlocking the reward.
* **Hybrid Storage (`ipfsService.js`):** Heavy media files (photos/videos) are pinned to IPFS for decentralized permanence, while lightweight indexing data is kept in MongoDB for fast querying.
