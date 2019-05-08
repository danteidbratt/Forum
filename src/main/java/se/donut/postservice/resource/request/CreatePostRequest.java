package se.donut.postservice.resource.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreatePostRequest {

    private String title;
    private String content;

}