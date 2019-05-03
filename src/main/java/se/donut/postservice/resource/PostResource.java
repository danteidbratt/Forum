package se.donut.postservice.resource;

import io.dropwizard.auth.Auth;
import se.donut.postservice.auth.AuthenticatedUser;
import se.donut.postservice.model.api.PostDTO;
import se.donut.postservice.resource.request.SortType;
import se.donut.postservice.resource.request.CreatePostRequest;
import se.donut.postservice.service.PostService;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
                authenticatedUser.getName(),
                request.getTitle(),
                request.getLink(),
                request.getContent()
        );
        return Response.ok(uuid).build();
    }

    @Path("{postUuid}")
    @GET
    public PostDTO getPost(
            @PathParam("postUuid") UUID postUuid,
            @DefaultValue("TOP") @QueryParam("sort") SortType sortType) {
        return postService.getPost(postUuid, sortType);
    }

}
