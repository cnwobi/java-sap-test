package com.h2rd.refactoring.web;

import com.h2rd.refactoring.dao.UserDao;
import com.h2rd.refactoring.dao.UserDaoImpl;
import com.h2rd.refactoring.exception.EmailException;
import com.h2rd.refactoring.exception.RoleException;
import com.h2rd.refactoring.exception.UserNotFoundException;
import com.h2rd.refactoring.usermanagement.User;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.h2rd.refactoring.usermanagement.UserDao1;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Path("/users")
@Repository
public class UserResource{



    private UserDao userDao = UserDaoImpl.getUserDao();


    @GET
    @Path("add/")
    public Response addUser(@QueryParam("name") String name,
                            @QueryParam("email") String email,
                            @QueryParam("role") Set<String> roles) throws Exception {


        User user = new User();
        user.setName(name);
        user.setEmail(email);
       // user.setRoles(roles);



        userDao.saveUser(user);
        return Response.ok().entity(user).build();
    }

    @GET
    @Path("update/")
    public Response updateUser(@QueryParam("name") String name,
                               @QueryParam("email") String email,
                               @QueryParam("role") Set<String> roles) throws UserNotFoundException, RoleException, EmailException {

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        //user.setRoles(roles);



        userDao.updateUser(user);
        return Response.ok().entity(user).build();
    }

    @GET
    @Path("delete/")
    public Response deleteUser(@QueryParam("name") String name,
                               @QueryParam("email") String email,
                               @QueryParam("role") List<String> roles) throws UserNotFoundException {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
      //  user.setRoles(roles);



        userDao.deleteUser(user);
        return Response.ok().entity(user).build();
    }

    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() {


        List<User> users = userDao.getUsers();
       /* if (userDao == null) {

        }

        if (users == null || users.isEmpty()) {
            User user = new User();
           // user.setStatus("error");
            //user.setMessage("there are no users in the database");
            return Response.status(400).entity(user).build();
        }*/

        GenericEntity<List<User>> usersEntity = new GenericEntity<List<User>>(users) {};
        return Response.status(200).entity(usersEntity).build();
    }

    @GET
    @Path("search/")
    public Response findUser(@QueryParam("email") String email) throws UserNotFoundException, EmailException {


        User user = userDao.findUserByEmail(email);
        return Response.ok().entity(user).build();
    }
}
