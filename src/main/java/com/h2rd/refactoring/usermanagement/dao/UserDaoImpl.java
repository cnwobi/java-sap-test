package com.h2rd.refactoring.usermanagement.dao;

import com.h2rd.refactoring.usermanagement.dao.UserDao;
import com.h2rd.refactoring.usermanagement.exception.EmailException;
import com.h2rd.refactoring.usermanagement.exception.RoleException;
import com.h2rd.refactoring.usermanagement.exception.UserNotFoundException;
import com.h2rd.refactoring.usermanagement.domain.User;
import org.springframework.stereotype.Component;

import java.util.*;


public class UserDaoImpl implements UserDao {

    private Map<String,User> users = Collections.synchronizedMap(new HashMap<>());
    @Override
    public void saveUser(User user) throws EmailException, RoleException {
        if ( user.getEmail() == null || user.getEmail().isEmpty() ) throw new EmailException("A valid email is required to add user");
        if (!userEmailIsUnique(user)) throw new EmailException("Email provided already exists on record");
        if (!userHasAtLeastOneRole(user)) throw new RoleException("A user must have at least one role");
        users.put(user.getEmail(),user);
    }

    @Override
    public Map<String, User> getUsers() {
        return users;
    }

    private boolean userHasAtLeastOneRole(User user) {
        return !user.getRoles().isEmpty();
    }

    private boolean userEmailIsUnique(User user) {
               return !users.containsKey(user.getEmail());
    }

    private boolean userExists(User user) {
      return   users.containsKey(user.getEmail());
    }



    @Override
    public void deleteUser(User userToDelete) throws UserNotFoundException, EmailException {
       if(userToDelete.getEmail() == null|| userToDelete.getEmail().isEmpty() ){
           throw new EmailException("Please provide a valid email address");
       }
        if (!userExists(userToDelete)) {
            throw new UserNotFoundException("User with email address " + userToDelete.getEmail() + " does not exist and cannot be deleted");
        }

        users.remove(userToDelete.getEmail());

    }

    @Override
    public User updateUser(User userToUpdate) throws UserNotFoundException, RoleException, EmailException {
        User update = findUserByEmail(userToUpdate.getEmail());
        update.setName(userToUpdate.getName());

        if (userToUpdate.getRoles() != null && userToUpdate.getRoles().size() > 0) {
            update.getRoles().clear();
            update.setRoles(userToUpdate.getRoles());
        }

        if (!userToUpdate.getEmail().isEmpty() && userToUpdate.getEmail() != null) {
            update.setEmail(userToUpdate.getEmail());
        } else {
            throw new EmailException("Provide a valid email for update");
        }

      return update;
    }

    private Optional<User> findOptionalUserByEmail(String email) throws  EmailException {
        if (email == null || email.isEmpty()) throw new EmailException("Please provide a valid email address");

        return Optional.ofNullable(users.get(email));
    }

    @Override
    public User findUserByEmail(String email) throws UserNotFoundException, EmailException {
              return findOptionalUserByEmail(email).orElseThrow(() -> new UserNotFoundException("User with email address " + email + " does not exist on record"));
    }

    @Override
    public List<User> findUsers(String name) {
        List<User> usersList = Collections.synchronizedList(new ArrayList<>());
        for(Map.Entry<String,User> entry : users.entrySet()){
            if(entry.getValue().getName().equalsIgnoreCase(name)){
                usersList.add(entry.getValue());
            }

        }
        return usersList;
    }

    @Override
    public List<User> findAllUsers() {
        List<User> usersList = Collections.synchronizedList(new ArrayList<>());
        for(Map.Entry<String,User> entry : users.entrySet()){
                usersList.add(entry.getValue());
        }
        return usersList;
    }
}
