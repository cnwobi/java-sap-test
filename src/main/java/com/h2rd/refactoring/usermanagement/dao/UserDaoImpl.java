package com.h2rd.refactoring.usermanagement.dao;

import com.h2rd.refactoring.usermanagement.exception.email.EmailEmptyOrNullException;
import com.h2rd.refactoring.usermanagement.exception.email.EmailFormatException;
import com.h2rd.refactoring.usermanagement.exception.RoleException;
import com.h2rd.refactoring.usermanagement.exception.user.UserNotFoundException;
import com.h2rd.refactoring.usermanagement.domain.User;
import com.h2rd.refactoring.usermanagement.exception.user.UserNotUniqueException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UserDaoImpl implements UserDao {

    private Map<String,User> users = Collections.synchronizedMap(new HashMap<>());
    @Override
    public void saveUser(User user) throws EmailEmptyOrNullException, RoleException, EmailFormatException, UserNotUniqueException {
        if (!emailIsValid(user.getEmail()) ) throw getEmptyOrNullEmailException();
        if (!userEmailIsUnique(user)) throw new UserNotUniqueException("A user with this email provided already exists on record");
        if (!userHasAtLeastOneRole(user)) throw new RoleException("A user must have at least one role");
        //convert input email to lower case
        user.setEmail(user.getEmail().toLowerCase());
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
               return !users.containsKey(user.getEmail().toLowerCase());
    }

    private boolean userExists(User user) {
      return   users.containsKey(user.getEmail().toLowerCase());
    }
    private EmailEmptyOrNullException getEmptyOrNullEmailException() {
        String s = "Email address must not be empty";
        return new EmailEmptyOrNullException(s);
    }



    @Override
    public void deleteUser(User userToDelete) throws UserNotFoundException, EmailEmptyOrNullException, EmailFormatException {
       if(!emailIsValid(userToDelete.getEmail()) ){
           throw getEmptyOrNullEmailException();
       }
        if (!userExists(userToDelete)) {
            throw new UserNotFoundException("User with email address " + userToDelete.getEmail() + " does not exist and cannot be deleted");
        }

        users.remove(userToDelete.getEmail().toLowerCase());

    }

    @Override
    public User updateUser(User userToUpdate) throws UserNotFoundException, EmailFormatException,EmailEmptyOrNullException {
        User update = findUserByEmail(userToUpdate.getEmail());
        update.setName(userToUpdate.getName());

        if (userToUpdate.getRoles() != null && userToUpdate.getRoles().size() > 0) {
            update.getRoles().clear();
            update.setRoles(userToUpdate.getRoles());
        }

        if (emailIsValid(userToUpdate.getEmail())) {
            update.setEmail(userToUpdate.getEmail());
        } else {
            throw getEmptyOrNullEmailException();
        }

      return update;
    }


    @Override
    public User findUserByEmail(String email) throws UserNotFoundException, EmailEmptyOrNullException, EmailFormatException {
        if (!emailIsValid(email)) throw getEmptyOrNullEmailException();
        Optional<User> optionalUser = Optional.ofNullable(users.get(email.toLowerCase()));
                 return optionalUser.orElseThrow(() -> new UserNotFoundException("User with email address " + email + " does not exist on record"));
    }



    @Override
    public List<User> findUsersByName(String name) {
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

    public boolean emailIsValid(String email) throws EmailFormatException {
         if (email == null || email.isEmpty()) return false;
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher emailMatcher =pattern.matcher(email);
        if(!emailMatcher.matches()){
            throw new EmailFormatException("Email provided is not of the expected format");
        }

        return emailMatcher.matches();
    }
}
