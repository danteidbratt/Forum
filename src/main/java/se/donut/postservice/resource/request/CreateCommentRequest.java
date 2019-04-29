package se.donut.postservice.resource.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class CreateCommentRequest extends CreateEntryRequest {

    private UUID parentEntryUuid;

}
