package com.h2rd.refactoring.usermanagement.web;

import com.h2rd.refactoring.usermanagement.config.Context;
import com.h2rd.refactoring.usermanagement.dao.UserDao;
import com.h2rd.refactoring.usermanagement.exception.*;
import com.h2rd.refactoring.usermanagement.domain.User;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.h2rd.refactoring.usermanagement.exception.email.EmailEmptyOrNullException;
import com.h2rd.refactoring.usermanagement.exception.email.EmailFormatException;
import com.h2rd.refactoring.usermanagement.exception.user.UserNotFoundException;
import org.springframework.stereotype.Controller;

import java.util.*;

@Path("/users")
@Controller

public class UserResource {


    private UserDao userDao;
    private ResponseBody responseBody;

    {
        responseBody = new ResponseBody();
        userDao = (UserDao) Context.getContext().getBean("userDao");
    }

    //to provide fresh userDao for testing;
    public UserResource(UserDao userDao) {
        if(userDao != null) this.userDao = userDao;
    }

    public UserResource() {

    }


    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUser(@QueryParam("name") String name,
                            @QueryParam("email") String email,
                            @QueryParam("role") String roles) throws Exception {

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setRoles(convertRoleStringToRoleSet(roles));

        try {

            userDao.saveUser(user);
        } catch (EmailEmptyOrNullException |UserNotFoundException | EmailFormatException | RoleException exception) {

            responseBody.setMessage(exception.getMessage());
            responseBody.setStatus(400);
            return Response.status(400).entity(responseBody).build();

        }

        return Response.status(201).entity(user).build();
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@QueryParam("name") String name,
                               @QueryParam("email") String email,
                               @QueryParam("role") String roles) throws UserNotFoundException, RoleException, EmailEmptyOrNullException {

        User user = new User();
        user.setName(name);
        user.setEmail(email);

        User updatedUser ;

        try {
            user.setRoles(convertRoleStringToRoleSet(roles));
            updatedUser = userDao.updateUser(user);


        } catch (EmailEmptyOrNullException | EmailFormatException e) {
            responseBody.setStatus(400);
            responseBody.setMessage(e.getMessage());
            return Response.status(400).entity(responseBody).build();


        } catch (UserNotFoundException e) {
            responseBody.setMessage(e.getMessage());
            responseBody.setStatus(404);
            return Response.status(404).entity(responseBody).build();
        }

        return Response.ok().entity(updatedUser).build();
    }

    @DELETE
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@QueryParam("email") String email
    ) throws UserNotFoundException {

        User user = new User();
        user.setEmail(email);

        try {
            userDao.deleteUser(user);
        } catch (UserNotFoundException e) {
            responseBody.setMessage(e.getMessage());
            responseBody.setStatus(404);
            return Response.status(404).entity(responseBody).build();
        } catch (EmailEmptyOrNullException | EmailFormatException em) {
            responseBody.setMessage(em.getMessage());
            responseBody.setStatus(400);
            return Response.status(400).entity(responseBody).build();
        }

        responseBody.setMessage("User with email " + email + " was deleted successfully");
        responseBody.setStatus(200);
        return Response.ok().entity(responseBody).build();
    }

    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() throws Exception {
      List<User> users = userDao.findAllUsers();
        if (users.isEmpty()) {

            responseBody.setStatus(200);
            responseBody.setMessage("No users found in the database");
            return Response.status(200).entity(responseBody).build();
        }

        GenericEntity<List<User>> usersEntity = new GenericEntity<List<User>>(users) {
        };
        return Response.status(200).entity(usersEntity).build();
    }

    @GET
    @Path("/search")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findUser(@QueryParam("email") String email) {

        User user;
        try {
            user = userDao.findUserByEmail(email);
        } catch (UserNotFoundException e) {
            responseBody.setMessage(e.getMessage());
            responseBody.setStatus(404);
            return Response.status(404).entity(responseBody).build();
        } catch (EmailEmptyOrNullException | EmailFormatException e) {
            responseBody.setMessage(e.getMessage());
            responseBody.setStatus(400);
            return Response.status(400).entity(responseBody).build();
        }


        return Response.ok().entity(user).build();
    }

    private Set<String> convertRoleStringToRoleSet(String roles) throws RoleException {
        Set<String> roleSet = Collections.synchronizedSet(new HashSet<>());
        if(roles == null ||roles.isEmpty()) return roleSet;


        String[] stringsOfRoles = roles.split(",");
        roleSet.addAll(Arrays.asList(stringsOfRoles));

        return roleSet;
    }

}
