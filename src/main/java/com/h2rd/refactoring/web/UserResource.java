package com.h2rd.refactoring.web;

import com.h2rd.refactoring.dao.UserDao;
import com.h2rd.refactoring.dao.UserDaoImpl;
import com.h2rd.refactoring.exception.EmailException;
import com.h2rd.refactoring.exception.ResponseMessage;
import com.h2rd.refactoring.exception.RoleException;
import com.h2rd.refactoring.exception.UserNotFoundException;
import com.h2rd.refactoring.usermanagement.User;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Repository;

import java.util.*;

@Path("/users")
@Repository
public class UserResource{



    private UserDao userDao = UserDaoImpl.getUserDao();


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
       user.setRoles(rolesSet(roles));
       try {
           userDao.saveUser(user);
       }
       catch (EmailException | RoleException exception){
           ResponseMessage e = new ResponseMessage();
           e.setMessage(exception.getMessage());
           e.setStatus(400);
           return Response.status(400).entity(e).build();

       }

        return Response.status(201).entity(user).build();
    }

    @GET
    @Path("update/")
    public Response updateUser(@QueryParam("name") String name,
                               @QueryParam("email") String email,
                               @QueryParam("role") Set<String> roles) throws UserNotFoundException, RoleException, EmailException {

       /* User user = User.builder()
                .email(email)
                .name(name).build();
*/
User user =  new User();




        userDao.updateUser(user);
        return Response.ok().entity(user).build();
    }

    @DELETE
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@QueryParam("email") String email
                              ) throws UserNotFoundException {
        ResponseMessage responseMessage =  new ResponseMessage();
        User user = new User();
        user.setEmail(email);

       try{
           userDao.deleteUser(user);
       }
        catch (UserNotFoundException e){
           responseMessage.setMessage(e.getMessage());
           responseMessage.setStatus(404);
           return Response.status(404).entity(responseMessage).build();
        }
       catch (EmailException em){
           responseMessage.setMessage(em.getMessage());
           responseMessage.setStatus(400);
           return Response.status(400).entity(responseMessage).build();
       }

        responseMessage.setMessage("User with email "+email+" was deleted successfully");
         responseMessage.setStatus(200);
        return Response.ok().entity(responseMessage).build();
    }

    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() throws Exception {
            List<User> users = userDao.getUsers();
      if(users.isEmpty()) {
          ResponseMessage e = new ResponseMessage();
          e.setStatus(200);
          e.setMessage("No users found in the database");
          return Response.status(200).entity(e).build();
      }

        GenericEntity<List<User>> usersEntity = new GenericEntity<List<User>>(users) {};
        return Response.status(200).entity(usersEntity).build();
    }

    @GET
    @Path("search/")
    public Response findUser(@QueryParam("email") String email) throws UserNotFoundException, EmailException {


        User user = userDao.findUserByEmail(email);
        return Response.ok().entity(user).build();
    }

    private Set<String> rolesSet(String roles){

        Set<String> roleSet = Collections.synchronizedSet(new HashSet<>());
        if(roles !=null) {
            String[] stringsOfRoles = roles.split(",");
            roleSet.addAll(Arrays.asList(stringsOfRoles));

        }

        return roleSet;
    }

}
