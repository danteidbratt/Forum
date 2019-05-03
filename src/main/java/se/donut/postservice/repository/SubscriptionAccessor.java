package se.donut.postservice.repository;

import se.donut.postservice.model.domain.Subscription;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionAccessor extends EntityAccessor<Subscription> {

    Optional<Subscription> getByUserAndForum(UUID userUuid, UUID forumUuid);

}
