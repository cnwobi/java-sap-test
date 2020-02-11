package com.h2rd.refactoring.usermanagement.dao;

import com.h2rd.refactoring.usermanagement.exception.email.EmailEmptyOrNullException;
import com.h2rd.refactoring.usermanagement.exception.email.EmailFormatException;
import com.h2rd.refactoring.usermanagement.exception.RoleException;
import com.h2rd.refactoring.usermanagement.exception.user.UserNotFoundException;
import com.h2rd.refactoring.usermanagement.domain.User;

import java.util.List;
import java.util.Map;

public interface UserDao {
    void saveUser(User user) throws Exception;
   Map<String,User> getUsers();
    void deleteUser(User userToDelete) throws UserNotFoundException, EmailEmptyOrNullException, EmailFormatException;
    User updateUser(User userToUpdate) throws UserNotFoundException, RoleException, EmailEmptyOrNullException, EmailFormatException;
    User findUserByEmail(String email) throws UserNotFoundException, EmailEmptyOrNullException, EmailFormatException;

    List<User> findUsers(String name);
    List<User> findAllUsers();

}
