package com.h2rd.refactoring.service;

import com.h2rd.refactoring.usermanagement.dao.UserDao;
import com.h2rd.refactoring.usermanagement.dao.UserDaoImpl;
import com.h2rd.refactoring.usermanagement.exception.EmailException;
import com.h2rd.refactoring.usermanagement.exception.RoleException;
import com.h2rd.refactoring.usermanagement.exception.UserNotFoundException;
import com.h2rd.refactoring.usermanagement.domain.User;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class UserDao1ImplTest {

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
        userDao.getUsers().clear();
        saveUserWithUniqueEmailAndAtLeastOneRoleTest();
        User user1 = new User();
        user1.setEmail("secondfake@gmail.com");


        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> userDao.deleteUser(user1))
                .withMessage("User with email address " +user1.getEmail()+" does not exist and cannot be deleted");
    }
@Test
public void deleteUserWithEmptyEmailParameterTest() throws Exception{
        userDao.getUsers().clear();
        saveUserWithNonUniqueEmailAndAtLeastOneRoleTest();
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



        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> userDao.updateUser(toUpdate))
                .withMessage("Please provide a valid email address");

    }



}