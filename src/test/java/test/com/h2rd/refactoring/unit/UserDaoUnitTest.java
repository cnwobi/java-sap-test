package test.com.h2rd.refactoring.unit;

import com.h2rd.refactoring.usermanagement.dao.UserDao;
import com.h2rd.refactoring.usermanagement.dao.UserDaoImpl;
import com.h2rd.refactoring.usermanagement.exception.email.EmailEmptyOrNullException;
import com.h2rd.refactoring.usermanagement.exception.RoleException;
import com.h2rd.refactoring.usermanagement.exception.email.EmailFormatException;
import com.h2rd.refactoring.usermanagement.exception.user.UserNotFoundException;
import com.h2rd.refactoring.usermanagement.domain.User;
import com.h2rd.refactoring.usermanagement.exception.user.UserNotUniqueException;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

public class UserDaoUnitTest {

    private static final String ROLE_EXCEPTION_MESSAGE = "A user must have at least one role";
    private static final String DEFAULT_TEST_USER_EMAIL = "fake@email.com";
    public static final String DEFAULT_TEST_USER_ROLE1 = "admin";
    private static final String DEFAULT_TEST_USER_ROLE2 = "masters";
    private static final String DEFAULT_TEST_USER_NAME = "Fake Name";
    private static final String FAKE_NAME2 = "Fake Name 2";
    private static final String VALID_EMAIL2 = "secondfake@gmail.com";
    private static final String EMPTY_OR_NULL_EMAIL_EXCEPTION_MESSAGE = "Email address must not be empty";
    private static final String INCORRECTLY_FORMATED_EMAIL = "cdawe";
    private static final String EXPECTED_INVALID_EMAIL_FORMAT_EXCEPTION_MESSAGE = "Email provided is not of the expected format";
    public static final String EXPECTED_NONE_UNIQUE_USER_EXCEPTION_MESSAGE = "A user with this email provided already exists on record";

    private Map<String, User> users;
    private User user;
    private UserDao userDao;

    @Before
    public void setUp() {
        userDao = new UserDaoImpl();
        users = userDao.getUsers();
        user = new User();
        user.setName(DEFAULT_TEST_USER_NAME);
        user.setEmail(DEFAULT_TEST_USER_EMAIL);
        user.getRoles().add(DEFAULT_TEST_USER_ROLE1);
        user.getRoles().add(DEFAULT_TEST_USER_ROLE2);


    }

    @Test
    public void saveUserWithAValidAndUniqueEmailAndAtLeastOneRoleTest() throws Exception {

        userDao.saveUser(user);
        User retrievedUser = users.get(user.getEmail());
        assertThat(retrievedUser).isEqualToComparingFieldByField(user);
    }
@Test
  public void   saveUserWithIncorrectlyFormattedEmailAndAtLeastOneRoleTest() throws Exception {
        user.setEmail(INCORRECTLY_FORMATED_EMAIL);

        assertThatExceptionOfType(EmailFormatException.class)
                .isThrownBy(() ->{
                    userDao.saveUser(user);
                }).withMessage(EXPECTED_INVALID_EMAIL_FORMAT_EXCEPTION_MESSAGE);



}

    @Test
    public void saveUserWithoutEmailAndAtLeastOneRoleTest() throws Exception {

        user.setEmail("");
        assertThatExceptionOfType(EmailEmptyOrNullException.class)
                .isThrownBy(() -> {
                    userDao.saveUser(user);
                })
                .withMessage(EMPTY_OR_NULL_EMAIL_EXCEPTION_MESSAGE);

    }

    @Test
    public void saveUserWithNonUniqueEmailAndAtLeastOneRoleTest() throws Exception {

        userDao.saveUser(user);
        User user1 = new User();
        user1.setEmail(user.getEmail());
        user1.setRoles(user.getRoles());


        assertThatExceptionOfType(UserNotUniqueException.class)
                .isThrownBy(() -> {
                    userDao.saveUser(user1);
                })
                .withMessage(EXPECTED_NONE_UNIQUE_USER_EXCEPTION_MESSAGE);


    }

    @Test
    public void saveUserWithUniqueEmailAndNoRoleTest() throws Exception {

        user.getRoles().clear();
        assertThatExceptionOfType(RoleException.class)
                .isThrownBy(() -> {
                    userDao.saveUser(user);
                }).withMessage(ROLE_EXCEPTION_MESSAGE);
    }

    @Test
    public void getUsersTest() throws Exception {

        userDao.saveUser(user);
        assertThat(userDao.getUsers()).containsValue(user);
    }

    @Test
    public void deleteExistingUserTest() throws Exception {
        userDao.getUsers().clear();
       userDao.saveUser(user);
        assertThat(userDao.getUsers()).containsValue(user);
        User user1 = new User();
        user1.setEmail(VALID_EMAIL2);
        user1.setRoles(new HashSet<>(Arrays.asList(DEFAULT_TEST_USER_ROLE1)));
        userDao.saveUser(user1);
        userDao.deleteUser(user);
        assertThat(userDao.getUsers()).doesNotContainValue(user);

    }

    @Test
    public void deleteNonExistingUserTest() throws Exception {
        userDao.saveUser(user);
        User user1 = new User();
        user1.setEmail(VALID_EMAIL2);


        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> userDao.deleteUser(user1))
                .withMessage("User with email address " + user1.getEmail() + " does not exist and cannot be deleted");
    }

    @Test
    public void deleteUserWithEmptyEmailParameterTest() throws Exception {

        User user1 = new User();
        user1.setEmail("");
        assertThatExceptionOfType(EmailEmptyOrNullException.class)
                .isThrownBy(() -> userDao.deleteUser(user1))
                .withMessage(EMPTY_OR_NULL_EMAIL_EXCEPTION_MESSAGE);

    }

    @Test
    public void deleteUserWithNullEmailParameterTest() throws Exception {

        User user1 = new User();
        user1.setEmail(null);

        assertThatExceptionOfType(EmailEmptyOrNullException.class)
                .isThrownBy(() -> userDao.deleteUser(user1))
                .withMessage(EMPTY_OR_NULL_EMAIL_EXCEPTION_MESSAGE);
    }
    @Test
    public void deleteUserWithInvalidFormatEmailParameterTest() throws UserNotFoundException, EmailEmptyOrNullException, EmailFormatException {
        User user = new User();
        user.setEmail(INCORRECTLY_FORMATED_EMAIL);

        assertThatExceptionOfType(EmailFormatException.class)
                .isThrownBy(() ->{
                    userDao.deleteUser(user);
                }).withMessage(EXPECTED_INVALID_EMAIL_FORMAT_EXCEPTION_MESSAGE);


    }

    @Test
    public void updateUserWithNewEmailAndValidRoleTest() throws Exception {
        userDao.getUsers().clear();
        userDao.saveUser(user);

        User toUpdate = new User();
        toUpdate.setName(FAKE_NAME2);
        toUpdate.setRoles(user.getRoles());
        toUpdate.setEmail(user.getEmail());

        userDao.updateUser(toUpdate);

        assertThat(userDao.getUsers()).hasSize(1);
        assertThat(userDao.getUsers().get(user.getEmail()).getName()).isEqualTo(FAKE_NAME2);

    }

    @Test
    public void updateUserWithInvalidEmailAndValidRoleTest() throws Exception {
        userDao.getUsers().clear();
        userDao.saveUser(user);
        String updateName = "Updated fake name";
        User toUpdate = new User();
        toUpdate.setEmail("");
        toUpdate.setName(updateName);
        toUpdate.setRoles(user.getRoles());


        assertThatExceptionOfType(EmailEmptyOrNullException.class)
                .isThrownBy(() -> userDao.updateUser(toUpdate))
                .withMessage(EMPTY_OR_NULL_EMAIL_EXCEPTION_MESSAGE);

    }


    @Test
    public void findNonExistingUserByEmailTest() throws Exception {
        userDao.saveUser(user);
        String searchEmail = "chuka@nwobi.com";
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    userDao.findUserByEmail(searchEmail);
                })
                .withMessage("User with email address " + searchEmail + " does not exist on record");
    }

    @Test
    public void findExistingUserByEmailTest() throws Exception {
        userDao.saveUser(user);
        String searchEmail = user.getEmail();
        User user1 = userDao.findUserByEmail(searchEmail);

        assertThat(user1).isEqualToComparingFieldByField(user);

    }

    @Test
    public void findNonExistingUserByNullEmailValueTest() throws Exception {


        assertThatExceptionOfType(EmailEmptyOrNullException.class)
                .isThrownBy(() -> {
                    userDao.findUserByEmail(null);
                })
                .withMessage(EMPTY_OR_NULL_EMAIL_EXCEPTION_MESSAGE);
    }

    @Test
    public void findNonExistingUserByEmptyEmailValueTest() {
        String searchEmail = "";
        assertThatExceptionOfType(EmailEmptyOrNullException.class)
                .isThrownBy(() -> {
                    userDao.findUserByEmail(searchEmail);
                })
                .withMessage(EMPTY_OR_NULL_EMAIL_EXCEPTION_MESSAGE);
    }

    @Test
    public void findUsers() {
    }

    @Test
    public void updateUserWithANullEmailAddressTest() throws Exception {
        userDao.saveUser(user);
        User user1 = new User();
        assertThatExceptionOfType(EmailEmptyOrNullException.class)
                .isThrownBy(() -> {
                    userDao.updateUser(user1);
                })
                .withMessage(EMPTY_OR_NULL_EMAIL_EXCEPTION_MESSAGE);
    }

    @Test
    public void updateExistingUserWithValidEmailAddressTest() throws Exception {
       userDao.saveUser(user);
        String nameToUpdate = "newest fake Name";
        String role = "sap dev";
        Set<String> roles = Collections.synchronizedSet(new HashSet<>(Arrays.asList(role)));
        User user1 = new User();
        user1.setEmail(user.getEmail());
        user1.setRoles(roles);
        user1.setName(nameToUpdate);
// assert that user is not already updated
        assertThat(user.getName()).isNotEqualTo(nameToUpdate);
        assertThat(user.getRoles()).doesNotContain(role);


        userDao.updateUser(user1);

        //update
        User updatedUser = userDao.getUsers().get(user.getEmail());

        //assert update occurred
        assertThat(updatedUser.getName()).isEqualTo(nameToUpdate);
        assertThat(updatedUser.getRoles()).contains(role);

    }
}