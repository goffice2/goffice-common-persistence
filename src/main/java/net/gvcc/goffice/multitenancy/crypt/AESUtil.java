/*
 * goffice... 
 * https://www.goffice.org
 * 
 * Copyright (c) 2005-2022 Consorzio dei Comuni della Provincia di Bolzano Soc. Coop. <https://www.gvcc.net>.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.gvcc.goffice.multitenancy.crypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * <p>
 * The <code>AESUtil</code> class
 * </p>
 * <p>
 * Data: 29 apr 2022
 * </p>
 * 
 * @author <a href="mailto:edv@gvcc.net"></a>
 * @version 1.0
 */
public class AESUtil {

	/**
	 * @param algorithm String rappresentante il tipo di algoritmo da ottenere tramite {@link javax.crypto.Cipher#getInstance(String)}
	 * @param input the String to be encrypted
	 * @param key SecretKey
	 * @param iv Initialization Vector
	 * @return String encrypted String
	 * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
	 * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
	 * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
	 * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
	 * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
	 * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
	 */
	public static String encrypt(String algorithm, String input, SecretKey key, IvParameterSpec iv)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		byte[] cipherText = cipher.doFinal(input.getBytes());
		return Base64.getEncoder().encodeToString(cipherText);
	}

	/**
	 * @param algorithm String rappresentante il tipo di algoritmo da ottenere tramite {@link javax.crypto.Cipher#getInstance(String)}
	 * @param cipherText String representing the encrypted text
	 * @param key SecretKey 
	 * @param iv Initialization vector
	 * @return String representing the pain text string
	 * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
	 * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
	 * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
	 * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
	 * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
	 * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
	 */
	public static String decrypt(String algorithm, String cipherText, SecretKey key, IvParameterSpec iv)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.DECRYPT_MODE, key, iv);
		byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
		return new String(plainText);
	}

	/**
	 * @param n represent the keysize
	 * @return SecretKey A secret (symmetric) key. 
	 * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
	 */
	public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(n);
		SecretKey key = keyGenerator.generateKey();
		return key;
	}

	/**
	 * @param password String used as password to encrypt text
	 * @param salt Salt for the encryption
	 * @return SecretKey A secret (symmetric) key.
	 * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
	 * @throws InvalidKeySpecException This is the exception for invalid key specifications.
	 */
	public static SecretKey getKeyFromPassword(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
		SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
		return secret;
	}

	/**
	 * @return IvParameterSpec the Initialization Vector object
	 */
	public static IvParameterSpec generateIv() {
		byte[] iv = new byte[16];
		new SecureRandom().nextBytes(iv);
		return new IvParameterSpec(iv);
	}

	/**
	 * @param ivStr String representation of the Initialization Vector
	 * @return IvParameterSpec the Initialization Vector object
	 */
	public static IvParameterSpec generateIvFromString(String ivStr) {
		return new IvParameterSpec(ivStr.getBytes());
	}

	/**
	 * @param algorithm String rappresentante il tipo di algoritmo da ottenere tramite {@link javax.crypto.Cipher#getInstance(String)}
	 * @param key A secret (symmetric) key. 
	 * @param iv Initialization Vector
	 * @param inputFile File to be crypted
	 * @param outputFile resulting encrypted file
	 * @throws IOException Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions produced by failed or interrupted I/O operations.
	 * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
	 * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
	 * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
	 * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
	 * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
	 * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
	 */
	public static void encryptFile(String algorithm, SecretKey key, IvParameterSpec iv, File inputFile, File outputFile)
			throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		FileInputStream inputStream = new FileInputStream(inputFile);
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		byte[] buffer = new byte[64];
		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			byte[] output = cipher.update(buffer, 0, bytesRead);
			if (output != null) {
				outputStream.write(output);
			}
		}
		byte[] outputBytes = cipher.doFinal();
		if (outputBytes != null) {
			outputStream.write(outputBytes);
		}
		inputStream.close();
		outputStream.close();
	}

	/**
	 * @param algorithm String rappresentante il tipo di algoritmo da ottenere tramite {@link javax.crypto.Cipher#getInstance(String)}
	 * @param key A secret (symmetric) key. 
	 * @param iv Initialization Vector
	 * @param encryptedFile the encrypted file
	 * @param decryptedFile the decrypted file
	 * @throws IOException Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions produced by failed or interrupted I/O operations.
	 * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
	 * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
	 * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
	 * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
	 * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
	 * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
	 */
	public static void decryptFile(String algorithm, SecretKey key, IvParameterSpec iv, File encryptedFile, File decryptedFile)
			throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.DECRYPT_MODE, key, iv);
		FileInputStream inputStream = new FileInputStream(encryptedFile);
		FileOutputStream outputStream = new FileOutputStream(decryptedFile);
		byte[] buffer = new byte[64];
		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			byte[] output = cipher.update(buffer, 0, bytesRead);
			if (output != null) {
				outputStream.write(output);
			}
		}
		byte[] output = cipher.doFinal();
		if (output != null) {
			outputStream.write(output);
		}
		inputStream.close();
		outputStream.close();
	}

	/**
	 * @param algorithm String rappresentante il tipo di algoritmo da ottenere tramite {@link javax.crypto.Cipher#getInstance(String)}
	 * @param object Serializable Object to be encrypted
	 * @param key A secret (symmetric) key. 
	 * @param iv Initialization Vector
	 * @return a sealed object containg the encrypted Object
	 * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
	 * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
	 * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
	 * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
	 * @throws IOException Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions produced by failed or interrupted I/O operations.
	 * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
	 */
	public static SealedObject encryptObject(String algorithm, Serializable object, SecretKey key, IvParameterSpec iv)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IOException, IllegalBlockSizeException {
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		SealedObject sealedObject = new SealedObject(object, cipher);
		return sealedObject;
	}

	/**
	 * @param algorithm String rappresentante il tipo di algoritmo da ottenere tramite {@link javax.crypto.Cipher#getInstance(String)}
	 * @param sealedObject a SealedObjectthat encapsulates the original object, in serializedformat (i.e., a "deep copy"), and seals (encrypts) its serialized contents,using a cryptographic algorithm such as AES, to protect itsconfidentiality.
	 * @param key A secret (symmetric) key. 
	 * @param iv Initialization Vector
	 * @return the object held into the {@link javax.crypto.SealedObject}
	 * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
	 * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
	 * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
	 * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
	 * @throws ClassNotFoundException Thrown when an application tries to load in a class through itsstring name
	 * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
	 * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
	 * @throws IOException Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions produced by failed or interrupted I/O operations.
	 */
	public static Serializable decryptObject(String algorithm, SealedObject sealedObject, SecretKey key, IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
			InvalidAlgorithmParameterException, InvalidKeyException, ClassNotFoundException, BadPaddingException, IllegalBlockSizeException, IOException {
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.DECRYPT_MODE, key, iv);
		Serializable unsealObject = (Serializable) sealedObject.getObject(cipher);
		return unsealObject;
	}

	/**
	 * @param plainText plain text String we want to encrypt
	 * @param key {@link javax.crypto.SecretKey} derived from password ad salt
	 * @param iv {@link javax.crypto.spec.IvParameterSpec} representing the initialization vector
	 * @return the encrypted String
	 * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
	 * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
	 * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
	 * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
	 * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
	 * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
	 */
	public static String encryptPasswordBased(String plainText, SecretKey key, IvParameterSpec iv)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()));
	}

	/**
	 * @param plainText plain text String we want to encrypt
	 * @param key {@link javax.crypto.SecretKey} derived from password ad salt
	 * @param iv String representing the initialization vector
	 * @return the encrypted String
	 * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
	 * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
	 * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
	 * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
	 * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
	 * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
	 */
	public static String encryptPasswordBased(String plainText, SecretKey key, String iv)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, generateIvFromString(iv));
		return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()));
	}

	/**
	 * @param text plain text String we want to encrypt
	 * @param password String used to encrypt the original text
	 * @param salt String used to encrypt the original text
	 * @param iv String representing the initialization vector
	 * @return the encrypted String
	 * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
	 * @throws InvalidKeySpecException This is the exception for invalid key specifications.
	 * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
	 * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
	 * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
	 * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
	 * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
	 */
	public static String encrypt(String text, String password, String salt, String iv)
			throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
		SecretKey key = AESUtil.getKeyFromPassword(password, salt);
		return encryptPasswordBased(text, key, iv);
	}

	/**
	 * @param text encrypted String
	 * @param password String used to encrypt the original text
	 * @param salt String used to encrypt the original text
	 * @param iv String representing the initialization vector
	 * @return the original String in plain text
	 * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
	 * @throws InvalidKeySpecException This is the exception for invalid key specifications.
	 * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
	 * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
	 * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
	 * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
	 * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
	 */
	public static String decrypt(String text, String password, String salt, String iv)
			throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
		SecretKey key = AESUtil.getKeyFromPassword(password, salt);
		return decryptPasswordBased(text, key, iv);
	}

	/**
	 * @param cipherText String representing the cipher
	 * @param key A secret (symmetric) key. 
	 * @param iv initialization vector
	 * @return String the decrypted String
	 * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
	 * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
	 * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
	 * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
	 * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
	 * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
	 */
	public static String decryptPasswordBased(String cipherText, SecretKey key, IvParameterSpec iv)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		cipher.init(Cipher.DECRYPT_MODE, key, iv);
		return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)));
	}

	/**
	 * @param cipherText String representing the cipher
	 * @param key A secret (symmetric) key. 
	 * @param iv initialization vector
	 * @return String the decrypted String
	 * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
	 * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
	 * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
	 * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
	 * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
	 * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
	 */
	public static String decryptPasswordBased(String cipherText, SecretKey key, String iv)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		cipher.init(Cipher.DECRYPT_MODE, key, generateIvFromString(iv));
		return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)));
	}
}
