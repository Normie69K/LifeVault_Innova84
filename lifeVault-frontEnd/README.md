# 🌐 LifeVault : Architecture & Setup Guide

## 🎯 1. What Are We Building? (The Purpose)

The `lifeVault-frontEnd` is the **B2B2C Web Application** for the LifeVault ecosystem. It acts as the command center for two types of users:

1. **Explorers (Consumers):** Connect their Web3 wallets, view their decentralized memory timelines, map out physical quests, and bundle memories into "Stories" or "Time Capsules".
2. **Creators (Businesses):** Access a dedicated dashboard to create location-based campaigns (Quests), fund reward pools with APT, and track user engagement.

## 💻 2. Languages & Core Technologies

The project is built using modern, strictly typed web technologies to ensure scalability and fast performance.

* **Primary Language:** **TypeScript (`.ts`, `.tsx`)** - Provides static typing over JavaScript to catch errors early.
* **UI Library:** **React 18** - Component-based architecture for building interactive user interfaces.
* **Build Tool:** **Vite** - An ultra-fast development server and bundler utilizing the Rust-based SWC compiler.
* **Styling:** **Tailwind CSS** - Utility-first CSS framework for rapid UI styling.
* **UI Components:** **Radix UI & shadcn/ui** - Headless, accessible components (modals, dropdowns, accordions) styled with Tailwind.
* **Web3 Integration:** **Aptos Wallet Adapter** (`@aptos-labs/wallet-adapter-react`) - Manages connections to wallets like Petra.
* **Mapping:** **Leaflet & React-Leaflet** - Renders the interactive "Quest Map" for location-based campaigns.

---

## 📂 3. Detailed Folder Structure


```text
lifeVault-frontEnd/
├── public/                     # Static assets (images, robots.txt, placeholder SVGs)
├── src/                        # 🧠 CORE APPLICATION CODE
│   ├── components/             # Reusable UI Blocks
│   │   ├── dashboard/          # UI specific to user/business dashboards (Timeline, StatsCards, AddMemoryModal)
│   │   ├── landing/            # Public-facing website sections (Hero, Features, Security)
│   │   ├── layout/             # Page wrappers (DashboardLayout, BusinessLayout, Navbar)
│   │   ├── story/              # Components for grouping memories (CreateStoryModal, StoryList)
│   │   ├── ui/                 # Base design system components (Buttons, Inputs, Dialogs via shadcn)
│   │   └── wallet/             # Web3 components (ConnectWalletButton, WalletSelector)
│   ├── context/                # Global State Management
│   │   ├── AuthContext.tsx     # Manages JWT tokens, user sessions, and login states
│   │   └── WalletContext.tsx   # Wraps the app in the Aptos Wallet provider
│   ├── hooks/                  # Custom React Hooks
│   │   └── useMemories.ts      # Data fetching logic for the timeline
│   ├── pages/                  # Route Views (The actual screens)
│   │   ├── business/           # Brand-facing pages (BusinessLogin, BusinessDashboard)
│   │   ├── Dashboard.tsx       # Main user feed
│   │   ├── QuestMap.tsx        # Interactive map of nearby memory campaigns
│   │   └── Stories.tsx         # User's curated collections
│   ├── services/               # API & External Integrations
│   │   ├── api.ts              # Base Axios instance connecting to the Node.js backend
│   │   ├── aptosWallet.ts      # Aptos blockchain interaction logic
│   │   ├── questApi.ts         # Endpoints for fetching/completing geolocation quests
│   │   └── blockchainApi.ts    # Logic for interacting with the Smart Contract
│   ├── types/                  # TypeScript interface definitions (e.g., Memory, User, Quest)
│   ├── App.tsx                 # Main router configuration linking pages to URLs
│   └── main.tsx                # React DOM entry point
├── .env                        # Environment variables
├── tailwind.config.ts          # Tailwind theme colors and utility setup
└── vite.config.ts              # Build server configuration and path aliases

```

---

## ⚙️ 4. Step-by-Step Setup Guide

### Step 1: System Requirements

Ensure you have the following installed:

* **Node.js** (v18 or higher)
* **npm** (Node Package Manager)


### Step 2: Install Dependencies

Download all the required React, Aptos, and UI packages defined in the `package.json`.

```bash
npm install

```

### Step 3: Configure the Environment

The application needs to know how to talk to your backend API, IPFS, and the Aptos Blockchain. The `.env` file should look exactly like this:

```env
# 1. Backend Connection
VITE_API_URL=http://localhost:5000/api

# 2. Decentralized Storage Gateway
VITE_IPFS_GATEWAY=https://gateway.pinata.cloud/ipfs

# 3. Aptos Blockchain Configuration
VITE_APTOS_API_KEY=aptoslabs_4vJJL5uc7VY_4Ad178JY51P7hzHupupfZEyHs3KFcLUXM
VITE_APTOS_NETWORK=devnet
VITE_APTOS_MODULE_ADDRESS=0x599c19cd1f5a85d4eb4f403337bee2c26a8259b43c6cd0c9b6cdfd63d3874cc6

```

*Crucial Note: Your Node.js backend (`backEnd/src/server.js`) must be running on port 5000 for the frontend to log in or fetch data.*

### Step 4: Run the Development Server

Start the application in development mode with Hot-Module Replacement (HMR).

```bash
npm run dev

```

The terminal will output a local address (usually `http://localhost:5173`). Open this link in your web browser.

---

## 🏗️5. Key Architectural Workflows

How do the files interact when a user does something?

**1. Connecting a Wallet:**

* User clicks "Connect" -> `src/components/wallet/ConnectWalletButton.tsx` triggers.
* This opens `src/components/wallet/WalletSelector.tsx`, utilizing the `@aptos-labs/wallet-adapter-react` library.
* Once connected, the wallet address is stored globally via `src/context/WalletContext.tsx`.

**2. Viewing Memories:**

* User visits the `/dashboard` route (`src/pages/Dashboard.tsx`).
* The page calls the custom hook `src/hooks/useMemories.ts`.
* The hook utilizes `src/services/api.ts` to make an HTTP GET request to `http://localhost:5000/api/memories`.
* The data is passed down to `src/components/dashboard/Timeline.tsx` and mapped into individual `MemoryCard.tsx` components.

**3. Participating in Quests:**

* User navigates to `/map` (`src/pages/QuestMap.tsx`).
* The page uses `Leaflet` to render a map and queries `src/services/questApi.ts` to drop pins on the map representing physical campaigns.
