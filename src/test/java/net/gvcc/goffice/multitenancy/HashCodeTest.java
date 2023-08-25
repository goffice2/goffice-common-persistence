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
package net.gvcc.goffice.multitenancy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;

import com.google.common.hash.Hashing;

import net.gvcc.goffice.multitenancy.entity.User;

/**
 *
 * <p>
 * The <code>HashCodeTest</code> class
 * </p>
 * <p>
 * Data: 4 mag 2022
 * </p>
 * 
 * @author <a href="mailto:edv@gvcc.net"></a>
 * @version 1.0
 */
public class HashCodeTest {

	@Test
	public void testHashBehaviour() {
		LocalDateTime now = LocalDateTime.now();
		//@formatter:off
		User user = User.builder()
				.id(1L)
				.name("Name")
				.dateCreated(now)
				.dateModified(now)
				.createdBy("test")
				.modifiedBy("test").build();
		//@formatter:on
		int hash1 = user.hashCode();
		//@formatter:off
		User user2 = User.builder()
				.id(1L)
				.name("Name")
				.dateCreated(now)
				.dateModified(now)
				.createdBy("test")
				.modifiedBy("test").build();
		//@formatter:on
		int hash2 = user2.hashCode();
		assertEquals(hash1, hash2);
		//@formatter:off
		User user3 = User.builder()
				.id(1L)
				.name("Nameeee")
				.dateCreated(now)
				.dateModified(now)
				.createdBy("test")
				.modifiedBy("test").build();
		//@formatter:on
		int hash3 = user3.hashCode();
		assertNotEquals(hash1, hash3);
	}

	@Test
	public void when_hashFieldIsSet_isIgnoredInHashCode() {
		LocalDateTime now = LocalDateTime.now();
		//@formatter:off
		User user = User.builder()
				.id(1L)
				.name("Name")
				.dateCreated(now)
				.dateModified(now)
				.createdBy("test")
				.modifiedBy("test").build();
		//@formatter:on
		int hash1 = user.hashCode();
		user.setHash("hashFieldSet");
		int hash2 = user.hashCode();
		assertEquals(hash1, hash2);
	}

	@Test
	public void testCryptoHash() {
		LocalDateTime now = LocalDateTime.now();
		//@formatter:off
		User user = User.builder()
				.id(1L)
				.name("Name")
				.dateCreated(now)
				.dateModified(now)
				.createdBy("test")
				.modifiedBy("test").build();
		//@formatter:on
		int hash1 = user.hashCode();
		String sha256hex = Hashing.sha256().hashString("" + hash1, StandardCharsets.UTF_8).toString();
		//@formatter:off
		User user2 = User.builder()
				.id(2L)
				.name("Name")
				.dateCreated(now)
				.dateModified(now)
				.createdBy("test")
				.modifiedBy("test").build();
		//@formatter:on
		int hash2 = user2.hashCode();
		String sha256hex2 = Hashing.sha256().hashString("" + hash1, StandardCharsets.UTF_8).toString();
		assertEquals(sha256hex, sha256hex2);

	}

	// @Test
	// public void testEncodedCryptoHash()
	// throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
	// LocalDateTime now = LocalDateTime.now();
//		//@formatter:off
//		User user = User.builder()
//				.id(1L)
//				.name("Name")
//				.dateCreated(now)
//				.dateModified(now)
//				.createdBy("test")
//				.modifiedBy("test").build();
//		//@formatter:on
	// int hash1 = user.hashCode();
	// String sha256hex = Hashing.sha256().hashString("" + hash1, StandardCharsets.UTF_8).toString();
	// String password = "herzum";
	// String salt = "12345678";
	// String iv = "1234567890123456";
	// String encyptedSha256hex = AESUtil.encrypt(sha256hex, password, salt, iv);
	// user.setId(2L);
	// int hash2 = user.hashCode();
	// String sha256hex2 = Hashing.sha256().hashString("" + hash2, StandardCharsets.UTF_8).toString();
	// assertEquals(sha256hex2, AESUtil.decrypt(encyptedSha256hex, password, salt, iv));
	// user.setName("asfa");
	// int hash3 = user.hashCode();
	// String sha256hex3 = Hashing.sha256().hashString("" + hash3, StandardCharsets.UTF_8).toString();
	// assertNotEquals(sha256hex3, AESUtil.decrypt(encyptedSha256hex, password, salt, iv));
	// }

	// @Test
	// void givenPassword_whenEncrypt_thenSuccess()
	// throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {
	//
	// String plainText = "www.herzum.com";
	// String password = "herzum";
	// String salt = "12345678";
	// String iv = "1234567890123456";
	// String cipherText = AESUtil.encrypt(plainText, password, salt, iv);
	// String decryptedCipherText = AESUtil.decrypt(cipherText, password, salt, iv);
	// assertEquals(plainText, decryptedCipherText);
	// }

	public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(n);
		SecretKey key = keyGenerator.generateKey();
		return key;
	}
}
