package se.donut.postservice.resource;

import se.donut.postservice.service.UserService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("login")
public class AuthResource {

    private final UserService userService;

    public AuthResource(UserService userService) {
        this.userService = userService;
    }

    @GET
    public Response login() {
        return Response.ok().build();
    }

}
