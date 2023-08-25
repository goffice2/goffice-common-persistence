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
package net.gvcc.goffice.multitenancy.common;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * <p>
 * The <code>HashingManager</code> class
 * </p>
 * <p>
 * Data: 29 apr 2022
 * </p>
 * 
 * @author <a href="mailto:edv@gvcc.net"></a>
 * @version 1.0
 */
public interface HashingManager {
	
	
	/**
	 * This method will ecrypted a plain text <code>String</code>
	 * 
	 * @param text The text to be encrypted
	 * @return the ecrypted String
	 * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
	 * @throws InvalidKeySpecException This is the exception for invalid key specifications.
	 * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
	 * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
	 * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
	 * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
	 * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
	 */
	String encrypt(String text)
			throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException;

	/**
	 * This method will decrypt an ecrypted <code>String</code>
	 * 
	 * @param text The text to be decrypted
	 * @return String
	 * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
	 * @throws InvalidKeySpecException This is the exception for invalid key specifications.
	 * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
	 * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
	 * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
	 * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
	 * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
	 */
	String decrypt(String text)
			throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException;

	/**
	 * This method converts an <code>int</code> hashCode produced using for instance {@link Object#hashCode()} to a String 
	 * 
	 * @param hashCode an integer representation of a hash
	 * @return String The String counterpart for the hashCode
	 */
	String hash(int hashCode);
}
