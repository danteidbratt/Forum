package se.donut.postservice.resource.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Setter
@Getter
public class CreatePostRequest {

    @Size(max = 128)
    private String title;
    @Size(max = 512)
    private String content;

}