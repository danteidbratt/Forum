package se.donut.postservice.resource;

import io.dropwizard.auth.Auth;
import se.donut.postservice.auth.AuthenticatedUser;
import se.donut.postservice.model.api.ForumDTO;
import se.donut.postservice.resource.request.CreateForumRequest;
import se.donut.postservice.resource.request.SortType;
import se.donut.postservice.service.ForumService;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("forums")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ForumResource {

    private final ForumService forumService;

    public ForumResource(ForumService forumService) {
        this.forumService = forumService;
    }

    @GET
    public List<ForumDTO> getForums(
            @DefaultValue("25") @QueryParam("pageSize") int pageSize,
            @DefaultValue("0") @QueryParam("page") int page,
            @DefaultValue("TOP") @QueryParam("sort") SortType sortType
    ) {
        return forumService.getForums(sortType);
    }

    @PermitAll
    @POST
    public Response createForum(
            @Auth AuthenticatedUser user,
            CreateForumRequest request
    ) {
        UUID uuid = forumService.createForum(
                user.getUuid(),
                user.getName(),
                request.getName(),
                request.getDescription()
        );
        return Response.ok(uuid).build();
    }

    @PermitAll
    @Path("{forumUuid}/subscriptions")
    @GET
    public List<ForumDTO> getSubscriptions(@Auth AuthenticatedUser authenticatedUser) {
        return forumService.getSubscriptions(authenticatedUser.getUuid());
    }

    @PermitAll
    @Path("{forumUuid}/subscriptions")
    @POST
    public UUID subscribe(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("forumUuid") UUID forumUuid
    ) {
        return forumService.subscribe(authenticatedUser.getUuid(), forumUuid);
    }

    @PermitAll
    @Path("{forumUuid}/subscriptions")
    @DELETE
    public Response unsubscribe(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("forumUuid") UUID forumUuid
    ) {
        forumService.unsubscribe(authenticatedUser.getUuid(), forumUuid);
        return Response.ok().build();
    }

}
