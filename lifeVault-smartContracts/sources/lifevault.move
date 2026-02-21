module LifeVault::LifeVaultV2 {

    use std::string::String;
    use std::vector;
    use std::signer; // Added this
    use aptos_framework::event;
    use aptos_framework::timestamp;

    /// Error Codes
    const ENOT_INITIALIZED: u64 = 1;

    struct Memory has copy, drop, store {
        ipfs_hash: String,
        timestamp: u64,
        note: String,
    }

    struct MemoryVault has key {
        memories: vector<Memory>,
    }

    #[event]
    struct MemoryStored has drop, store {
        user: address,
        ipfs_hash: String,
        timestamp: u64,
    }

    /// Stores a memory with an IPFS hash and a short note
    public entry fun store_memory(
        account: &signer,
        ipfs_hash: String,
        note: String
    ) acquires MemoryVault {
        // Corrected: address_of is in the signer module
        let addr = signer::address_of(account);
        let now = timestamp::now_seconds();

        if (!exists<MemoryVault>(addr)) {
            move_to(account, MemoryVault {
                memories: vector::empty<Memory>(),
            });
        };

        let vault = borrow_global_mut<MemoryVault>(addr);
        let new_memory = Memory {
            ipfs_hash,
            timestamp: now,
            note,
        };

        vector::push_back(&mut vault.memories, new_memory);

        event::emit(MemoryStored {
            user: addr,
            ipfs_hash,
            timestamp: now,
        });
    }

    #[view]
    /// Get all memories for a user
    public fun get_all_memories(user_addr: address): vector<Memory> acquires MemoryVault {
        if (!exists<MemoryVault>(user_addr)) {
            return vector::empty<Memory>()
        };
        borrow_global<MemoryVault>(user_addr).memories
    }

    #[view]
    /// Get count of memories
    public fun get_memory_count(user_addr: address): u64 acquires MemoryVault {
        if (!exists<MemoryVault>(user_addr)) return 0;
        vector::length(&borrow_global<MemoryVault>(user_addr).memories)
    }
}
