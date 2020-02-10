package com.h2rd.refactoring.usermanagement.dao;

import com.h2rd.refactoring.usermanagement.exception.EmailException;
import com.h2rd.refactoring.usermanagement.exception.RoleException;
import com.h2rd.refactoring.usermanagement.exception.UserNotFoundException;
import com.h2rd.refactoring.usermanagement.domain.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class UserDaoImpl implements UserDao {

    private static UserDao userDao;
    private List<User> users = Collections.synchronizedList(new ArrayList<>());




   private UserDaoImpl(){

   }

   public static UserDao getUserDao(){
        if(userDao == null) userDao = new UserDaoImpl();
        return userDao;
   }
    @Override
    public void saveUser(User user) throws EmailException, RoleException {
        if ( user.getEmail() == null || user.getEmail().isEmpty() ) throw new EmailException("A valid email is required to add user");
        if (!userEmailIsUnique(user)) throw new EmailException("Email provided already exists on record");
        if (!userHasAtLeastOneRole(user)) throw new RoleException("User must have at least one role");
        userDao.getUsers().add(user);


    }

    private boolean userHasAtLeastOneRole(User user) {
        return !user.getRoles().isEmpty();
    }

    private boolean userEmailIsUnique(User user) {
        List<User> users = userDao.getUsers();
        Optional<User> optionalUser = users.stream()
                .filter(user1 -> user1.getEmail().equals(user.getEmail())).findFirst();

        return !optionalUser.isPresent();
    }

    private boolean userExists(User user) {
        Optional<User> optionalUser = getUsers().stream()
                .filter(user1 -> user1.getEmail().equals(user.getEmail())).findFirst();

        return optionalUser.isPresent();
    }

    @Override
    public List<User> getUsers() {
        return users;
    }

    @Override
    public void deleteUser(User userToDelete) throws UserNotFoundException, EmailException {
       if(userToDelete.getEmail() == null|| userToDelete.getEmail().isEmpty() ){
           throw new EmailException("Please provide a valid email address");
       }
        if (!userExists(userToDelete)) {
            throw new UserNotFoundException("User with email address " + userToDelete.getEmail() + " does not exist and cannot be deleted");
        }
         users.removeIf(user -> user.getEmail().equalsIgnoreCase(userToDelete.getEmail()));

    }

    @Override
    public void updateUser(User userToUpdate) throws UserNotFoundException, RoleException, EmailException {
        User update = findUserByEmail(userToUpdate.getEmail());
        update.setName(userToUpdate.getName());

        if (userToUpdate.getRoles() != null && userToUpdate.getRoles().size() > 0) {
            update.setRoles(userToUpdate.getRoles());
        } else {
            throw new RoleException("User must have a role");
        }

        if (!userToUpdate.getEmail().isEmpty() && userToUpdate.getEmail() != null) {
            update.setEmail(userToUpdate.getEmail());
        } else {
            throw new EmailException("Provide a valid email for update");
        }


    }

    private Optional<User> findOptionalUserByEmail(String email) throws UserNotFoundException {
        if (email == null || email.isEmpty()) throw new UserNotFoundException("Please provide a valid email address");


        return getUsers().stream().filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public User findUserByEmail(String email) throws UserNotFoundException {

        return findOptionalUserByEmail(email).orElseThrow(() -> new UserNotFoundException("User with " + email + " does not exist on record"));
    }

    @Override
    public List<User> findUsers(String name) {
        List<User> users = Collections.synchronizedList(new ArrayList<>());
        getUsers().stream().filter(user -> user.getName().equalsIgnoreCase(name))
                .forEach(users::add);
        return users;
    }
}
