package se.donut.postservice.resource;

import io.dropwizard.auth.Auth;
import se.donut.postservice.auth.AuthenticatedUser;
import se.donut.postservice.model.api.PostDTO;
import se.donut.postservice.resource.request.CreatePostRequest;
import se.donut.postservice.resource.request.SortType;
import se.donut.postservice.resource.request.VoteRequest;
import se.donut.postservice.service.PostService;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("forums/{forumUuid}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private final PostService postService;

    public PostResource(PostService postService) {
        this.postService = postService;
    }

    @PermitAll
    @POST
    public Response createPost(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("forumUuid") UUID forumUuid,
            CreatePostRequest request
    ) {
        UUID uuid = postService.createPost(
                forumUuid,
                authenticatedUser.getUuid(),
                request.getTitle(),
                request.getContent()
        );
        return Response.ok(uuid).build();
    }

    @Path("guest")
    @GET
    public List<PostDTO> getPostsByForumAsGuest(
            @PathParam("forumUuid") UUID forumUuid,
            @DefaultValue("HOT") @QueryParam("sort") SortType sortType
    ) {
        return postService.getByForum(forumUuid, sortType);
    }

    @PermitAll
    @GET
    public List<PostDTO> getPostsByForum(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("forumUuid") UUID forumUuid,
            @DefaultValue("HOT") @QueryParam("sort") SortType sortType
    ) {
        return postService.getByForum(forumUuid, sortType, authenticatedUser.getUuid());
    }

    @Path("{postUuid}/guest")
    @GET
    public PostDTO getPostAsGuest(
            @PathParam("postUuid") UUID postUuid) {
        return postService.getPost(postUuid);
    }

    @PermitAll
    @Path("{postUuid}")
    @GET
    public PostDTO getPost(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("postUuid") UUID postUuid) {
        return postService.getPost(postUuid, authenticatedUser.getUuid());
    }

    @PermitAll
    @Path("{postUuid}/vote")
    @POST
    public Response voteOnPost(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("forumUuid") UUID forumUuid,
            @PathParam("postUuid") UUID postUuid,
            VoteRequest voteRequest
    ) {
        postService.vote(forumUuid, postUuid, authenticatedUser.getUuid(), voteRequest.getDirection());
        return Response.ok().build();
    }

    @PermitAll
    @Path("{postUuid}/vote")
    @DELETE
    public Response deleteVoteOnPost(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("postUuid") UUID postUuid
    ) {
        postService.deleteVote(authenticatedUser.getUuid(), postUuid);
        return Response.ok().build();
    }

}
