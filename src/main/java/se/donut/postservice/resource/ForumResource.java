package se.donut.postservice.resource;

import io.dropwizard.auth.Auth;
import se.donut.postservice.auth.AuthenticatedUser;
import se.donut.postservice.model.api.ForumDTO;
import se.donut.postservice.resource.request.CreateForumRequest;
import se.donut.postservice.resource.request.SortType;
import se.donut.postservice.service.ForumService;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
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

    @Path("guest")
    @GET
    public List<ForumDTO> getForumsAsGuest(
            @DefaultValue("25") @QueryParam("pageSize") int pageSize,
            @DefaultValue("0") @QueryParam("page") int page,
            @DefaultValue("TOP") @QueryParam("sort") SortType sortType
    ) {
        return forumService.getAllForums(sortType);
    }

    @PermitAll
    @GET
    public List<ForumDTO> getForums(
            @Auth AuthenticatedUser authenticatedUser,
            @DefaultValue("25") @QueryParam("pageSize") int pageSize,
            @DefaultValue("0") @QueryParam("page") int page,
            @DefaultValue("TOP") @QueryParam("sort") SortType sortType
    ) {
        return forumService.getAllForums(sortType, authenticatedUser.getUuid());
    }

    @PermitAll
    @POST
    public UUID createForum(
            @Auth AuthenticatedUser user,
            @Valid CreateForumRequest request
    ) {
        return forumService.createForum(
                user.getUuid(),
                request.getName(),
                request.getDescription()
        );
    }

    @PermitAll
    @Path("{forumUuid}/subscriptions")
    @GET
    public List<ForumDTO> getSubscriptions(
            @Auth AuthenticatedUser authenticatedUser,
            @DefaultValue("TOP") @QueryParam("sort") SortType sortType
    ) {
        return forumService.getSubscriptions(authenticatedUser.getUuid(), sortType);
    }

    @PermitAll
    @Path("{forumUuid}/subscriptions")
    @POST
    public Response subscribe(
            @Auth AuthenticatedUser authenticatedUser,
            @PathParam("forumUuid") UUID forumUuid
    ) {
        forumService.subscribe(authenticatedUser.getUuid(), forumUuid);
        return Response.ok().build();
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
