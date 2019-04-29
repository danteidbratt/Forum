package se.donut.postservice.resource;

import se.donut.postservice.model.api.CommentDTO;
import se.donut.postservice.resource.request.CreatePostRequest;
import se.donut.postservice.service.PostService;

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

    @POST
    public Response createPost(
            @PathParam("forumUuid") UUID forumUuid,
            CreatePostRequest request
    ) {
        UUID uuid = postService.createPost(
                forumUuid,
                request.getAuthorUuid(),
                request.getAuthorName(),
                request.getTitle(),
                request.getLink(),
                request.getContent()
        );
        return Response.ok(uuid).build();
    }
}
