// src/main/java/com/codebyte/lifevault_dapp/core/AptosClient.kt
package com.codebyte.lifevault_dapp.core

import android.util.Log
import com.codebyte.lifevault_dapp.data.MemoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.BufferedSink
import okio.ByteString.Companion.decodeHex
import okio.ByteString.Companion.toByteString
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AptosClient(private val cryptoManager: CryptoManager) {

    companion object {
        private const val TAG = "AptosClient"
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Custom RequestBody to avoid charset
    private class JsonRequestBody(private val json: String) : RequestBody() {
        override fun contentType() = "application/json".toMediaType()
        override fun writeTo(sink: BufferedSink) {
            sink.writeUtf8(json)
        }
    }

    // Get balance
    suspend fun getBalance(address: String): Long = withContext(Dispatchers.IO) {
        try {
            val resourceType = "0x1::coin::CoinStore<0x1::aptos_coin::AptosCoin>"
            val url = "${AptosConfig.NODE_URL}/accounts/$address/resource/$resourceType"
            val request = Request.Builder().url(url).get().build()

            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext 0L

                val json = JSONObject(response.body?.string() ?: "{}")
                val data = json.optJSONObject("data") ?: return@withContext 0L
                val coin = data.optJSONObject("coin") ?: return@withContext 0L
                coin.optString("value", "0").toLongOrNull() ?: 0L
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get balance", e)
            0L
        }
    }

    // Fund from faucet
    suspend fun fundFromFaucet(address: String, amount: Long = 100_000_000): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = "${AptosConfig.FAUCET_URL}/mint?amount=$amount&address=$address"
            val request = Request.Builder()
                .url(url)
                .post(JsonRequestBody(""))
                .build()

            httpClient.newCall(request).execute().use { response ->
                Log.d(TAG, "Faucet: ${response.code}")
                response.isSuccessful
            }
        } catch (e: Exception) {
            Log.e(TAG, "Faucet failed", e)
            false
        }
    }

    // Fetch memories (View Function)
    suspend fun fetchUserMemories(address: String): List<MemoryItem> = withContext(Dispatchers.IO) {
        try {
            val url = "${AptosConfig.NODE_URL}/view"

            val payload = JSONObject().apply {
                put("function", "${AptosConfig.MODULE_ADDRESS}::${AptosConfig.MODULE_NAME}::get_memories")
                put("type_arguments", JSONArray())
                put("arguments", JSONArray().apply { put(address) })
            }

            val request = Request.Builder()
                .url(url)
                .post(JsonRequestBody(payload.toString()))
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext emptyList()

                val jsonArray = JSONArray(response.body?.string() ?: "[]")
                if (jsonArray.length() == 0) return@withContext emptyList()

                val memoriesArray = jsonArray.getJSONArray(0)
                val list = mutableListOf<MemoryItem>()

                for (i in 0 until memoriesArray.length()) {
                    val mem = memoriesArray.getJSONObject(i)
                    list.add(MemoryItem(
                        id = i + 1,
                        title = mem.optString("title", "Secured Memory"),
                        date = "Secured on Aptos",
                        ipfsHash = mem.optString("ipfs_hash", ""),
                        isSecured = true
                    ))
                }
                list
            }
        } catch (e: Exception) {
            Log.e(TAG, "Fetch failed", e)
            emptyList()
        }
    }

    // Register memory - REAL IMPLEMENTATION
    suspend fun registerMemory(title: String, ipfsHash: String): String = withContext(Dispatchers.IO) {
        try {
            val senderAddress = cryptoManager.getAddress()
            if (senderAddress.isEmpty()) throw IllegalStateException("Wallet not connected")

            // 1. Get Account Sequence Number
            val accountInfo = getAccountInfo(senderAddress)
            val sequenceNumber = accountInfo.sequenceNumber

            // 2. Build Transaction Payload
            // Note: Expiration is set to +10 minutes from now (in seconds)
            val expirationTimestamp = (System.currentTimeMillis() / 1000) + 600

            val txnPayload = JSONObject().apply {
                put("sender", senderAddress)
                put("sequence_number", sequenceNumber)
                put("max_gas_amount", "200000") // Adjust as needed
                put("gas_unit_price", "100")    // Adjust based on network
                put("expiration_timestamp_secs", expirationTimestamp.toString())
                put("payload", JSONObject().apply {
                    put("type", "entry_function_payload")
                    put("function", "${AptosConfig.MODULE_ADDRESS}::${AptosConfig.MODULE_NAME}::register_memory")
                    put("type_arguments", JSONArray())
                    put("arguments", JSONArray().apply {
                        put(title)
                        put(ipfsHash)
                    })
                })
            }

            // 3. Encode Submission (Get Signing Message)
            // We use the Node API to encode the transaction into BCS bytes (hex string)
            val encodeUrl = "${AptosConfig.NODE_URL}/transactions/encode_submission"
            val encodeRequest = Request.Builder()
                .url(encodeUrl)
                .post(JsonRequestBody(txnPayload.toString()))
                .build()

            val signingMessageHex = httpClient.newCall(encodeRequest).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorBody = response.body?.string()
                    throw Exception("Failed to encode transaction: $errorBody")
                }
                // The API returns the hex string of the message to sign (usually in double quotes)
                response.body?.string()?.replace("\"", "") ?: ""
            }

            if (signingMessageHex.isEmpty()) throw Exception("Empty signing message received")

            // 4. Sign the Message
            // Helper to decode hex string to byte array
            val messageBytes = signingMessageHex.decodeHex().toByteArray()
            val signatureBytes = cryptoManager.signMessage(messageBytes)
            val signatureHex = signatureBytes.toByteString().hex()
            val publicKeyHex = cryptoManager.getPublicKeyBytes()?.toByteString()?.hex()
                ?: throw IllegalStateException("Public key not found")

            // 5. Submit Signed Transaction
            // Add signature to the payload
            val signaturePayload = JSONObject().apply {
                put("type", "ed25519_signature")
                put("public_key", "0x$publicKeyHex")
                put("signature", "0x$signatureHex")
            }
            txnPayload.put("signature", signaturePayload)

            val submitUrl = "${AptosConfig.NODE_URL}/transactions"
            val submitRequest = Request.Builder()
                .url(submitUrl)
                .post(JsonRequestBody(txnPayload.toString()))
                .build()

            httpClient.newCall(submitRequest).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorBody = response.body?.string()
                    Log.e(TAG, "Submission failed: $errorBody")
                    throw Exception("Transaction submission failed: ${response.code}")
                }

                val responseJson = JSONObject(response.body?.string() ?: "{}")
                val hash = responseJson.optString("hash")
                Log.d(TAG, "Transaction Submitted: $hash")
                hash
            }

        } catch (e: Exception) {
            Log.e(TAG, "Register failed", e)
            throw e
        }
    }

    private suspend fun getAccountInfo(address: String): AccountInfo = withContext(Dispatchers.IO) {
        val url = "${AptosConfig.NODE_URL}/accounts/$address"
        val request = Request.Builder().url(url).get().build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                // If account not found (404), it might need funding
                if (response.code == 404) throw Exception("Account not found. Did you use the faucet?")
                throw Exception("Failed to fetch account info: ${response.code}")
            }
            val json = JSONObject(response.body?.string() ?: "{}")
            AccountInfo(
                sequenceNumber = json.optString("sequence_number", "0"),
                authenticationKey = json.optString("authentication_key", "")
            )
        }
    }

    data class AccountInfo(
        val sequenceNumber: String,
        val authenticationKey: String
    )
}