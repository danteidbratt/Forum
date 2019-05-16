package se.donut.postservice.resource;

import io.dropwizard.auth.Auth;
import se.donut.postservice.auth.AuthenticatedUser;
import se.donut.postservice.model.api.ForumDTO;
import se.donut.postservice.model.api.PostDTO;
import se.donut.postservice.resource.request.CreateForumRequest;
import se.donut.postservice.resource.request.CreatePostRequest;
import se.donut.postservice.resource.request.SortType;
import se.donut.postservice.service.ForumService;
import se.donut.postservice.service.PostService;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("forums")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ForumResource {

    private final ForumService forumService;
    private final PostService postService;

    public ForumResource(ForumService forumService, PostService postService) {
        this.forumService = forumService;
        this.postService = postService;
    }

    @Path("guest")
    @GET
    public List<ForumDTO> getAllForumsAsGuest(
            @DefaultValue("TOP") @QueryParam("sort") SortType sortType
    ) {
        return forumService.getAllForums(sortType);
    }

    @PermitAll
    @GET
    public List<ForumDTO> getAllForums(
            @Auth AuthenticatedUser authenticatedUser,
            @DefaultValue("TOP") @QueryParam("sort") SortType sortType
    ) {
        return forumService.getAllForums(sortType, authenticatedUser.getUuid());
    }

    @PermitAll
    @POST
    public UUID createForum(
            @Auth AuthenticatedUser user,
            @Valid CreateForumRequest request
    ) {
        return forumService.createForum(
                user.getUuid(),
                request.getName(),
                request.getDescription()
        );
    }

    @Path("{forumUuid}/posts/guest")
    @GET
    public List<PostDTO> getPostsByForumAsGuest(
            @PathParam("forumUuid") UUID forumUuid,
            @DefaultValue("HOT") @QueryParam("sort") SortType sortType
    ) {
        return postService.getByForum(forumUuid, sortType);
    }

    @PermitAll
    @Path("{forumUuid}/posts")
    @GET
    public List<PostDTO> getPostsByForum(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("forumUuid") UUID forumUuid,
            @DefaultValue("HOT") @QueryParam("sort") SortType sortType
    ) {
        return postService.getByForum(forumUuid, sortType, authenticatedUser.getUuid());
    }

    @PermitAll
    @Path("{forumUuid}/posts")
    @POST
    public Response createPost(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("forumUuid") UUID forumUuid,
            @Valid CreatePostRequest request
    ) {
        UUID uuid = postService.createPost(
                forumUuid,
                authenticatedUser.getUuid(),
                request.getTitle(),
                request.getContent()
        );
        return Response.ok(uuid).build();
    }

    @PermitAll
    @Path("{forumUuid}/subscriptions")
    @POST
    public Response subscribe(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("forumUuid") UUID forumUuid
    ) {
        forumService.subscribe(authenticatedUser.getUuid(), forumUuid);
        return Response.ok().build();
    }

    @PermitAll
    @Path("{forumUuid}/subscriptions")
    @DELETE
    public Response unsubscribe(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("forumUuid") UUID forumUuid
    ) {
        forumService.unsubscribe(authenticatedUser.getUuid(), forumUuid);
        return Response.ok().build();
    }

    @PermitAll
    @Path("subscriptions")
    @GET
    public List<ForumDTO> getSubscribedForums(
            @Auth AuthenticatedUser authenticatedUser,
            @DefaultValue("TOP") @QueryParam("sort") SortType sortType
    ) {
        return forumService.getSubscriptions(authenticatedUser.getUuid(), sortType);
    }

}
