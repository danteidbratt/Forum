package se.donut.postservice.resource;

import io.dropwizard.auth.Auth;
import se.donut.postservice.auth.AuthenticatedUser;
import se.donut.postservice.model.api.PostDTO;
import se.donut.postservice.model.api.UserDTO;
import se.donut.postservice.resource.request.CreateUserRequest;
import se.donut.postservice.service.PostService;
import se.donut.postservice.service.UserService;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.List;
import java.util.UUID;

@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserService userService;
    private final PostService postService;
    private final UriBuilder uriBuilder = UriBuilder.fromResource(UserResource.class);

    public UserResource(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
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

    @Path("{userUuid}/posts/guest")
    @GET
    public List<PostDTO> getPostsAsGuest(
            @PathParam("userUuid") UUID userUuid
    ) {
        return postService.getByAuthor(userUuid);
    }

    @PermitAll
    @Path("{userUuid}/posts")
    @GET
    public List<PostDTO> getPosts(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("userUuid") UUID userUuid
    ) {
        return postService.getByAuthor(userUuid, authenticatedUser.getUuid());
    }

    @Path("{userUuid}/likes/guest")
    @GET
    public List<PostDTO> getLikesAsGuest(
            @PathParam("userUuid") UUID userUuid
    ) {
        return postService.getLiked(userUuid);
    }

    @PermitAll
    @Path("{userUuid}/likes")
    @GET
    public List<PostDTO> getLikes(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("userUuid") UUID userUuid
    ) {
        return postService.getLiked(userUuid, authenticatedUser.getUuid());
    }


}
