package test.com.h2rd.refactoring.integration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import javax.ws.rs.core.Response;

import com.h2rd.refactoring.exception.EmailException;
import com.h2rd.refactoring.exception.RoleException;
import com.h2rd.refactoring.exception.UserNotFoundException;
import junit.framework.Assert;

import org.junit.Test;

import com.h2rd.refactoring.usermanagement.User;
import com.h2rd.refactoring.web.UserResource;

public class UserIntegrationTest {
	
	@Test
	public void createUserTest() throws Exception {
		UserResource userResource = new UserResource();
		
		User integration = new User();
        integration.setName("integration");
        integration.setEmail("initial@integration.com");
        integration.setRoles(Collections.synchronizedSet(new HashSet<>()));
        
        Response response = userResource.addUser(integration.getName(), integration.getEmail(), integration.getRoles());
        Assert.assertEquals(200, response.getStatus());
	}

	@Test
	public void updateUserTest() throws Exception {
		UserResource userResource = new UserResource();
		
		createUserTest();
        
        User updated = new User();
        updated.setName("integration");
        updated.setEmail("updated@integration.com");
        updated.setRoles(Collections.synchronizedSet(new HashSet<>()));
        
        Response response = userResource.updateUser(updated.getName(), updated.getEmail(), updated.getRoles());
        Assert.assertEquals(200, response.getStatus());
	}
}
