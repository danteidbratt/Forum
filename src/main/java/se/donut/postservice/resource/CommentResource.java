package se.donut.postservice.resource;

import io.dropwizard.auth.Auth;
import se.donut.postservice.auth.AuthenticatedUser;
import se.donut.postservice.model.api.CommentDTO;
import se.donut.postservice.resource.request.SortType;
import se.donut.postservice.resource.request.VoteRequest;
import se.donut.postservice.service.CommentService;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("comments")
public final class CommentResource {

    private final CommentService commentService;

    public CommentResource(CommentService commentService) {
        this.commentService = commentService;
    }

    @PermitAll
    @Path("{commentUuid}/vote")
    @POST
    public void voteOnComment(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("commentUuid") UUID commentUuid,
            @Valid VoteRequest voteRequest
    ) {
        commentService.vote(
                authenticatedUser.getUuid(),
                commentUuid,
                voteRequest.getDirection()
        );
    }

    @PermitAll
    @Path("{commentUuid}/vote")
    @DELETE
    public void deleteVoteOnComment(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("commentUuid") UUID commentUuid
    ) {
        commentService.deleteVote(authenticatedUser.getUuid(), commentUuid);
    }

    @PermitAll
    @Path("likes")
    @GET
    public List<CommentDTO> getLikeComments(
            @Auth AuthenticatedUser authenticatedUser,
            @DefaultValue("NEW") @QueryParam("sort") SortType sortType
    ) {
        return commentService.getByLikes(authenticatedUser.getUuid(), sortType);
    }
}
