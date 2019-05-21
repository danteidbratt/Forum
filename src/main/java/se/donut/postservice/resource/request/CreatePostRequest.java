package se.donut.postservice.resource.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter
@Getter
public final class CreatePostRequest {

    @NotNull
    @Size(min = 1, max = 255)
    private String title;

    @NotNull
    @Size(max = 2047)
    private String content;

}