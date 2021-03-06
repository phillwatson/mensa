package com.hillayes.mensa.user.resource;

import com.hillayes.mensa.exception.common.MissingParameterException;
import com.hillayes.mensa.exception.common.NotFoundException;
import com.hillayes.mensa.user.domain.User;
import com.hillayes.mensa.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
@Slf4j
public class UsersResource {
    private final UserService userService;

    @GET
    public Response listUsers() {
        log.info("Getting users");
        return Response.ok(userService.listUsers()).build();
    }

    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") UUID id) {
        log.info("Getting user [id: {}]", id);

        return userService.getUser(id)
            .map(user -> Response.ok(user).build())
            .orElseThrow(() -> new NotFoundException("user", id));
    }

    @POST
    public Response createUser(User user) {
        if (user == null) {
            throw new MissingParameterException("user content");
        }

        log.info("Creating user [username: {}]", user.getUsername());
        User result = userService.createUser(user.getUsername(), user.getPasswordHash().toCharArray(), user.getEmail());

        log.debug("Created user [username: {}, id: {}]", result.getUsername(), result.getId());
        return Response.ok(result).build();
    }

    @PUT
    @Path("/{id}/onboard")
    public Response onboardUser(@PathParam("id") UUID id) {
        log.info("Onboard user [id: {}]", id);
        return userService.onboardUser(id)
            .map(user -> {
                log.debug("Onboarded user [username: {}, id: {}]", user.getUsername(), user.getId());
                return Response.ok(user).build();
            })
            .orElseThrow(() -> new NotFoundException("user", id));
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") UUID id,
                               User userUpdate) {
        log.info("Update user [id: {}]", id);
        return userService.updateUser(id, userUpdate)
            .map(user -> {
                log.debug("Updated user [username: {}, id: {}]", user.getUsername(), user.getId());
                return Response.ok(user).build();
            })
            .orElseThrow(() -> new NotFoundException("user", id));
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") UUID id) {
        log.info("Delete user [id: {}]", id);
        return userService.deleteUser(id)
            .map(user -> {
                log.debug("Deleted user [username: {}, id: {}]", user.getUsername(), user.getId());
                return Response.ok().build();
            })
            .orElseThrow(() -> new NotFoundException("user", id));
    }
}
