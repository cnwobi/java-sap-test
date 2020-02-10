package com.h2rd.refactoring.usermanagement.dao;

import com.h2rd.refactoring.usermanagement.domain.User;

import java.util.*;

public class UserDaoImpl implements UserDao {
    private Map<String, User> users = Collections.synchronizedMap(new HashMap<>());
    @Override
    public Map<String,User> getAllUsers() {
        return users;
    }
}
