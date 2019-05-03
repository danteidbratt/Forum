package se.donut.postservice.repository;

import se.donut.postservice.model.domain.Forum;

import java.util.List;
import java.util.UUID;


public interface ForumAccessor extends EntityAccessor<Forum> {

    List<Forum> get();

    List<Forum> getByUser(UUID userUuid);

}
