package com.h2rd.refactoring.usermanagement.dao;

import com.h2rd.refactoring.usermanagement.exception.EmailException;
import com.h2rd.refactoring.usermanagement.exception.RoleException;
import com.h2rd.refactoring.usermanagement.exception.UserNotFoundException;
import com.h2rd.refactoring.usermanagement.domain.User;

import java.util.List;
import java.util.Map;

public interface UserDao {
    void saveUser(User user) throws Exception;
   Map<String,User> getUsers();
    void deleteUser(User userToDelete) throws UserNotFoundException, EmailException;
    User updateUser(User userToUpdate) throws UserNotFoundException, RoleException, EmailException;
    User findUserByEmail(String email) throws UserNotFoundException, EmailException;

    List<User> findUsers(String name);
    List<User> findAllUsers();

}
