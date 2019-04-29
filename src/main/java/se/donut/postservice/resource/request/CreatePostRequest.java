package se.donut.postservice.resource.request;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
public class CreatePostRequest extends CreateEntryRequest {

    private UUID forumUuid;
    private String title;
    private String link;

}
