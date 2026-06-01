package com.daily.app.util

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * 加密工具类.
 *
 * 使用 Android Keystore 管理系统密钥，采用 AES/GCM/NoPadding 模式对手机号等敏感信息进行加密和解密。
 * GCM 模式提供认证加密（AEAD），确保数据的机密性和完整性。
 *
 * - 密钥存储在 Android Keystore 中，只能由本应用访问
 * - 使用 AES-256-GCM 算法，12 字节 IV，128 位认证标签
 * - 加密时 IV 与密文一起存储（格式: IV || ciphertext）
 */
object CryptoUtil {

    /** Keystore 提供者名称 */
    private const val KEYSTORE_PROVIDER = "AndroidKeyStore"

    /** 密钥别名 - 用于在 Keystore 中标识本应用的加密密钥 */
    private const val KEY_ALIAS = "daily_crypto_key"

    /** GCM 标签长度（位）*/
    private const val GCM_TAG_LENGTH_BIT = 128

    /** IV 长度（字节）*/
    private const val IV_LENGTH = 12

    /**
     * 确保 Keystore 中存在加密密钥。
     * 如果密钥不存在则创建一个新的 AES 密钥。
     */
    @Synchronized
    fun ensureKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }

        return if (keyStore.containsAlias(KEY_ALIAS)) {
            val key = keyStore.getKey(KEY_ALIAS, null) as SecretKey
            key
        } else {
            val parameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()

            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                KEYSTORE_PROVIDER
            )
            keyGenerator.init(parameterSpec)
            keyGenerator.generateKey()

            val key = keyStore.getKey(KEY_ALIAS, null) as SecretKey
            key
        }
    }

    /**
     * 使用 AES-256-GCM 加密明文.
     *
     * 返回格式为 Base64 编码的字符串，内部结构为 IV (12 bytes) || ciphertext。
     * 解密时需使用 [decrypt] 方法。
     *
     * @param plainText 待加密的明文字符串
     * @return Base64 编码的密文字符串
     * @throws Exception 加密失败时抛出异常
     */
    fun encrypt(plainText: String): String {
        val key = ensureKey()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)

        val iv = cipher.iv
        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        // 将 IV 和密文拼接后 Base64 编码
        val combined = iv + encrypted
        return android.util.Base64.encodeToString(combined, android.util.Base64.NO_WRAP)
    }

    /**
     * 使用 AES-256-GCM 解密密文.
     *
     * @param encryptedText Base64 编码的密文字符串（由 [encrypt] 生成）
     * @return 解密后的明文字符串
     * @throws Exception 解密失败时抛出异常（如密钥不匹配、数据损坏）
     */
    fun decrypt(encryptedText: String): String {
        val key = ensureKey()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        val combined = android.util.Base64.decode(encryptedText, android.util.Base64.NO_WRAP)
        if (combined.size <= IV_LENGTH) {
            throw IllegalArgumentException("Invalid encrypted data: too short")
        }

        val iv = combined.copyOfRange(0, IV_LENGTH)
        val encryptedData = combined.copyOfRange(IV_LENGTH, combined.size)

        val spec = GCMParameterSpec(GCM_TAG_LENGTH_BIT, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)

        val decrypted = cipher.doFinal(encryptedData)
        return String(decrypted, Charsets.UTF_8)
    }

    /**
     * 生成随机 IV（通常不需要手动调用，encrypt 内部会自动生成）.
     */
    fun generateIv(): ByteArray {
        val iv = ByteArray(IV_LENGTH)
        SecureRandom().nextBytes(iv)
        return iv
    }
}
