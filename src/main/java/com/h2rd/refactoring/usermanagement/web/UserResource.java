package com.h2rd.refactoring.usermanagement.web;

import com.h2rd.refactoring.usermanagement.config.Context;
import com.h2rd.refactoring.usermanagement.dao.UserDao;
import com.h2rd.refactoring.usermanagement.exception.EmailException;
import com.h2rd.refactoring.usermanagement.exception.ResponseMessage;
import com.h2rd.refactoring.usermanagement.exception.RoleException;
import com.h2rd.refactoring.usermanagement.exception.UserNotFoundException;
import com.h2rd.refactoring.usermanagement.domain.User;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Controller;

import java.util.*;

@Path("/users")
@Controller

public class UserResource {


    private UserDao userDao;
    private ResponseMessage responseMessage;

    {
        responseMessage = new ResponseMessage();
        userDao = (UserDao) Context.getContext().getBean("userService");
    }
//for testing
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
        } catch (EmailException | RoleException exception) {

            responseMessage.setMessage(exception.getMessage());
            responseMessage.setStatus(400);
            return Response.status(400).entity(responseMessage).build();

        }

        return Response.status(201).entity(user).build();
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@QueryParam("name") String name,
                               @QueryParam("email") String email,
                               @QueryParam("role") String roles) throws UserNotFoundException, RoleException, EmailException {

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setRoles(convertRoleStringToRoleSet(roles));
        User updateUser;

        try {
            updateUser = userDao.updateUser(user);


        } catch (EmailException e) {
            responseMessage.setStatus(400);
            responseMessage.setMessage(e.getMessage());
            return Response.status(400).entity(responseMessage).build();


        } catch (UserNotFoundException e) {
            responseMessage.setMessage(e.getMessage());
            responseMessage.setStatus(404);
            return Response.status(404).entity(responseMessage).build();
        }

        return Response.ok().entity(updateUser).build();
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
            responseMessage.setMessage(e.getMessage());
            responseMessage.setStatus(404);
            return Response.status(404).entity(responseMessage).build();
        } catch (EmailException em) {
            responseMessage.setMessage(em.getMessage());
            responseMessage.setStatus(400);
            return Response.status(400).entity(responseMessage).build();
        }

        responseMessage.setMessage("User with email " + email + " was deleted successfully");
        responseMessage.setStatus(200);
        return Response.ok().entity(responseMessage).build();
    }

    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() throws Exception {
        List<User> users = userDao.findAllUsers();
        if (users.isEmpty()) {

            responseMessage.setStatus(200);
            responseMessage.setMessage("No users found in the database");
            return Response.status(200).entity(responseMessage).build();
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
            responseMessage.setMessage(e.getMessage());
            responseMessage.setStatus(404);
            return Response.status(404).entity(responseMessage).build();
        } catch (EmailException e) {
            responseMessage.setMessage(e.getMessage());
            responseMessage.setStatus(400);
            return Response.status(400).entity(responseMessage).build();
        }


        return Response.ok().entity(user).build();
    }

    private Set<String> convertRoleStringToRoleSet(String roles) {

        Set<String> roleSet = Collections.synchronizedSet(new HashSet<>());
        if (roles != null) {
            String[] stringsOfRoles = roles.split(",");
            roleSet.addAll(Arrays.asList(stringsOfRoles));

        }

        return roleSet;
    }

}
