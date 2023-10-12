package com.truedigital.core.utils

import android.util.Base64
import org.apache.commons.codec.binary.Hex
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

interface EncryptUtil {
    fun md5(inputString: String): String
    fun encryptToSHA256(plainText: String, key: String, iv: String): String
    fun encryptIserviceToSHA256(plainText: String, key: String, iv: String): String
    fun decryptToSHA256(encryptedText: String, key: String, iv: String): String
    fun generateRandomIV(length: Int): String
    fun encryptAES128Base64(plainText: String, key: String, iv: String): String
    fun decryptAES128Base64(encryptedText: String, key: String, iv: String): String
    fun decryptToSHA128(encryptedText: String, key: String, iv: String): String
    fun encryptBase64(plainText: String): String
    fun decryptBase64(encryptedText: String): String
    fun encryptAES256Base64(plainText: String, key: String, iv: String): String
    fun decryptAES256Base64(encryptedText: String, key: String, iv: String): String
}

class EncryptUtilImpl @Inject constructor() : EncryptUtil {

    /**
     * Encryption mode enumeration
     */
    private enum class EncryptMode {
        ENCRYPT, DECRYPT
    }

    private companion object {
        private const val MD5 = "MD5"
    }

    // cipher to be used for encryption and decryption
    private var cx: Cipher? = null

    // encryption key and initialization vector
    private var key: ByteArray? = null
    private var iv: ByteArray? = null

    private var ivSpec: IvParameterSpec? = null
    private var keySpec: SecretKeySpec? = null
    private var cipher: Cipher? = null

    init {
        cx = Cipher.getInstance("AES/CBC/PKCS5Padding")
        key = ByteArray(32) // 256 bit key space
        iv = ByteArray(16) // 128 bit IV
    }

    /**
     * Note: This function is no longer used.
     * This function generates md5 hash of the input string
     * @param inputString
     *
     * @return md5 hash of the input string
     */
    override fun md5(inputString: String): String {
        try {
            // Create MD5 Hash
            val digest = MessageDigest
                .getInstance(MD5)
            digest.update(inputString.toByteArray())
            val messageDigest = digest.digest()

            // Create Hex String
            val hexString = StringBuilder()
            for (aMessageDigest in messageDigest) {
                var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                while (h.length < 2)
                    h = "0$h"
                hexString.append(h)
            }
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return ""
    }

    /**
     *
     * @param _inputText
     * Text to be encrypted or decrypted
     * @param _encryptionKey
     * Encryption key to used for encryption / decryption
     * @param _mode
     * specify the mode encryption / decryption
     * @param _initVector
     * Initialization vector
     * @return encrypted or decrypted string based on the mode
     * @throws UnsupportedEncodingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    @Throws(
        UnsupportedEncodingException::class,
        InvalidKeyException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    private fun encryptDecrypt(
        _inputText: String,
        _encryptionKey: String,
        _mode: EncryptMode,
        _initVector: String
    ): String {
        var out = "" // output string
        // _encryptionKey = md5(_encryptionKey);
        // System.out.println("key="+_encryptionKey);

        var len = _encryptionKey.toByteArray(charset("UTF-8")).size // length of the key	provided

        if (_encryptionKey.toByteArray(charset("UTF-8")).size > key!!.size)
            len = key!!.size

        var ivlen = _initVector.toByteArray(charset("UTF-8")).size

        if (_initVector.toByteArray(charset("UTF-8")).size > iv!!.size)
            ivlen = iv!!.size

        System.arraycopy(_encryptionKey.toByteArray(charset("UTF-8")), 0, key, 0, len)
        System.arraycopy(_initVector.toByteArray(charset("UTF-8")), 0, iv, 0, ivlen)
        // KeyGenerator _keyGen = KeyGenerator.getInstance("AES");
        // _keyGen.init(128);

        val keySpec = SecretKeySpec(key, "AES") // Create a new SecretKeySpec
        val ivSpec = IvParameterSpec(iv) // Create a new

        // encryption
        if (_mode == EncryptMode.ENCRYPT) {
            cx!!.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec) // Initialize this cipher instance
            val results = cx!!.doFinal(_inputText.toByteArray(charset("UTF-8"))) // Finish
            out = Base64.encodeToString(results, Base64.DEFAULT) // ciphertext
            // output
        }

        // decryption
        if (_mode == EncryptMode.DECRYPT) {
            cx!!.init(Cipher.DECRYPT_MODE, keySpec, ivSpec) // Initialize this ipher instance

            val decodedValue = Base64.decode(
                _inputText.toByteArray(),
                Base64.DEFAULT
            )
            val decryptedVal = cx!!.doFinal(decodedValue) // Finish
            out = String(decryptedVal)
        }
        println(out)
        return out // return encrypted/decrypted string
    }

    /***
     * This function computes the SHA256 hash of input string
     * @param text input text whose SHA256 hash has to be computed
     * @param length length of the text to be returned
     * @return returns SHA256 hash of input text
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    @Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class)
    private fun SHA256(text: String, length: Int): String {

        val resultStr: String
        val md = MessageDigest.getInstance("SHA-256")

        md.update(text.toByteArray(charset("UTF-8")))
        val digest = md.digest()

        val result = StringBuffer()
        for (b in digest) {
            result.append(String.format("%02x", b)) // convert to hex
        }
        // return result.toString();

        if (length > result.toString().length) {
            resultStr = result.toString()
        } else {
            resultStr = result.toString().substring(0, length)
        }

        return resultStr
    }

    /***
     * This function encrypts the plain text to cipher text using the key
     * provided. You'll have to use the same key for decryption
     *
     * @param _plainText
     * Plain text to be encrypted
     * @param _key
     * Encryption Key. You'll have to use the same key for decryption
     * @param _iv
     * initialization Vector
     * @return returns encrypted (cipher) text
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */

    @Throws(
        InvalidKeyException::class,
        UnsupportedEncodingException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    override fun encryptToSHA256(plainText: String, key: String, iv: String): String {
        val key = SHA256(key, 32)
        return encryptDecrypt(plainText, key, EncryptMode.ENCRYPT, iv)
    }

    @Throws(
        InvalidKeyException::class,
        UnsupportedEncodingException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    override fun encryptIserviceToSHA256(plainText: String, key: String, iv: String): String {
        val key = SHA256(key, 32)
        val plainTextWithRandom = generateRandomIV(16) + plainText
        return encryptDecrypt(plainTextWithRandom, key, EncryptMode.ENCRYPT, iv)
    }

    /***
     * This funtion decrypts the encrypted text to plain text using the key
     * provided. You'll have to use the same key which you used during
     * encryprtion
     *
     * @param _encryptedText
     * Encrypted/Cipher text to be decrypted
     * @param _key
     * Encryption key which you used during encryption
     * @param _iv
     * initialization Vector
     * @return encrypted value
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    @Throws(
        InvalidKeyException::class,
        UnsupportedEncodingException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    override fun decryptToSHA256(encryptedText: String, key: String, iv: String): String {
        val key = SHA256(key, 32)
        var out = encryptDecrypt(encryptedText, key, EncryptMode.DECRYPT, iv)
        return out.substring(16, out.length)
    }

    /**
     * this function generates random string for given length
     * @param length
     * Desired length
     * * @return
     */
    override fun generateRandomIV(length: Int): String {
        val ranGen = SecureRandom()
        val aesKey = ByteArray(16)
        ranGen.nextBytes(aesKey)
        val result = StringBuffer()
        for (b in aesKey) {
            result.append(String.format("%02x", b)) // convert to hex
        }
        return if (length > result.toString().length) {
            result.toString()
        } else {
            result.toString().substring(0, length)
        }
    }

    override fun encryptAES128Base64(plainText: String, key: String, iv: String): String {
        try {
            val ivSpec = IvParameterSpec(iv.toByteArray())
            val keySpec = SecretKeySpec(key.toByteArray(), "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
            val valueByte = plainText.toByteArray()
            val encrypted = cipher.doFinal(valueByte)

            return Base64.encodeToString(encrypted, Base64.NO_WRAP)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return ""
    }

    override fun decryptAES128Base64(encryptedText: String, key: String, iv: String): String {
        if (encryptedText.isNotEmpty()) {
            try {
                val ivSpec = IvParameterSpec(iv.toByteArray(charset("UTF-8")))
                val keySpec = SecretKeySpec(key.toByteArray(charset("UTF-8")), "AES")

                val cipher = Cipher.getInstance("AES/CBC/NoPadding")
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

                val original = cipher.doFinal(Base64.decode(encryptedText, Base64.NO_WRAP))

                return String(original)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        return ""
    }

    override fun encryptAES256Base64(plainText: String, key: String, iv: String): String {
        try {
            val ivSpec = IvParameterSpec(iv.toByteArray(charset("UTF-8")))
            val keySpec = SecretKeySpec(key.toByteArray(), "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
            val valueByte = plainText.toByteArray()
            val encrypted = cipher.doFinal(valueByte)

            return Base64.encodeToString(encrypted, Base64.DEFAULT)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return ""
    }

    override fun decryptAES256Base64(encryptedText: String, key: String, iv: String): String {
        if (encryptedText.isNotEmpty()) {
            try {
                val ivSpec = IvParameterSpec(iv.toByteArray(charset("UTF-8")))
                val keySpec = SecretKeySpec(key.toByteArray(charset("UTF-8")), "AES")

                val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

                val original = cipher.doFinal(Base64.decode(encryptedText, Base64.DEFAULT))

                return String(original, Charset.forName("UTF-8"))
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        return ""
    }

    override fun decryptToSHA128(encryptedText: String, key: String, iv: String): String {
        ivSpec = IvParameterSpec(iv.toByteArray())
        keySpec = SecretKeySpec(key.toByteArray(), "AES")
        var decrypted = ""
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            decrypted =
                decrypt(encryptedText)?.let { String(it, Charset.forName("UTF-8")) }.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        }
        return decrypted
    }

    override fun encryptBase64(plainText: String): String {
        try {
            return Base64.encodeToString(plainText.toByteArray(), Base64.DEFAULT)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return ""
    }

    override fun decryptBase64(encryptedText: String): String {
        if (encryptedText.isNotEmpty()) {
            try {
                return String(Base64.decode(encryptedText, Base64.DEFAULT), StandardCharsets.UTF_8)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        return ""
    }

    private fun decrypt(code: String): ByteArray? {
        var decrypted: ByteArray? = null
        try {
            cipher?.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
            decrypted = cipher?.doFinal(Hex.decodeHex(code.toCharArray()))
        } catch (e: Exception) {
            // DO NOTHING
        }
        return decrypted
    }
}
