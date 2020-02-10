package test.com.h2rd.refactoring.unit;


import com.h2rd.refactoring.usermanagement.config.Context;
import com.h2rd.refactoring.usermanagement.dao.UserDao;
import com.h2rd.refactoring.usermanagement.dao.UserDaoImpl;
import com.h2rd.refactoring.usermanagement.domain.User;
import com.h2rd.refactoring.usermanagement.exception.ResponseMessage;
import com.h2rd.refactoring.usermanagement.web.UserResource;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class UserResourceUnitTest {
  private UserResource userResource;
  private UserDao userDao;
  private User testUser;
  @Before
  public void setUp(){
      userDao = (UserDao) Context.getContext().getBean("userDao");
      userResource = new UserResource(userDao);
      testUser = new User();
      testUser.setName("Chuka");
      testUser.setEmail("c.nwobi@gmail.com");
      testUser.setRoles(new HashSet<>(Arrays.asList("dev")));

  }

    @Test
    public void getUsersNoUserInDatabaseTest() throws Exception {
      userDao.getUsers().clear();
        Response response = userResource.getUsers();
        ResponseMessage responseMessage = (ResponseMessage) response.getEntity();
        String expectedMessage ="No users found in the database";
        assertThat(response.getStatus()).isEqualTo(200);
       assertThat(responseMessage.getMessage()).isEqualTo(expectedMessage);

    }

    @Test
    public void getUsersInDatabaseTest() throws Exception {
      userDao.saveUser(testUser);
      Response response = userResource.getUsers();
        GenericEntity<List<User>> genericEntity = (GenericEntity<List<User>>) response.getEntity();
    List<User> responseList = genericEntity.getEntity();

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(responseList).contains(testUser);

  }
}
