package se.donut.postservice.resource;

import io.dropwizard.auth.Auth;
import se.donut.postservice.auth.AuthenticatedUser;
import se.donut.postservice.model.api.UserDTO;
import se.donut.postservice.resource.request.CreateUserRequest;
import se.donut.postservice.service.UserService;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @PermitAll
    @Path("{userUuid}")
    @GET
    public Response getUser(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("userUuid") UUID userUuid
    ) {
        UserDTO userDTO = userService.getUser(userUuid);
        return Response.ok(userDTO).build();
    }

    @POST
    public Response createUser(CreateUserRequest request) {
        userService.createUser(request.getUsername(), request.getPassword());
        return Response.ok().build();
    }
}
