package se.donut.postservice.resource;

import se.donut.postservice.model.api.CommentDTO;
import se.donut.postservice.resource.request.CreateCommentRequest;
import se.donut.postservice.service.CommentService;

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

    @POST
    public Response commentOnPost(
            @PathParam("forumUuid") UUID forumUuid,
            @PathParam("postUuid") UUID postUuid,
            CreateCommentRequest request) {
        UUID uuid = createComment(postUuid, postUuid, request);
        return Response.ok(uuid).build();
    }

    @Path("{commentUuid}")
    @POST
    public Response commentOnComment(
            @PathParam("forumUuid") UUID forumUuid,
            @PathParam("postUuid") UUID postUuid,
            @PathParam("commentUuid") UUID commentUuid,
            CreateCommentRequest request) {
        UUID uuid = createComment(postUuid, commentUuid, request);
        return Response.ok(uuid).build();
    }

    private UUID createComment(UUID postUuid, UUID parentUuid, CreateCommentRequest request) {
        return commentService.createComment(
                postUuid,
                parentUuid,
                request.getAuthorUuid(),
                request.getAuthorName(),
                request.getContent()
        );
    }
}
