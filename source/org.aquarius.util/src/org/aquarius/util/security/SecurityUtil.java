/**
 *
 */
package org.aquarius.util.security;

import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * encrypt and decrypt function provider.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class SecurityUtil {

	/**
	 *
	 */
	private SecurityUtil() {
		// No instances needed
	}

	/**
	 * Decrypt by AES.<BR>
	 *
	 * @param content
	 * @param key
	 * @param iv
	 * @return
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidAlgorithmParameterException
	 */
	public static byte[] decryptByAES(byte[] content, byte[] key, byte[] iv) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
		Cipher cipher = createAESCipher(key, iv, Cipher.DECRYPT_MODE);

		byte[] bjiemihou = cipher.doFinal(content);
		return bjiemihou;
	}

	/**
	 * Decrypt by AES.<BR>
	 *
	 * @param inputStream
	 * @param key
	 * @param iv
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidAlgorithmParameterException
	 */
	public static InputStream decryptByAES(InputStream inputStream, byte[] key, byte[] iv)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
		Cipher cipher = createAESCipher(key, iv, Cipher.DECRYPT_MODE);

		return new CipherInputStream(inputStream, cipher);
	}

	/**
	 * Encrypt by AES.<BR>
	 *
	 * @param content
	 * @param key
	 * @param iv
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static byte[] encryptByAES(byte[] content, byte[] key, byte[] iv) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = createAESCipher(key, iv, Cipher.ENCRYPT_MODE);

		byte[] bjiemihou = cipher.doFinal(content);
		return bjiemihou;
	}

	/**
	 * Encrypt by AES.<BR>
	 *
	 * @param inputStream
	 * @param key
	 * @param iv
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidAlgorithmParameterException
	 */
	public static InputStream encryptByAES(InputStream inputStream, byte[] key, byte[] iv)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
		Cipher cipher = createAESCipher(key, iv, Cipher.ENCRYPT_MODE);

		return new CipherInputStream(inputStream, cipher);
	}

	/**
	 * create a aes cipher to encrypt and decrypt data.<BR>
	 *
	 * @param key
	 * @param iv
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 */
	private static Cipher createAESCipher(byte[] key, byte[] iv, int mode)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
		return createCipher(key, iv, mode, "AES");
	}

	/**
	 * create a cipher to encrypt and decrypt data.<BR>
	 *
	 * @param key
	 * @param iv
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 */
	private static Cipher createCipher(byte[] key, byte[] iv, int mode, String aligorithm)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
		Cipher cipher;
		cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Key cipherKey = new SecretKeySpec(key, aligorithm);
		AlgorithmParameterSpec cipherIV = new IvParameterSpec(iv);
		cipher.init(mode, cipherKey, cipherIV);
		return cipher;
	}

}
