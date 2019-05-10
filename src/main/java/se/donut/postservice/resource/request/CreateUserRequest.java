package se.donut.postservice.resource.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {

    @NotNull
    @Size(max = 32)
    private String username;

    @NotNull
    @Size(max = 256)
    private String password;

}
