package test.com.h2rd.refactoring.integration;

import com.h2rd.refactoring.usermanagement.dao.UserDao;
import com.h2rd.refactoring.usermanagement.dao.UserDaoImpl;
import com.h2rd.refactoring.usermanagement.domain.User;
import com.h2rd.refactoring.usermanagement.exception.email.EmailEmptyOrNullException;
import com.h2rd.refactoring.usermanagement.exception.ResponseBody;
import com.h2rd.refactoring.usermanagement.exception.RoleException;
import com.h2rd.refactoring.usermanagement.exception.user.UserNotFoundException;
import com.h2rd.refactoring.usermanagement.web.UserResource;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class UserIntegrationTest {
    private static final String EXPECTED_EMPTY_OR_NULL_EMAIL_EXCEPTION_MESSAGE = "Email address must not be empty";
    private static final String EXPECTED_INVALID_EMAIL_FORMAT_EXCEPTION_MESSAGE = "Email provided is not of the expected format";
    private static final String NAME = "integration";
    private static final String VALID_INTEGERATION_EMAIL = "initial@integration.com";
    private static final String INVALID_INTEGRATION_EMAIL = "initial";
    private static final String ROLES = "admin,banker";
    private static final String EXPECTED_NULL_OR_EMPTY_ROLE_EXCEPTION_MESSAGE = "A user must have at least one role";
    private static final String NO_USERS_FOUND_IN_THE_DATABASE = "No users found in the database";

    private UserResource userResource;
    private String secondEmail;
    private String secondName;
    private String secondRole;
    private ResponseBody responseBody;
 private  Response response;
 private UserDao userDao;
    @Before
    public void setUp(){
     userDao = new UserDaoImpl();
     userResource = new UserResource(userDao);

    }
	
	@Test
	public void createUserWithUniqueAndValidEmailAndRoleTest() throws Exception {

        response = userResource.addUser(NAME, VALID_INTEGERATION_EMAIL,ROLES );

        assertThat(response.getStatus()).isEqualTo(201);

	}
	@Test
    public void createUserWithWronglyFormattedEmailAndValidRoleTest() throws Exception {
        response = userResource.addUser(NAME,INVALID_INTEGRATION_EMAIL,ROLES);
        assertThat(response.getStatus()).isEqualTo(400);
        ResponseBody responseBody = (ResponseBody) response.getEntity();
        assertThat(responseBody.getMessage()).isEqualTo(EXPECTED_INVALID_EMAIL_FORMAT_EXCEPTION_MESSAGE);
    }
	@Test
   public void createUserWithNonUniqueAndValidEmailAndRoleTest() throws Exception {

      response =  userResource.addUser(NAME, VALID_INTEGERATION_EMAIL,ROLES );
        assertThat(response.getStatus()).isEqualTo(201);

        response =  userResource.addUser(NAME, VALID_INTEGERATION_EMAIL,ROLES );
        assertThat(response.getStatus()).isEqualTo(400);

        responseBody = (ResponseBody) response.getEntity();
        assertThat(responseBody.getMessage()).isEqualTo("A user with this email provided already exists on record");

    }
    @Test
    public void createUserWithoutANullEmailAndWithValidRoleTest() throws Exception {

        Response response = userResource.addUser(NAME, null,ROLES );
        ResponseBody responseBody = (ResponseBody) response.getEntity();

        assertThat(response.getStatus()).isEqualTo(400);


        assertThat((responseBody.getMessage())).isEqualTo(EXPECTED_EMPTY_OR_NULL_EMAIL_EXCEPTION_MESSAGE);

    }

    @Test
    public void createUserWithEmptyEmailAndWithValidRoleTest() throws Exception {

        Response response = userResource.addUser(NAME, "",ROLES );
        ResponseBody responseBody = (ResponseBody) response.getEntity();

        assertThat(response.getStatus()).isEqualTo(400);


        assertThat((responseBody.getMessage())).isEqualTo(EXPECTED_EMPTY_OR_NULL_EMAIL_EXCEPTION_MESSAGE);
    }
    @Test
    public void createUserWithAValidEmailAndWithoutAValidRoleTest() throws Exception {

        Response response = userResource.addUser(NAME, VALID_INTEGERATION_EMAIL,"" );
       ResponseBody responseBody = (ResponseBody) response.getEntity();

        assertThat(response.getStatus()).isEqualTo(400);


        assertThat((responseBody.getMessage())).isEqualTo(EXPECTED_NULL_OR_EMPTY_ROLE_EXCEPTION_MESSAGE);
    }
	@Test
	public void updateExistingUserWithAValidEmailAndValidRoleTest() throws Exception {

        userResource.addUser(NAME, VALID_INTEGERATION_EMAIL,ROLES);
        String newRoles = "manager,developer";


        Response response = userResource.updateUser(NAME, VALID_INTEGERATION_EMAIL,newRoles);
      assertThat(response.getStatus()).isEqualTo(200);

      User updatedUser = (User) response.getEntity();

      assertThat(updatedUser.getName()).isEqualTo(NAME);
      assertThat(updatedUser.getEmail()).isEqualTo(VALID_INTEGERATION_EMAIL);
      assertThat(updatedUser.getRoles()).contains("manager");
      assertThat(updatedUser.getRoles()).contains("developer");


	}

	@Test
    public void updateExistingUserWithAValidEmailAndIgnoreNullRoleParameterTest() throws Exception {

        userResource.addUser(NAME, VALID_INTEGERATION_EMAIL,ROLES);
        String newRoles = "";
        Response response = userResource.updateUser(null, VALID_INTEGERATION_EMAIL,null);

        assertThat(response.getStatus()).isEqualTo(200);

        User user = (User) response.getEntity();

        assertThat(user.getRoles()).contains("admin");
        assertThat(user.getName()).isNull();

    }
@Test
    public void updateExistingUserWithAValidEmailAndIgnoreEmptyRoleParameterTest() throws Exception {
    userResource.addUser(NAME, VALID_INTEGERATION_EMAIL,ROLES);
    String newRoles = "";
    Response response = userResource.updateUser(null, VALID_INTEGERATION_EMAIL,newRoles);

    assertThat(response.getStatus()).isEqualTo(200);

    User user = (User) response.getEntity();

    assertThat(user.getRoles()).contains("admin");
    assertThat(user.getName()).isNull();

}
	@Test
    public void updateNonExistingUserWithAValidEmailTest() throws UserNotFoundException, RoleException, EmailEmptyOrNullException {

        Response response = userResource.updateUser(NAME, VALID_INTEGERATION_EMAIL,ROLES);

        assertThat(response.getStatus()).isEqualTo(404);

        ResponseBody responseBody = (ResponseBody) response.getEntity();

        assertThat(responseBody.getMessage()).isEqualTo("User with email address "+ VALID_INTEGERATION_EMAIL +" does not exist on record");
    }
@Test
   public  void deleteWithNullEmailTest() throws UserNotFoundException {

        Response response = userResource.deleteUser(null);
        assertThat(response.getStatus()).isEqualTo(400);

        ResponseBody responseBody = (ResponseBody) response.getEntity();
        assertThat(responseBody.getMessage()).isEqualTo(EXPECTED_EMPTY_OR_NULL_EMAIL_EXCEPTION_MESSAGE);
   }
   @Test
   public  void  deleteNonExistingUserWithAValidEmailTest() throws UserNotFoundException {
// no save operation yet
        Response response = userResource.deleteUser(VALID_INTEGERATION_EMAIL);
        assertThat(response.getStatus()).isEqualTo(404);
        ResponseBody responseBody = (ResponseBody) response.getEntity();
        assertThat(responseBody.getMessage()).isEqualTo("User with email address "+ VALID_INTEGERATION_EMAIL +" does not exist and cannot be deleted");
   }
   @Test
   public void deleteExistingUserWithAValidEmailTest() throws Exception {


      response = userResource.addUser(NAME, VALID_INTEGERATION_EMAIL,ROLES);
       assertThat(response.getStatus()).isEqualTo(201);


        response = userResource.findUser(VALID_INTEGERATION_EMAIL);
        assertThat(response.getStatus()).isEqualTo(200);

        response = userResource.deleteUser(VALID_INTEGERATION_EMAIL);
        assertThat(response.getStatus()).isEqualTo(200);

        response = userResource.findUser(VALID_INTEGERATION_EMAIL);
        assertThat(response.getStatus()).isEqualTo(404);

   }
   @Test
  public void getUserNoUserInDataBaseTest() throws Exception {

         response = userResource.getUsers();

        assertThat(response.getStatus()).isEqualTo(200);
        ResponseBody responseBody = (ResponseBody) response.getEntity();
        assertThat(responseBody.getMessage()).isEqualTo(NO_USERS_FOUND_IN_THE_DATABASE);

  }

  @Test
    public void getUsersWhenUsersExistTest() throws Exception {
        userResource.addUser(NAME, VALID_INTEGERATION_EMAIL,ROLES);

      secondEmail = "2@mail.com";
      secondName = "Second user";
      secondRole = "dev,manager";
      userResource.addUser(secondName, secondEmail, secondRole);
        Response response = userResource.getUsers();

      GenericEntity<List<User>> user = (GenericEntity<List<User>>) response.getEntity();
      List<User> users = user.getEntity();
      assertThat(users.size()).isEqualTo(2);
      assertThat(users).filteredOn(user1 -> user1.getEmail().equals(VALID_INTEGERATION_EMAIL)).isNotNull();
      assertThat(users).filteredOn(user1 -> user1.getEmail().equals(secondEmail)).isNotNull();




  }

  @Test
    public void searchForUserWithNullEmailParameterTest(){
       response = userResource.findUser(null);
        assertThat(response.getStatus()).isEqualTo(400);


        responseBody = (ResponseBody) response.getEntity();


        assertThat(responseBody.getMessage()).isEqualTo(EXPECTED_EMPTY_OR_NULL_EMAIL_EXCEPTION_MESSAGE);

  }
    @Test
    public void searchForUserWithEmptyEmailParameterTest(){
        response = userResource.findUser("");
        assertThat(response.getStatus()).isEqualTo(400);


        responseBody = (ResponseBody) response.getEntity();


        assertThat(responseBody.getMessage()).isEqualTo(EXPECTED_EMPTY_OR_NULL_EMAIL_EXCEPTION_MESSAGE);
    }

  @Test
  public void searchForNonExistingUserWithValidEmailTest(){
        response = userResource.findUser(VALID_INTEGERATION_EMAIL);
        assertThat(response.getStatus()).isEqualTo(404);
        responseBody = (ResponseBody) response.getEntity();

        assertThat(responseBody.getMessage()).isEqualTo("User with email address "+ VALID_INTEGERATION_EMAIL + " does not exist on record");

  }
  @Test
public  void searchForExistingUserWithValidEmailTest() throws Exception {
        userResource.addUser(NAME, VALID_INTEGERATION_EMAIL,ROLES);
        response = userResource.findUser(VALID_INTEGERATION_EMAIL);
    assertThat(response.getStatus()).isEqualTo(200);
    User foundUser = (User) response.getEntity();

    assertThat(foundUser.getEmail()).isEqualTo(VALID_INTEGERATION_EMAIL);

}

    @Test
    public  void searchForExistingUserWithInvalidFormatEmailTest() throws Exception {
        response = userResource.findUser(INVALID_INTEGRATION_EMAIL);
        assertThat(response.getStatus()).isEqualTo(400);
         responseBody = (ResponseBody) response.getEntity();
         assertThat(responseBody.getMessage()).isEqualTo(EXPECTED_INVALID_EMAIL_FORMAT_EXCEPTION_MESSAGE);
    }


}
