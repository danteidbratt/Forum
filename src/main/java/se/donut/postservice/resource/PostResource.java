package se.donut.postservice.resource;

import se.donut.postservice.model.api.CommentDTO;
import se.donut.postservice.resource.request.CreatePostRequest;
import se.donut.postservice.resource.request.CommentRequest;
import se.donut.postservice.service.PostService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("forum/{forumUuid}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private final PostService postService;

    public PostResource(PostService postService) {
        this.postService = postService;
    }

    @POST
    public Response createPost(CreatePostRequest request) {
        UUID uuid = postService.createPost(
                request.getAuthorUuid(),
                request.getAuthorName(),
                request.getForumUuid(),
                request.getTitle(),
                request.getLink(),
                request.getContent()
        );
        return Response.ok(uuid).build();
    }

    @Path("{postUuid}/comments")
    @GET
    public Response getComments(
            @PathParam("forumUuid") UUID forumUuid,
            @PathParam("postUuid") UUID postUuid,
            CommentRequest request
    ) {
        List<CommentDTO> comments = postService.getCommentsChildren(forumUuid, postUuid, request.getPath());
        return Response.ok(comments).build();
    }

    @Path("{postUuid}/comments")
    @POST
    public Response comment(
            @PathParam("forumUuid") UUID forumUuid,
            @PathParam("postUuid") UUID postUuid,
            CommentRequest request
    ) {
        List<CommentDTO> comments = postService.getCommentsChildren(forumUuid, postUuid, request.getPath());
        return Response.ok(comments).build();
    }



}
