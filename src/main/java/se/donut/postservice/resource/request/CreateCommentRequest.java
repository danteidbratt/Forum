package se.donut.postservice.resource.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@NoArgsConstructor
public class CreateCommentRequest {

    @NotNull
    private UUID parentUuid;

    @NotNull
    @Size(min = 1, max = 511)
    private String content;

}
