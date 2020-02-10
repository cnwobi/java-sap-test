package com.h2rd.refactoring.service;

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

public class UserDaoImplTest {
    private com.h2rd.refactoring.dao.UserDao userDao;
    private List<User> users;
    private User user;
    private UserDao userDao;

    @Before
    public void setUp(){
        userDao = userDao.getUserDao();
        users = userDao.getUsers();
        user = new User();
        user.setName("Fake Name");
        user.setEmail("fake@email.com");
        user.getRoles().add("admin");
        user.getRoles().add("masters");
        userDao = new UserDaoImpl(userDao);


    }
    @Test
    public void saveUserWithUniqueEmailAndAtLeastOneRoleTest() throws Exception {
        userDao.saveUser(user);
        assertThat(users.get(0)).isEqualToComparingFieldByField(user);
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
        assertThat(userDao.getUsers()).isEqualTo(userDao.getUsers());
    }

    @Test
    public void deleteExistingUserTest() throws Exception {
        saveUserWithUniqueEmailAndAtLeastOneRoleTest();
        assertThat(userDao.getUsers()).contains(user);
        User user1 = new User();
        user1.setEmail("secondfake@gmail.com");
        user1.setRoles(new HashSet<>(Arrays.asList("admin")));
        userDao.saveUser(user1);
        userDao.deleteUser(user);
        assertThat(userDao.getUsers()).doesNotContain(user);

    }
    @Test
    public void deleteNonExistingUserTest() throws Exception{
        saveUserWithUniqueEmailAndAtLeastOneRoleTest();
        User user1 = new User();
        user1.setEmail("secondfake@gmail.com");
        user1.setRoles(new HashSet<>(Arrays.asList("admin")));

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> userDao.deleteUser(user1))
                .withMessage(user1.toString()+" does not exist and cannot be deleted");
    }



    @Test
    public void updateUserWithNewEmailAndValidRoleTest() throws Exception {
        userDao.saveUser(user);
        String updateName = "Updated fake name";
        User toUpdate = new User();
        toUpdate.setEmail(user.getEmail());
        toUpdate.setRoles(user.getRoles());
        toUpdate.setName(updateName);
        userDao.updateUser(toUpdate);

        assertThat(userDao.getUsers()).hasSize(1);
        assertThat(userDao.getUsers().get(0).getName()).isEqualTo(updateName);

    }
    @Test
    public void updateUserWithInvalidEmailAndValidRoleTest() throws Exception{
        userDao.saveUser(user);
        String updateName = "Updated fake name";
        User toUpdate = new User();
        toUpdate.setEmail("");
        toUpdate.setRoles(user.getRoles());
        toUpdate.setName(updateName);

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> userDao.updateUser(toUpdate))
                .withMessage("Please provide a valid email address");

    }



}