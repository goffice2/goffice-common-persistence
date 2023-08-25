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

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.hash.Hashing;

import lombok.Getter;
import lombok.Setter;
import net.gvcc.goffice.multitenancy.common.HashingManager;

/**
 *
 * <p>
 * The <code>AESManager</code> class
 * </p>
 * <p>
 * This is a concrete Implementation of the {@link net.gvcc.goffice.multitenancy.common.HashingManager} interface 
 * <p>
 * It delegates encryption and decryptio to the {@link net.gvcc.goffice.multitenancy.crypt.AESUtil} class 
 * while the hash calculation is performed using SHA256 through {@link com.google.common.hash.Hashing#sha256()} and then {@link com.google.common.hash.HashFunction#hashString(CharSequence, java.nio.charset.Charset)}
 * <p>
 * This class will also enable Auditing using the {@link org.hibernate.envers.Audited} annotation
*
 * Data: 29 apr 2022
 * </p>
 * @author <a href="mailto:edv@gvcc.net"></a>
 *
 * @version 1.0
 * 
* @see net.gvcc.goffice.multitenancy.common.HashingManager
 */
@Component
@Getter
@Setter
public class AESManager implements HashingManager {
	@Value("${enc.password}")
	private String password;

	@Value("${enc.salt}")
	private String salt;

	@Value("${enc.iv}")
	private String iv;

	// @Autowired
	// private PasswordEncoder passwordEncoder;

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.gvcc.goffice.multitenancy.common.HashingManager#encrypt(java.lang.String)
	 */
	public String encrypt(String text)
			throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
		SecretKey key = AESUtil.getKeyFromPassword(password, salt);
		return AESUtil.encryptPasswordBased(text, key, iv);
		// StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		// encryptor.setPassword(iv);
		// // textEncryptor.setPassword(encryptedPassword);
		// return encryptor.encrypt(text);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.gvcc.goffice.multitenancy.common.HashingManager#decrypt(java.lang.String)
	 */
	public String decrypt(String text)
			throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
		SecretKey key = AESUtil.getKeyFromPassword(password, salt);
		return AESUtil.decryptPasswordBased(text, key, iv);

		// StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
		// decryptor.setPassword(iv);
		// return decryptor.decrypt(text);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.gvcc.goffice.multitenancy.common.HashingManager#hash(int)
	 */
	public String hash(int hasCode) {
		return Hashing.sha256().hashString("" + hasCode, StandardCharsets.UTF_8).toString();
	}

}
