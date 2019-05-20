package se.donut.postservice.resource;

import io.dropwizard.auth.Auth;
import se.donut.postservice.auth.AuthenticatedUser;
import se.donut.postservice.model.api.CommentDTO;
import se.donut.postservice.model.api.PostDTO;
import se.donut.postservice.resource.request.CreateCommentRequest;
import se.donut.postservice.resource.request.SortType;
import se.donut.postservice.resource.request.VoteRequest;
import se.donut.postservice.service.CommentService;
import se.donut.postservice.service.PostService;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private final PostService postService;
    private final CommentService commentService;

    public PostResource(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @Path("guest")
    @GET
    public List<PostDTO> getAllPostsAsGuest(
            @DefaultValue("HOT") @QueryParam("sort") SortType sortType
    ) {
        return postService.getAll(sortType);
    }

    @PermitAll
    @GET
    public List<PostDTO> getAllPosts(
            @Auth AuthenticatedUser authenticatedUser,
            @DefaultValue("HOT") @QueryParam("sort") SortType sortType
    ) {
        return postService.getAll(sortType, authenticatedUser.getUuid());
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
            @PathParam("postUuid") UUID postUuid,
            @Valid VoteRequest voteRequest
    ) {
        postService.vote(postUuid, authenticatedUser.getUuid(), voteRequest.getDirection());
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

    @PermitAll
    @Path("{postUuid}/comments")
    @POST
    public CommentDTO createComment(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("postUuid") UUID postUuid,
            CreateCommentRequest request
    ) {
        return commentService.createComment(
                postUuid,
                request.getParentUuid(),
                authenticatedUser.getUuid(),
                authenticatedUser.getName(),
                request.getContent()
        );
    }

    @Path("{postUuid}/comments/guest")
    @GET
    public List<CommentDTO> getCommentsByPostAsGuest(
            @PathParam("postUuid") UUID postUuid,
            @DefaultValue("TOP") @QueryParam("sort") SortType sortType
    ) {
        return commentService.getCommentsByPost(postUuid, sortType);
    }

    @PermitAll
    @Path("{postUuid}/comments")
    @GET
    public List<CommentDTO> getCommentsByPost(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("postUuid") UUID postUuid,
            @DefaultValue("TOP") @QueryParam("sort") SortType sortType
    ) {
        return commentService.getCommentsByPost(postUuid, sortType, authenticatedUser.getUuid());
    }

    @PermitAll
    @Path("subscriptions")
    @GET
    public List<PostDTO> getPostsBySubscriptions(
            @Auth AuthenticatedUser authenticatedUser,
            @DefaultValue("HOT") @QueryParam("sort") SortType sortType
    ) {
        return postService.getBySubscriptions(authenticatedUser.getUuid(), sortType);
    }

}
