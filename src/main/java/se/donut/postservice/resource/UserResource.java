package se.donut.postservice.resource;

import se.donut.postservice.model.api.UserDTO;
import se.donut.postservice.resource.request.CreateUserRequest;
import se.donut.postservice.service.UserService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.UUID;

@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserService userService;
    private final UriBuilder uriBuilder = UriBuilder.fromResource(UserResource.class);

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @Path("{userUuid}")
    @GET
    public UserDTO getUser(
            @PathParam("userUuid") UUID userUuid
    ) {
        return userService.getUser(userUuid);
    }

    @POST
    public Response createUser(CreateUserRequest request) {
        UUID uuid = userService.createUser(request.getUsername(), request.getPassword());
        return Response.ok(uuid).build();
//        return Response.created(uriBuilder.path(uuid.toString()).build()).build();
    }
}
