package com.h2rd.refactoring.usermanagement.dao;

import com.h2rd.refactoring.usermanagement.domain.User;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserDao1ImplUnitTest {

    private UserDao userDao;
    private User user;

    @Before
    public void setUp(){
        user = new User();
        user.setName("Fake Name");
        user.setEmail("fake@email.com");
        user.getRoles().add("admin");
        user.getRoles().add("masters");
        userDao = UserDaoImpl.getUserDao();
    }

    @Test
    public void saveUserTest() throws Exception {
        userDao.saveUser(user);
        assertThat(userDao.getUsers()).isNotEmpty();
    }

    @Test
    public void deleteUserTest() throws Exception {
        userDao.getUsers().clear();
        saveUserTest();
        userDao.deleteUser(user);
        assertThat(userDao.getUsers()).isEmpty();

    }
}