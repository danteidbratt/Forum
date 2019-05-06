package se.donut.postservice.resource;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.auth.Auth;
import se.donut.postservice.auth.AuthenticatedUser;
import se.donut.postservice.model.api.CommentDTO;
import se.donut.postservice.resource.request.CreateCommentRequest;
import se.donut.postservice.resource.request.SortType;
import se.donut.postservice.resource.request.VoteRequest;
import se.donut.postservice.service.CommentService;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("forums/{forumUuid}/posts/{postUuid}/comments")
public class CommentResource {

    private final CommentService commentService;

    public CommentResource(CommentService commentService) {
        this.commentService = commentService;
    }

    @PermitAll
    @POST
    public Response commentOnPost(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("forumUuid") UUID forumUuid,
            @PathParam("postUuid") UUID postUuid,
            CreateCommentRequest request
    ) {
        UUID uuid = commentService.createComment(
                postUuid,
                postUuid,
                authenticatedUser.getUuid(),
                request.getContent()
        );
        return Response.ok(uuid).build();
    }

    @Path("guest")
    @GET
    public List<CommentDTO> getCommentsByPostAsGuest(
            @PathParam("postUuid") UUID postUuid,
            @DefaultValue("TOP") @QueryParam("sort") SortType sortType
    ) {
        return commentService.getCommentsByPost(postUuid, sortType);
    }

    @PermitAll
    @GET
    public List<CommentDTO> getCommentsByPost(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("postUuid") UUID postUuid,
            @DefaultValue("TOP") @QueryParam("sort") SortType sortType
    ) {
        return commentService.getCommentViews(authenticatedUser.getUuid(), postUuid, sortType);
//        return commentService.getCommentsByPost(authenticatedUser.getUuid(), postUuid, sortType);
    }

    @PermitAll
    @Path("{commentUuid}")
    @POST
    public Response commentOnComment(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("forumUuid") UUID forumUuid,
            @PathParam("postUuid") UUID postUuid,
            @PathParam("commentUuid") UUID commentUuid,
            CreateCommentRequest request
    ) {
        // TODO: Verify that parent comment is tied to the correct post?

        UUID uuid = commentService.createComment(
                postUuid,
                commentUuid,
                authenticatedUser.getUuid(),
                request.getContent()
        );
        return Response.ok(uuid).build();
    }

    @PermitAll
    @Path("{commentUuid}/vote")
    @POST
    public void voteOnComment(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("postUuid") UUID postUuid,
            @PathParam("commentUuid") UUID commentUuid,
            VoteRequest voteRequest
    ) {
        commentService.vote(
                authenticatedUser.getUuid(),
                postUuid,
                commentUuid,
                voteRequest.getDirection()
        );
    }

    @PermitAll
    @Path("{commentUuid}/vote")
    @DELETE
    public void deleteVoteOnComment(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("commentUuid") UUID commentUuid,
            VoteRequest voteRequest
    ) {
        commentService.deleteVote(authenticatedUser.getUuid(), commentUuid);
    }
}
