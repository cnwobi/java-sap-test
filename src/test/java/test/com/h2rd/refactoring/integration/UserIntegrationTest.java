package test.com.h2rd.refactoring.integration;

import com.h2rd.refactoring.usermanagement.dao.UserDao;
import com.h2rd.refactoring.usermanagement.dao.UserDaoImpl;
import com.h2rd.refactoring.usermanagement.domain.User;
import com.h2rd.refactoring.usermanagement.exception.EmailException;
import com.h2rd.refactoring.usermanagement.exception.ResponseMessage;
import com.h2rd.refactoring.usermanagement.exception.RoleException;
import com.h2rd.refactoring.usermanagement.exception.UserNotFoundException;
import com.h2rd.refactoring.usermanagement.web.UserResource;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class UserIntegrationTest {
    private UserResource userResource;
    private User integration;
    private static final String NAME = "integration";
    private static final String EMAIL = "initial@integration.com";
    private static final String ROLES = "admin,banker";
    private String secondEmail;
    private String secondName;
    private String secondRole;
    private ResponseMessage responseMessage;
 private  Response response;
 private UserDao userDao;
    @Before
    public void setUp(){
     userDao = new UserDaoImpl();
     userResource = new UserResource(userDao);

    }
	
	@Test
	public void createUserWithUniqueAndValidEmailAndRoleTest() throws Exception {

        response = userResource.addUser(NAME, EMAIL,ROLES );

        assertThat(response.getStatus()).isEqualTo(201);

	}
	@Test
   public void createUserWithNonUniqueAndValidEmailAndRoleTest() throws Exception {

      response =  userResource.addUser(NAME, EMAIL,ROLES );
        assertThat(response.getStatus()).isEqualTo(201);

        response =  userResource.addUser(NAME, EMAIL,ROLES );
        assertThat(response.getStatus()).isEqualTo(400);

        responseMessage = (ResponseMessage) response.getEntity();
        assertThat(responseMessage.getMessage()).isEqualTo("Email provided already exists on record");

    }
    @Test
    public void createUserWithoutAValidEmailAndWithValidRoleTest() throws Exception {

        Response response = userResource.addUser(NAME, null,ROLES );
        ResponseMessage responseMessage = (ResponseMessage) response.getEntity();

        assertThat(response.getStatus()).isEqualTo(400);


        assertThat((responseMessage.getMessage())).isEqualTo("A valid email is required to add user");
    }
    @Test
    public void createUserWithAValidEmailAndWithoutAValidRoleTest() throws Exception {

        Response response = userResource.addUser(NAME, EMAIL,"" );
       ResponseMessage responseMessage = (ResponseMessage) response.getEntity();

        assertThat(response.getStatus()).isEqualTo(400);


        assertThat((responseMessage.getMessage())).isEqualTo("A user must have at least one role");
    }
	@Test
	public void updateExistingUserWithAValidEmailAndValidRoleTest() throws Exception {

        userResource.addUser(NAME,EMAIL,ROLES);
        String newRoles = "manager,developer";


        Response response = userResource.updateUser(NAME,EMAIL,newRoles);
      assertThat(response.getStatus()).isEqualTo(200);

      User updatedUser = (User) response.getEntity();

      assertThat(updatedUser.getName()).isEqualTo(NAME);
      assertThat(updatedUser.getEmail()).isEqualTo(EMAIL);
      assertThat(updatedUser.getRoles()).contains("manager");
      assertThat(updatedUser.getRoles()).contains("developer");


	}

	@Test
    public void updateExistingUserWithAValidEmailAndIgnoreNullRoleParameterTest() throws Exception {

        userResource.addUser(NAME,EMAIL,ROLES);
        String newRoles = "";
        Response response = userResource.updateUser(null,EMAIL,null);

        assertThat(response.getStatus()).isEqualTo(200);

        User user = (User) response.getEntity();

        assertThat(user.getRoles()).contains("admin");
        assertThat(user.getName()).isNull();

    }
@Test
    public void updateExistingUserWithAValidEmailAndIgnoreEmptyRoleParameterTest() throws Exception {
    userResource.addUser(NAME,EMAIL,ROLES);
    String newRoles = "";
    Response response = userResource.updateUser(null,EMAIL,newRoles);

    assertThat(response.getStatus()).isEqualTo(200);

    User user = (User) response.getEntity();

    assertThat(user.getRoles()).contains("admin");
    assertThat(user.getName()).isNull();

}
	@Test
    public void updateNonExistingUserWithAValidEmailTest() throws UserNotFoundException, RoleException, EmailException {

        Response response = userResource.updateUser(NAME,EMAIL,ROLES);

        assertThat(response.getStatus()).isEqualTo(404);

        ResponseMessage responseMessage = (ResponseMessage) response.getEntity();

        assertThat(responseMessage.getMessage()).isEqualTo("User with email address "+EMAIL+" does not exist on record");
    }
@Test
   public  void deleteWithInvalidEmailTest() throws UserNotFoundException {

        Response response = userResource.deleteUser(null);
        assertThat(response.getStatus()).isEqualTo(400);

        ResponseMessage responseMessage = (ResponseMessage) response.getEntity();
        assertThat(responseMessage.getMessage()).isEqualTo("Please provide a valid email address");
   }
   @Test
   public  void  deleteNonExistingUserWithAValidEmailTest() throws UserNotFoundException {

        Response response = userResource.deleteUser(EMAIL);
        assertThat(response.getStatus()).isEqualTo(404);
        ResponseMessage responseMessage = (ResponseMessage) response.getEntity();
        assertThat(responseMessage.getMessage()).isEqualTo("User with email address "+EMAIL+" does not exist and cannot be deleted");
   }
   @Test
   public void deleteExistingUserWithAValidEmailTest() throws Exception {

       Response response;
      response = userResource.addUser(NAME,EMAIL,ROLES);
       assertThat(response.getStatus()).isEqualTo(201);


        response = userResource.findUser(EMAIL);
        assertThat(response.getStatus()).isEqualTo(200);

        response = userResource.deleteUser(EMAIL);
        assertThat(response.getStatus()).isEqualTo(200);

        response = userResource.findUser(EMAIL);
        assertThat(response.getStatus()).isEqualTo(404);

   }
   @Test
  public void getUserNoUserInDataBaseTest() throws Exception {

        Response response = userResource.getUsers();

        assertThat(response.getStatus()).isEqualTo(200);
        ResponseMessage responseMessage = (ResponseMessage) response.getEntity();
        assertThat(responseMessage.getMessage()).isEqualTo("No users found in the database");

  }

  @Test
    public void getUsersWhenUsersExistTest() throws Exception {
        userResource.addUser(NAME,EMAIL,ROLES);

      secondEmail = "2@mail.com";
      secondName = "Second user";
      secondRole = "dev,manager";
      userResource.addUser(secondName, secondEmail, secondRole);
        Response response = userResource.getUsers();

      GenericEntity<List<User>> user = (GenericEntity<List<User>>) response.getEntity();
      List<User> users = user.getEntity();
      assertThat(users.size()).isEqualTo(2);
      assertThat(users).filteredOn(user1 -> user1.getEmail().equals(EMAIL)).isNotNull();
      assertThat(users).filteredOn(user1 -> user1.getEmail().equals(secondEmail)).isNotNull();




  }

  @Test
    public void searchForUserWithNullEmailParameterTest(){
       response = userResource.findUser(null);
        assertThat(response.getStatus()).isEqualTo(400);


        responseMessage = (ResponseMessage) response.getEntity();


        assertThat(responseMessage.getMessage()).isEqualTo("Please provide a valid email address");

  }
    @Test
    public void searchForUserWithEmptyEmailParameterTest(){
        response = userResource.findUser("");
        assertThat(response.getStatus()).isEqualTo(400);


        responseMessage = (ResponseMessage) response.getEntity();


        assertThat(responseMessage.getMessage()).isEqualTo("Please provide a valid email address");
    }

  @Test
  public void searchForNonExistingUserWithValidEmailTest(){
        response = userResource.findUser(EMAIL);
        assertThat(response.getStatus()).isEqualTo(404);
        responseMessage = (ResponseMessage) response.getEntity();

        assertThat(responseMessage.getMessage()).isEqualTo("User with email address "+EMAIL+ " does not exist on record");

  }
  @Test
public  void searchForExistingUserWithValidEmailTest() throws Exception {
        userResource.addUser(NAME,EMAIL,ROLES);
        response = userResource.findUser(EMAIL);
    assertThat(response.getStatus()).isEqualTo(200);
    User foundUser = (User) response.getEntity();

    assertThat(foundUser.getEmail()).isEqualTo(EMAIL);

}

}
