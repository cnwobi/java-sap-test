package com.h2rd.refactoring.service;

import com.h2rd.refactoring.usermanagement.service.UserService;
import com.h2rd.refactoring.usermanagement.service.UserServiceImpl;
import com.h2rd.refactoring.usermanagement.exception.EmailException;
import com.h2rd.refactoring.usermanagement.exception.RoleException;
import com.h2rd.refactoring.usermanagement.exception.UserNotFoundException;
import com.h2rd.refactoring.usermanagement.domain.User;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class UserService1ImplTest {

    private List<User> users;
    private User user;
    private UserService userService;

    @Before
    public void setUp(){
        userService = new UserServiceImpl();
        users = userService.getUsers();
        user =new User();
        user.setName("Fake Name");
        user.setEmail("fake@email.com");
        user.getRoles().add("admin");
        user.getRoles().add("masters");



    }
    @Test
    public void saveUserWithUniqueEmailAndAtLeastOneRoleTest() throws Exception {
        userService.getUsers().clear();
        userService.saveUser(user);
        assertThat(users.get(0)).isEqualToComparingFieldByField(user);
    }
    @Test
    public void saveUserWithoutEmailAndAtLeastOneRoleTest() throws Exception{
        userService.getUsers().clear();
        user.setEmail("");
        assertThatExceptionOfType(EmailException.class)
                .isThrownBy(()->{
                    userService.saveUser(user);
                })
                .withMessage("A valid email is required to add user");

    }
    @Test
    public void saveUserWithNonUniqueEmailAndAtLeastOneRoleTest() throws Exception{
        userService.getUsers().clear();
        userService.saveUser(user);
        User user1 = new User();
        user1.setEmail(user.getEmail());
        user1.setRoles(user.getRoles());


        assertThatExceptionOfType(EmailException.class)
                .isThrownBy(() ->{
                    userService.saveUser(user1);
                })
                .withMessage("Email provided already exists on record");


    }

    @Test
    public  void saveUserWithUniqueEmailAndNoRoleTest() throws Exception{
        userService.getUsers().clear();
        user.getRoles().clear();
        assertThatExceptionOfType(RoleException.class)
                .isThrownBy(() ->{
                    userService.saveUser(user);
                }).withMessage("User must have at least one role");
    }

    @Test
    public void getUsersTest() throws Exception {
        userService.getUsers().clear();
        userService.saveUser(user);
        assertThat(userService.getUsers()).contains(user);
    }

    @Test
    public void deleteExistingUserTest() throws Exception {
        userService.getUsers().clear();
        saveUserWithUniqueEmailAndAtLeastOneRoleTest();
        assertThat(userService.getUsers()).contains(user);
        User user1 = new User();
        user1.setEmail("secondfake@gmail.com");
        user1.setRoles(new HashSet<>(Arrays.asList("admin")));
        userService.saveUser(user1);
        userService.deleteUser(user);
        assertThat(userService.getUsers()).doesNotContain(user);

    }
    @Test
    public void deleteNonExistingUserTest() throws Exception {
        userService.getUsers().clear();
        saveUserWithUniqueEmailAndAtLeastOneRoleTest();
        User user1 = new User();
        user1.setEmail("secondfake@gmail.com");


        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> userService.deleteUser(user1))
                .withMessage("User with email address " +user1.getEmail()+" does not exist and cannot be deleted");
    }
@Test
public void deleteUserWithEmptyEmailParameterTest() throws Exception{
        userService.getUsers().clear();
        saveUserWithNonUniqueEmailAndAtLeastOneRoleTest();
    User user1 = new User();
    user1.setEmail("");
    assertThatExceptionOfType(EmailException.class)
            .isThrownBy(() -> userService.deleteUser(user1))
            .withMessage("Please provide a valid email address");

}
@Test
    public void deleteUserWithNullEmailParameterTest() throws Exception {
        userService.getUsers().clear();
    saveUserWithNonUniqueEmailAndAtLeastOneRoleTest();
    User user1 = new User();
    user1.setEmail(null);

    assertThatExceptionOfType(EmailException.class)
            .isThrownBy(() -> userService.deleteUser(user1))
            .withMessage("Please provide a valid email address");
    }

    @Test
    public void updateUserWithNewEmailAndValidRoleTest() throws Exception {
        userService.getUsers().clear();
        userService.saveUser(user);
        String updateName = "Updated fake name";
        User toUpdate =new User();
               toUpdate .setName(updateName);
               toUpdate .setRoles(user.getRoles());
                toUpdate.setEmail(user.getEmail());

        userService.updateUser(toUpdate);

        assertThat(userService.getUsers()).hasSize(1);
        assertThat(userService.getUsers().get(0).getName()).isEqualTo(updateName);

    }
    @Test
    public void updateUserWithInvalidEmailAndValidRoleTest() throws Exception{
        userService.getUsers().clear();
        userService.saveUser(user);
        String updateName = "Updated fake name";
        User toUpdate = new User();
              toUpdate  .setEmail("");
                toUpdate.setName(updateName);
               toUpdate .setRoles(user.getRoles());



        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> userService.updateUser(toUpdate))
                .withMessage("Please provide a valid email address");

    }



}