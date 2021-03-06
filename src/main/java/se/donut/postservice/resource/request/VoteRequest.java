package se.donut.postservice.resource.request;

import lombok.Getter;
import lombok.Setter;
import se.donut.postservice.model.Direction;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public final class VoteRequest {

    @NotNull
    Direction direction;

}
