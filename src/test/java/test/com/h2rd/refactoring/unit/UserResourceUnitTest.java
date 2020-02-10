package test.com.h2rd.refactoring.unit;


import com.h2rd.refactoring.usermanagement.config.Context;
import com.h2rd.refactoring.usermanagement.dao.UserDao;
import com.h2rd.refactoring.usermanagement.dao.UserDaoImpl;
import com.h2rd.refactoring.usermanagement.domain.User;
import com.h2rd.refactoring.usermanagement.exception.EmailException;
import com.h2rd.refactoring.usermanagement.exception.ResponseMessage;
import com.h2rd.refactoring.usermanagement.exception.UserNotFoundException;
import com.h2rd.refactoring.usermanagement.web.UserResource;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import javax.ws.rs.core.GenericEntity;
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
    userResource = new UserResource(userDao);
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
        ResponseMessage responseMessage = (ResponseMessage) response.getEntity();
        String expectedMessage ="No users found in the database";
        assertThat(response.getStatus()).isEqualTo(200);
       assertThat(responseMessage.getMessage()).isEqualTo(expectedMessage);

    }

    @Test
    public void getUsersInDatabaseTest() throws Exception {

        List<User> mockUserList = new ArrayList<>(Collections.singletonList(testUser));
       doReturn(mockUserList).when(userDao).findAllUsers();

      Response response = userResource.getUsers();
        GenericEntity<List<User>> genericEntity = (GenericEntity<List<User>>) response.getEntity();
    List<User> responseList = genericEntity.getEntity();

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(responseList).contains(testUser);

  }

  @Test
  public void deleteUserNonExistingTest() throws UserNotFoundException, EmailException {




  }
}
