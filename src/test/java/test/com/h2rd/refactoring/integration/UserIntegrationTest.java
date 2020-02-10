package test.com.h2rd.refactoring.integration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import javax.ws.rs.core.Response;

import com.h2rd.refactoring.usermanagement.dao.UserDao;
import com.h2rd.refactoring.usermanagement.dao.UserDaoImpl;
import com.h2rd.refactoring.usermanagement.exception.EmailException;
import com.h2rd.refactoring.usermanagement.exception.RoleException;
import com.h2rd.refactoring.usermanagement.exception.UserNotFoundException;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.h2rd.refactoring.usermanagement.domain.User;
import com.h2rd.refactoring.usermanagement.web.UserResource;

public class UserIntegrationTest {
    private UserResource userResource;
    private UserDao userDao;
    @Before
    public void setUp(){

    }
	
	@Test
	public void createUserWithValidEmailAndRoleTest() throws Exception {
		UserResource userResource = new UserResource();
		
		User integration = new User();
        integration.setName("integration");
        integration.setEmail("initial@integration.com");
        String roles = "admin,banker";
        integration.setRoles(Collections.synchronizedSet(new HashSet<>()));
        
        Response response = userResource.addUser(integration.getName(), integration.getEmail(),roles );
        Assert.assertEquals(200, response.getStatus());
	}

	@Test
	public void updateUserTest() throws Exception {
        UserDao userDao = new UserDaoImpl();
		UserResource userResource = new UserResource();

		//createUserTest();
        
        User updated = new User();
        updated.setName("integration");
        updated.setEmail("updated@integration.com");
        updated.setRoles(Collections.synchronizedSet(new HashSet<>()));
        
        Response response = userResource.updateUser(updated.getName(), updated.getEmail(), "chuka,emeka");
        Assert.assertEquals(200, response.getStatus());
	}
}
