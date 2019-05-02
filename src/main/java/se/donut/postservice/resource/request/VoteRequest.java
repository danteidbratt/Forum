package se.donut.postservice.resource.request;

import lombok.Getter;
import lombok.Setter;
import se.donut.postservice.model.Direction;

@Getter
@Setter
public class VoteRequest {

    Direction direction;

}
