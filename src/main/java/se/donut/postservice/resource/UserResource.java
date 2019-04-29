package se.donut.postservice.resource;

import se.donut.postservice.model.api.UserDTO;
import se.donut.postservice.resource.request.CreateUserRequest;
import se.donut.postservice.service.UserService;

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

    @Path("{authorUuid}")
    @GET
    public Response getUser(@PathParam("authorUuid") UUID userUuid) {
        UserDTO userDTO = userService.getPoster(userUuid);
        return Response.ok(userDTO).build();
    }

    @POST
    public Response createUser(CreateUserRequest request) {
        UUID uuid = userService.createPoster(request.getName());
        return Response.ok(uuid).build();
    }
}
