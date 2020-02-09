package com.h2rd.refactoring.web;

import com.h2rd.refactoring.usermanagement.User;
import com.h2rd.refactoring.usermanagement.UserDao;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Path("/users")
@Repository
public class UserResource{

    public UserDao userDao;

    @GET
    @Path("add/")
    public Response addUser(@QueryParam("name") String name,
                            @QueryParam("email") String email,
                            @QueryParam("role") List<String> roles) {

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setRoles(roles);

        if (userDao == null) {
            userDao = UserDao.getUserDao();
        }

        userDao.saveUser(user);
        return Response.ok().entity(user).build();
    }

    @GET
    @Path("update/")
    public Response updateUser(@QueryParam("name") String name,
                               @QueryParam("email") String email,
                               @QueryParam("role") List<String> roles) {

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setRoles(roles);

        if (userDao == null) {
            userDao = UserDao.getUserDao();
        }

        userDao.updateUser(user);
        return Response.ok().entity(user).build();
    }

    @GET
    @Path("delete/")
    public Response deleteUser(@QueryParam("name") String name,
                               @QueryParam("email") String email,
                               @QueryParam("role") List<String> roles) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setRoles(roles);

        if (userDao == null) {
            userDao = UserDao.getUserDao();
        }

        userDao.deleteUser(user);
        return Response.ok().entity(user).build();
    }

    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() {

        if (userDao == null) {
            userDao = UserDao.getUserDao();
        }

        List<User> users = userDao.getUsers();
        if (users == null || users.isEmpty()) {
            User user = new User();
           // user.setStatus("error");
            //user.setMessage("there are no users in the database");
            return Response.status(400).entity(user).build();
        }

        GenericEntity<List<User>> usersEntity = new GenericEntity<List<User>>(users) {};
        return Response.status(200).entity(usersEntity).build();
    }

    @GET
    @Path("search/")
    public Response findUser(@QueryParam("name") String name) {

        if (userDao == null) {
            userDao = UserDao.getUserDao();
        }

        User user = userDao.findUser(name);
        return Response.ok().entity(user).build();
    }
}
