package se.donut.postservice.resource.request;

import lombok.*;

@Setter
@Getter
public class CreatePostRequest extends CreateCommentRequest {

    private String title;
    private String link;

}
