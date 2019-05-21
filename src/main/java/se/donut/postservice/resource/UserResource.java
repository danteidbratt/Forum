package se.donut.postservice.resource;

import io.dropwizard.auth.Auth;
import se.donut.postservice.auth.AuthenticatedUser;
import se.donut.postservice.model.api.CommentDTO;
import se.donut.postservice.model.api.PostDTO;
import se.donut.postservice.model.api.UserDTO;
import se.donut.postservice.resource.request.CreateUserRequest;
import se.donut.postservice.resource.request.SortType;
import se.donut.postservice.service.CommentService;
import se.donut.postservice.service.PostService;
import se.donut.postservice.service.UserService;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;

    public UserResource(UserService userService, PostService postService, CommentService commentService) {
        this.userService = userService;
        this.postService = postService;
        this.commentService = commentService;
    }

    @Path("{userUuid}")
    @GET
    public UserDTO getUser(
            @PathParam("userUuid") UUID userUuid
    ) {
        return userService.getUser(userUuid);
    }

    @PermitAll
    @Path("me")
    @GET
    public UserDTO getMe(@Auth AuthenticatedUser authenticatedUser) {
        return userService.getUser(authenticatedUser.getUuid());
    }

    @POST
    public UserDTO createUser(@Valid CreateUserRequest request) {
        return userService.createUser(request.getUsername(), request.getPassword());
    }

    @Path("{userUuid}/posts/guest")
    @GET
    public List<PostDTO> getPostsByAuthorAsGuest(
            @PathParam("userUuid") UUID userUuid,
            @DefaultValue("New") @QueryParam("sort") SortType sortType
    ) {
        return postService.getByAuthor(userUuid, sortType);
    }

    @PermitAll
    @Path("{userUuid}/posts")
    @GET
    public List<PostDTO> getPostsByAuthor(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("userUuid") UUID userUuid,
            @DefaultValue("New") @QueryParam("sort") SortType sortType
    ) {
        return postService.getByAuthor(userUuid, sortType, authenticatedUser.getUuid());
    }

    @Path("{userUuid}/comments/guest")
    @GET
    public List<CommentDTO> getCommentsByAuthorAsGuest(
            @PathParam("userUuid") UUID userUuid,
            @DefaultValue("New") @QueryParam("sort") SortType sortType
    ) {
        return commentService.getCommentsByAuthor(userUuid, sortType);
    }

    @PermitAll
    @Path("{userUuid}/comments")
    @GET
    public List<CommentDTO> getCommentsByAuthor(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("userUuid") UUID userUuid,
            @DefaultValue("New") @QueryParam("sort") SortType sortType
    ) {
        return commentService.getCommentsByAuthor(userUuid, sortType, authenticatedUser.getUuid());
    }

    @Path("{userUuid}/likes/posts/guest")
    @GET
    public List<PostDTO> getLikedPostsAsGuest(
            @PathParam("userUuid") UUID userUuid,
            @DefaultValue("NEW") @QueryParam("sort") SortType sortType
    ) {
        return postService.getByLikes(userUuid, sortType);
    }

    @PermitAll
    @Path("{userUuid}/likes/posts")
    @GET
    public List<PostDTO> getLikedPosts(
            @Auth   AuthenticatedUser authenticatedUser,
            @PathParam("userUuid") UUID userUuid,
            @DefaultValue("NEW") @QueryParam("sort") SortType sortType
    ) {
        return postService.getByLikes(userUuid, sortType, authenticatedUser.getUuid());
    }

    @Path("{userUuid}/likes/comments/guest")
    @GET
    public List<CommentDTO> getLikedCommentsAsGuest(
            @PathParam("userUuid") UUID userUuid,
            @DefaultValue("NEW") @QueryParam("sort") SortType sortType
    ) {
        return commentService.getByLikes(userUuid, sortType);
    }

    @PermitAll
    @Path("{userUuid}/likes/comments")
    @GET
    public List<CommentDTO> getLikedComments(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("userUuid") UUID userUuid,
            @DefaultValue("NEW") @QueryParam("sort") SortType sortType
    ) {
        return commentService.getByLikes(userUuid, sortType, authenticatedUser.getUuid());
    }

}
