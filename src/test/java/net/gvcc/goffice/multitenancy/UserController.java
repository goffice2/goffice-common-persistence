package net.gvcc.goffice.multitenancy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.gvcc.goffice.multitenancy.common.CRUDController;
import net.gvcc.goffice.multitenancy.common.IOperations;
import net.gvcc.goffice.multitenancy.entity.User;

@RestController
@RequestMapping("/users")
public class UserController extends CRUDController<User> {

	@Autowired
	private UserService userService;

	@Override
	public IOperations<User> getService() {
		return userService;
	}

}
