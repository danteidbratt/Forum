package se.donut.postservice.resource.request;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class CreateCommentRequest {

    private String content;

}
