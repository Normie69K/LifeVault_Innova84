# 🏛️ LifeVault Smart Contracts (Aptos Move)

Welcome to the core smart contract repository for the **LifeVault**. 

LifeVault is a decentralized application (dApp) that bridges digital permanence with real-world experiences. This repository contains the on-chain logic written in **Aptos Move**, responsible for minting, storing, and managing digital memories as owned assets on the Aptos blockchain.

## 🛠️ Tech Stack & Language
* **Language:** [Move](https://aptos.dev/en/build/smart-contracts/book/SUMMARY) (specifically the Aptos flavor). Move is a secure, Rust-inspired smart contract language designed to prevent common Web3 vulnerabilities like reentrancy attacks.
* **Blockchain:** Aptos (Devnet / Testnet / Mainnet)
* **Framework:** AptosFramework v1

## 📂 Project Structure


```text
lifeVault-smartContracts/
├── Move.toml                 # Package manifest, dependencies, and named addresses
├── sources/                  
│   └── lifevault.move        # The core smart contract logic and data structures
└── README.md                 # This documentation file

```

---

## 🚀 Getting Started: Deploying Your Own Vault

Follow this guide to deploy your own instance of the LifeVault contract to the Aptos blockchain.

### 1. Prerequisites

You must have the **Aptos CLI** installed on your machine.

* **Mac/Linux:** `brew install aptos` or use the install script: `curl -fsSL "https://aptos.dev/scripts/install_cli.py" | bash`
* **Windows:** Install via Python scripts or download the pre-compiled binaries from the official Aptos Labs GitHub.

Verify your installation:

```bash
aptos --version

```

### 2. Initialize Your Publisher Account

Open your terminal, navigate into this repository, and initialize a new Aptos workspace. This creates a local wallet to sponsor your contract deployment.

```bash
cd lifeVault-smartContracts
aptos init

```

* Select your preferred network (e.g., `testnet` or `devnet`).
* The CLI will automatically generate a private key and a **Wallet Address**.
* **⚠️ IMPORTANT:** Copy the Wallet Address that the terminal outputs.

### 3. Configure the Manifest

Open the `Move.toml` file in your code editor. Under the `[addresses]` section, replace the underscore `_` placeholder with the wallet address you just generated.

```toml
[addresses]
life_vault = "0xYOUR_GENERATED_WALLET_ADDRESS_HERE"

```

*This tells the compiler exactly where this contract will live on the blockchain.*

### 4. Compile the Code

Before deploying, verify that the Move code compiles successfully and that there are no syntax errors.

```bash
aptos move compile

```

### 5. Publish to the Blockchain

Deploy your contract to the Aptos network. If you are using Testnet or Devnet, the CLI will automatically request free test-APT from the faucet to pay the deployment gas fee.

```bash
aptos move publish

```

Type `yes` to confirm the transaction. Once complete, your smart contract is live! 🎉

---

## 🔌 Interacting with the Contract

Once deployed, your frontend application or backend gas relayer can interact with the contract using the Aptos SDKs (TypeScript/Python/Rust).

To call a function from your dApp, you will target the module address, the module name, and the function name.
Example payload structure for a frontend transaction:

```javascript
const payload = {
  type: "entry_function_payload",
  function: "0xYOUR_GENERATED_WALLET_ADDRESS_HERE::LifeVault::mint_memory",
  type_arguments: [],
  arguments: ["ipfs://...", "Memory Title", "Location Data"]
};

```

