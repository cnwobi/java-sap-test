package com.h2rd.refactoring.usermanagement.dao;

import com.h2rd.refactoring.usermanagement.domain.User;


import java.util.Map;

public interface UserDao {

    Map<String,User> getAllUsers();

}
