package com.h2rd.refactoring.service;

import com.h2rd.refactoring.dao.UserDao;
import com.h2rd.refactoring.dao.UserDaoImpl;
import com.h2rd.refactoring.exception.EmailException;
import com.h2rd.refactoring.exception.RoleException;
import com.h2rd.refactoring.exception.UserNotFoundException;
import com.h2rd.refactoring.usermanagement.User;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.*;

public class UserServiceImplTest {
    private UserDao userDao;
    private List<User> users;
    private User user;
    private UserService userService;

    @Before
    public void setUp(){
        userDao = new UserDaoImpl();
        users = userDao.getUsers();
        user = new User();
        user.setName("Fake Name");
        user.setEmail("fake@email.com");
        user.getRoles().add("admin");
        user.getRoles().add("masters");
        userService = new UserServiceImpl(userDao);


    }
    @Test
    public void saveUserWithUniqueEmailAndAtLeastOneRoleTest() throws Exception {
        userService.saveUser(user);
        assertThat(users.get(0)).isEqualToComparingFieldByField(user);
    }
    @Test
    public void saveUserWithoutEmailAndAtLeastOneRoleTest() throws Exception{
        user.setEmail("");
        assertThatExceptionOfType(EmailException.class)
                .isThrownBy(()->{
                    userService.saveUser(user);
                })
                .withMessage("A valid email is required to add user");

    }
    @Test
    public void saveUserWithNonUniqueEmailAndAtLeastOneRoleTest() throws Exception{
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
        user.getRoles().clear();
        assertThatExceptionOfType(RoleException.class)
                .isThrownBy(() ->{
                    userService.saveUser(user);
                }).withMessage("User must have at least one role");
    }

    @Test
    public void getUsersTest() throws Exception {
        userService.saveUser(user);
        assertThat(userService.getUsers()).isEqualTo(userDao.getUsers());
    }

    @Test
    public void deleteExistingUserTest() throws Exception {
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
    public void deleteNonExistingUserTest() throws Exception{
        saveUserWithUniqueEmailAndAtLeastOneRoleTest();
        User user1 = new User();
        user1.setEmail("secondfake@gmail.com");
        user1.setRoles(new HashSet<>(Arrays.asList("admin")));

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> userService.deleteUser(user1))
                .withMessage(user1.toString()+" does not exist and cannot be deleted");
    }



    @Test
    public void updateUserWithNewEmailAndValidRoleTest() throws Exception {
        userService.saveUser(user);
        String updateName = "Updated fake name";
        User toUpdate = new User();
        toUpdate.setEmail(user.getEmail());
        toUpdate.setRoles(user.getRoles());
        toUpdate.setName(updateName);
        userService.updateUser(toUpdate);

        assertThat(userService.getUsers()).hasSize(1);
        assertThat(userService.getUsers().get(0).getName()).isEqualTo(updateName);

    }
    @Test
    public void updateUserWithInvalidEmailAndValidRoleTest() throws Exception{
        userService.saveUser(user);
        String updateName = "Updated fake name";
        User toUpdate = new User();
        toUpdate.setEmail("");
        toUpdate.setRoles(user.getRoles());
        toUpdate.setName(updateName);

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> userService.updateUser(toUpdate))
                .withMessage("Please provide a valid email address");

    }



}