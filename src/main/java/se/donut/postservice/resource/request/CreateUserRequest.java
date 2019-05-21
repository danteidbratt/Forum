package se.donut.postservice.resource.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Getter
@Setter
public final class CreateUserRequest {

    @NotNull
    @Size(min = 3, max = 31)
    private String username;

    @NotNull
    @Size(min = 3, max = 255)
    private String password;

}
