package se.donut.postservice.resource.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CreateForumRequest {

    private UUID userUuid;
    private String name;
    private String description;

}
