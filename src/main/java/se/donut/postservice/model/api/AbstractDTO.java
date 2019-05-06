package se.donut.postservice.model.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.UUID;

@Getter
@AllArgsConstructor
abstract class AbstractDTO {

    private final UUID uuid;
    private final Date createdAt;

}