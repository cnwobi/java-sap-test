package com.h2rd.refactoring.usermanagement;

import java.util.ArrayList;

public class UserDao1 {

    public ArrayList<User> users;

    public static UserDao1 userDao1;

    public static UserDao1 getUserDao1() {
        if (userDao1 == null) {
            userDao1 = new UserDao1();
        }
        return userDao1;
    }

    public void saveUser(User user) {
        if (users == null) {
            users = new ArrayList<User>();
        }
        users.add(user);
    }

    public ArrayList<User> getUsers() {
        try {
            return users;
        } catch (Throwable e) {
            System.out.println("error");
            return null;
        }
    }

    public void deleteUser(User userToDelete) {
        try {
            for (User user : users) {
                if (user.getName() == userToDelete.getName()) {
                    users.remove(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUser(User userToUpdate) {
        try {
            for (User user : users) {
                if (user.getName() == userToUpdate.getName()) {
                    user.setEmail(userToUpdate.getEmail());
                    user.setRoles(userToUpdate.getRoles());
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public User findUser(String name) {
        try {
            for (User user : users) {
                if (user.getName() == name) {
                    return user;
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }
}
