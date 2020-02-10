package com.h2rd.refactoring.usermanagement.dao;

import com.h2rd.refactoring.usermanagement.domain.User;
import com.h2rd.refactoring.usermanagement.service.UserService;
import com.h2rd.refactoring.usermanagement.service.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserService1ImplUnitTest {

    private UserService userService;
    private User user;

    @Before
    public void setUp(){
        user = new User();
        user.setName("Fake Name");
        user.setEmail("fake@email.com");
        user.getRoles().add("admin");
        user.getRoles().add("masters");
        userService = new UserServiceImpl();
    }

    @Test
    public void saveUserTest() throws Exception {
        userService.getUsers().clear();
        userService.saveUser(user);
        assertThat(userService.getUsers()).isNotEmpty();
    }

    @Test
    public void deleteUserTest() throws Exception {
        userService.getUsers().clear();
        saveUserTest();
        userService.deleteUser(user);
        assertThat(userService.getUsers()).isEmpty();

    }
}