package se.donut.postservice.resource;

import io.dropwizard.auth.Auth;
import se.donut.postservice.auth.AuthenticatedUser;
import se.donut.postservice.model.api.CommentDTO;
import se.donut.postservice.resource.request.CreateCommentRequest;
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

    @GET
    public List<CommentDTO> getComments(
            @PathParam("forumUuid") UUID forumUuid,
            @PathParam("postUuid") UUID postUuid
    ) {
        return commentService.getComments(postUuid);
    }

    @Path("{commentUuid}/children")
    @GET
    public List<CommentDTO> getNestedComments(
            @PathParam("forumUuid") UUID forumUuid,
            @PathParam("postUuid") UUID postUuid,
            @PathParam("commentUuid") UUID commentUuid
    ) {
        return commentService.getComments(commentUuid);
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
                authenticatedUser.getName(),
                request.getContent()
        );
        return Response.ok(uuid).build();
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
        // TODO: Verify that parent comment is tied to the correct post

        UUID uuid = commentService.createComment(
                postUuid,
                commentUuid,
                authenticatedUser.getUuid(),
                authenticatedUser.getName(),
                request.getContent()
        );
        return Response.ok(uuid).build();
    }
}
