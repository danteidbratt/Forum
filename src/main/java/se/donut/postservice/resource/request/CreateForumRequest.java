package se.donut.postservice.resource.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class CreateForumRequest {

    @Size(max = 32)
    private String name;
    @Size(max = 256)
    private String description;

}
