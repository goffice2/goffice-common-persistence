
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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import net.gvcc.goffice.multitenancy.crypt.AESManager;
import net.gvcc.goffice.multitenancy.entity.User;

/**
 *
 * <p>
 * The <code>AESManagerTest</code> class
 * </p>
 * <p>
 * Data: 4 mag 2022
 * </p>
 * 
 * @author <a href="mailto:edv@gvcc.net"></a>
 * @version 1.0
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableTransactionManagement
@PropertySources({ @PropertySource("classpath:application.properties"), @PropertySource("classpath:security.properties") })
@TestInstance(Lifecycle.PER_CLASS)
public class AESManagerTest {

	@Autowired
	private AESManager aesManager;

	// @Test
	public void testEncryptedHash()
			throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
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
		String sha256hex = aesManager.hash(hash1);
		String encyptedSha256hex = aesManager.encrypt(sha256hex);
		user.setId(2L);
		int hash2 = user.hashCode();
		String sha256hex2 = aesManager.hash(hash2);
		assertEquals(sha256hex2, aesManager.decrypt(encyptedSha256hex));
		user.setName("asfa");
		int hash3 = user.hashCode();
		String sha256hex3 = aesManager.hash(hash3);
		assertNotEquals(sha256hex3, aesManager.decrypt(encyptedSha256hex));
	}
}
