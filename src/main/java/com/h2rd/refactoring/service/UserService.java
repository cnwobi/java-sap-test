package com.h2rd.refactoring.service;

import com.h2rd.refactoring.exception.EmailException;
import com.h2rd.refactoring.exception.RoleException;
import com.h2rd.refactoring.exception.UserNotFoundException;
import com.h2rd.refactoring.usermanagement.User;

import java.util.List;

public interface UserService {
    void saveUser(User user) throws Exception;
    List<User> getUsers();
    void deleteUser(User userToDelete) throws UserNotFoundException;
    void updateUser(User userToUpdate) throws UserNotFoundException, RoleException, EmailException;
    User findUserByEmail(String email) throws UserNotFoundException, EmailException;

    List<User> findUsers(String name);
}
