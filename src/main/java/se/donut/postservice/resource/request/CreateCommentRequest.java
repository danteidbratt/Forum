package se.donut.postservice.resource.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class CreateCommentRequest {

    @NotNull
    @Size(max = 512)
    private String content;

}
