package se.donut.postservice.resource.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateForumRequest {

    private String name;
    private String description;

}
