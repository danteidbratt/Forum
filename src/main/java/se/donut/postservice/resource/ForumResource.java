package se.donut.postservice.resource;

import io.dropwizard.auth.Auth;
import se.donut.postservice.auth.AuthenticatedUser;
import se.donut.postservice.resource.request.CreateForumRequest;
import se.donut.postservice.service.ForumService;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("forums")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ForumResource {

    private final ForumService forumService;

    public ForumResource(ForumService forumService) {
        this.forumService = forumService;
    }

    @PermitAll
    @POST
    public Response createForum(
            @Auth AuthenticatedUser user,
            CreateForumRequest request
    ) {
        UUID uuid = forumService.createForum(
                user.getUuid(),
                request.getName(),
                request.getDescription()
        );
        return Response.ok(uuid).build();
    }

}
