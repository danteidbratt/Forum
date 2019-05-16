package se.donut.postservice.resource.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter
@Getter
public class CreatePostRequest {

    @NotNull
    @Size(min = 1, max = 255)
    private String title;

    @NotNull
    @Size(max = 511)
    private String content;

}