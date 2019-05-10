package se.donut.postservice.resource.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class CreateForumRequest {

    @NotNull
    @Size(max = 32)
    private String name;

    @NotNull
    @Size(max = 256)
    private String description;

}
