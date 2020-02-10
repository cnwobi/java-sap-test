/*
package test.com.h2rd.refactoring.unit;

import com.h2rd.refactoring.usermanagement.domain.User;
import com.h2rd.refactoring.usermanagement.UserDao1;
import com.h2rd.refactoring.usermanagement.web.UserResource;
import junit.framework.Assert;
import org.junit.Test;

import javax.ws.rs.core.Response;

public class UserResourceUnitTest {

    UserResource userResource;
    UserDao1 userDao1;

    @Test
    public void getUsersTest() {

        userResource = new UserResource();
        userDao1 = UserDao1.getUserDao1();

        User user = User.builder().build();
        user.setName("fake user");
        user.setEmail("fake@user.com");
        userDao1.saveUser(user);

        Response response = userResource.getUsers();
        Assert.assertEquals(200, response.getStatus());
    }
}
*/
