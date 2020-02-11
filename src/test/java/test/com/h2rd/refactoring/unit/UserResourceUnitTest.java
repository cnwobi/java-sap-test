package test.com.h2rd.refactoring.unit;


import com.h2rd.refactoring.usermanagement.dao.UserDao;
import com.h2rd.refactoring.usermanagement.domain.User;
import com.h2rd.refactoring.usermanagement.exception.ResponseBody;
import com.h2rd.refactoring.usermanagement.web.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserResourceUnitTest {
  private UserResource userResource;
@Mock
  private UserDao userDao;
  private User testUser;
  @Before
  public void setUp(){
     /* userDao = (UserDao) Context.getContext().getBean("userDao");*/
      MockitoAnnotations.initMocks(this);
    userResource = new UserResource();
     // userResource = new UserResource();
      testUser = new User();
      testUser.setName("Chuka");
      testUser.setEmail("c.nwobi@gmail.com");
      testUser.setRoles(new HashSet<>(Collections.singletonList("dev")));

  }

    @Test
    public void getUsersNoUserInDatabaseTest() throws Exception {
      when(userDao.findAllUsers()).thenReturn(new ArrayList<>());

      userDao.getUsers().clear();
        Response response = userResource.getUsers();
        ResponseBody responseBody = (ResponseBody) response.getEntity();
        String expectedMessage ="No users found in the database";
        assertThat(response.getStatus()).isEqualTo(200);
       assertThat(responseBody.getMessage()).isEqualTo(expectedMessage);

    }

}
