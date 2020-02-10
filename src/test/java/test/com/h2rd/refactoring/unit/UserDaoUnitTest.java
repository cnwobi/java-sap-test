package test.com.h2rd.refactoring.unit;

import com.h2rd.refactoring.usermanagement.dao.UserDao;
import com.h2rd.refactoring.usermanagement.dao.UserDaoImpl;
import com.h2rd.refactoring.usermanagement.exception.EmailException;
import com.h2rd.refactoring.usermanagement.exception.RoleException;
import com.h2rd.refactoring.usermanagement.exception.UserNotFoundException;
import com.h2rd.refactoring.usermanagement.domain.User;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

public class UserDaoUnitTest {

    private Map<String,User> users;
    private User user;
    private UserDao userDao;

    @Before
    public void setUp(){
        userDao = new UserDaoImpl();
        users = userDao.getUsers();
        user =new User();
        user.setName("Fake Name");
        user.setEmail("fake@email.com");
        user.getRoles().add("admin");
        user.getRoles().add("masters");



    }
    @Test
    public void saveUserWithUniqueEmailAndAtLeastOneRoleTest() throws Exception {

        userDao.saveUser(user);
        User retrievedUser = users.get(user.getEmail());
        assertThat(retrievedUser).isEqualToComparingFieldByField(user);
    }
    @Test
    public void saveUserWithoutEmailAndAtLeastOneRoleTest() throws Exception{

        user.setEmail("");
        assertThatExceptionOfType(EmailException.class)
                .isThrownBy(()->{
                    userDao.saveUser(user);
                })
                .withMessage("A valid email is required to add user");

    }
    @Test
    public void saveUserWithNonUniqueEmailAndAtLeastOneRoleTest() throws Exception{

        userDao.saveUser(user);
        User user1 = new User();
        user1.setEmail(user.getEmail());
        user1.setRoles(user.getRoles());


        assertThatExceptionOfType(EmailException.class)
                .isThrownBy(() ->{
                    userDao.saveUser(user1);
                })
                .withMessage("Email provided already exists on record");


    }

    @Test
    public  void saveUserWithUniqueEmailAndNoRoleTest() throws Exception{

        user.getRoles().clear();
        assertThatExceptionOfType(RoleException.class)
                .isThrownBy(() ->{
                    userDao.saveUser(user);
                }).withMessage("User must have at least one role");
    }

    @Test
    public void getUsersTest() throws Exception {

        userDao.saveUser(user);
        assertThat(userDao.getUsers()).containsValue(user);
    }

    @Test
    public void deleteExistingUserTest() throws Exception {
        userDao.getUsers().clear();
        saveUserWithUniqueEmailAndAtLeastOneRoleTest();
        assertThat(userDao.getUsers()).containsValue(user);
        User user1 = new User();
        user1.setEmail("secondfake@gmail.com");
        user1.setRoles(new HashSet<>(Arrays.asList("admin")));
        userDao.saveUser(user1);
        userDao.deleteUser(user);
        assertThat(userDao.getUsers()).doesNotContainValue(user);

    }
    @Test
    public void deleteNonExistingUserTest() throws Exception {
      userDao.saveUser(user);
        User user1 = new User();
        user1.setEmail("secondfake@gmail.com");


        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> userDao.deleteUser(user1))
                .withMessage("User with email address " +user1.getEmail()+" does not exist and cannot be deleted");
    }
@Test
public void deleteUserWithEmptyEmailParameterTest() throws Exception{

    User user1 = new User();
    user1.setEmail("");
    assertThatExceptionOfType(EmailException.class)
            .isThrownBy(() -> userDao.deleteUser(user1))
            .withMessage("Please provide a valid email address");

}
@Test
    public void deleteUserWithNullEmailParameterTest() throws Exception {
        userDao.getUsers().clear();
    saveUserWithNonUniqueEmailAndAtLeastOneRoleTest();
    User user1 = new User();
    user1.setEmail(null);

    assertThatExceptionOfType(EmailException.class)
            .isThrownBy(() -> userDao.deleteUser(user1))
            .withMessage("Please provide a valid email address");
    }

    @Test
    public void updateUserWithNewEmailAndValidRoleTest() throws Exception {
        userDao.getUsers().clear();
        userDao.saveUser(user);
        String updateName = "Updated fake name";
        User toUpdate =new User();
               toUpdate .setName(updateName);
               toUpdate .setRoles(user.getRoles());
                toUpdate.setEmail(user.getEmail());

        userDao.updateUser(toUpdate);

        assertThat(userDao.getUsers()).hasSize(1);
        assertThat(userDao.getUsers().get(user.getEmail()).getName()).isEqualTo(updateName);

    }
    @Test
    public void updateUserWithInvalidEmailAndValidRoleTest() throws Exception{
        userDao.getUsers().clear();
        userDao.saveUser(user);
        String updateName = "Updated fake name";
        User toUpdate = new User();
              toUpdate  .setEmail("");
                toUpdate.setName(updateName);
               toUpdate .setRoles(user.getRoles());



        assertThatExceptionOfType(EmailException.class)
                .isThrownBy(() -> userDao.updateUser(toUpdate))
                .withMessage("Please provide a valid email address");

    }


    @Test
    public void findNonExistingUserByEmailTest() throws Exception {
        userDao.saveUser(user);
        String searchEmail = "chuka@nwobi.com";
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() ->{
                    userDao.findUserByEmail(searchEmail);
                })
                .withMessage("User with email address "+ searchEmail+ " does not exist on record");
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

        String searchEmail = null;
        assertThatExceptionOfType(EmailException.class)
                .isThrownBy(() ->{
                    userDao.findUserByEmail(searchEmail);
                })
                .withMessage("Please provide a valid email address");
    }
    @Test
    public void findNonExistingUserByEmptyEmailValueTest(){
        String searchEmail = "";
        assertThatExceptionOfType(EmailException.class)
                .isThrownBy(() ->{
                    userDao.findUserByEmail(searchEmail);
                })
                .withMessage("Please provide a valid email address");
    }
    @Test
    public void findUsers() {
    }

    @Test
    public void updateUserWithoutAnInvalidEmailAddressTest() throws Exception {
        userDao.saveUser(user);
        User user1 = new User();
        assertThatExceptionOfType(EmailException.class)
                .isThrownBy(() ->{
                    userDao.updateUser(user1);
                })
                .withMessage("Please provide a valid email address");
    }

    @Test
    public void updateUserWithValidEmailAddressTest() throws Exception{
        saveUserWithUniqueEmailAndAtLeastOneRoleTest();
        String nameToUpdate = "newest fake Name";
        String role = "sap dev";
        Set<String> roles = Collections.synchronizedSet(new HashSet<>(Arrays.asList(role)));
        User user1 = new User();
        user1.setEmail(user.getEmail());
        user1.setRoles(roles);
        user1.setName(nameToUpdate);
// assert that user is not already update
        assertThat(user.getName()).isNotEqualTo(nameToUpdate);
        assertThat(user.getRoles()).doesNotContain(role);


        userDao.updateUser(user1);

        //update
        User updatedUser = userDao.getUsers().get(user.getEmail());

        //assert update occurred
        assertThat(updatedUser.getName()).isEqualTo(nameToUpdate);
        assertThat(updatedUser.getRoles()).contains(role);

    }

    public void updateUserWithValidEmailAddressAndNullName(){

    }
}