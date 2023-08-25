package net.gvcc.goffice.multitenancy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.gvcc.goffice.multitenancy.common.AbstractService;
import net.gvcc.goffice.multitenancy.common.GofficeBaseRepository;
import net.gvcc.goffice.multitenancy.common.HashingManager;
import net.gvcc.goffice.multitenancy.crypt.AESManager;
import net.gvcc.goffice.multitenancy.entity.User;
import net.gvcc.goffice.multitenancy.repository.UserRepository;

@Service
public class UserService extends AbstractService<User> {

	@Autowired
	private UserRepository repository;
	@Autowired
	private AESManager aesManager;

	@Override
	protected GofficeBaseRepository<User> getDao() {
		return repository;
	}

	@Override
	protected HashingManager getHashingManager() {
		return aesManager;
	}

}
