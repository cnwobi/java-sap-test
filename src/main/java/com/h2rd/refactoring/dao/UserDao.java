package com.h2rd.refactoring.dao;

import com.h2rd.refactoring.usermanagement.User;

import java.util.List;

public interface UserDao {
    void saveUser(User user) throws Exception;

    List<User> getUsers();

    void deleteUser(User userToDelete);
}
