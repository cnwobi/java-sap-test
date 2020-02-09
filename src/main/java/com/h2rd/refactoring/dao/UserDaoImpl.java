package com.h2rd.refactoring.dao;

import com.h2rd.refactoring.usermanagement.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserDaoImpl implements UserDao {
    private List<User> users = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void saveUser(User user) throws Exception {
        users.add(user);

    }

    @Override
    public List<User> getUsers() {
        return users;
    }

    @Override
    public void deleteUser(User userToDelete) {
        users.removeIf(user -> user.getEmail().equalsIgnoreCase(userToDelete.getEmail()));

    }
}
