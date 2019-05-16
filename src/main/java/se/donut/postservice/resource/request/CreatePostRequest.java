package se.donut.postservice.resource.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Setter
@Getter
public class CreatePostRequest {

    @NotNull
    @Size(min = 1, max = 128)
    private String title;

    @NotNull
    @Size(max = 512)
    private String content;

}