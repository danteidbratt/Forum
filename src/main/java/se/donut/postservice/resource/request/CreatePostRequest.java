package se.donut.postservice.resource.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreatePostRequest extends CreateCommentRequest {

    private String title;
    private String link;
    private String content;

}