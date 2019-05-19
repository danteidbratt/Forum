package se.donut.postservice.resource.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class CreateForumRequest {

    @NotNull
    @Size(min = 3, max = 31)
    private String name;

    @NotNull
    @Size(min = 1, max = 2047)
    private String description;

}
