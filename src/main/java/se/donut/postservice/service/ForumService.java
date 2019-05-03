package se.donut.postservice.service;

import se.donut.postservice.exception.ExceptionType;
import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.api.ForumDTO;
import se.donut.postservice.model.domain.Forum;
import se.donut.postservice.model.domain.Subscription;
import se.donut.postservice.repository.ForumAccessor;
import se.donut.postservice.repository.SubscriptionAccessor;
import se.donut.postservice.resource.request.SortType;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ForumService {

    private final ForumAccessor forumAccessor;
    private final SubscriptionAccessor subscriptionAccessor;

    public ForumService(ForumAccessor ForumAccessor, SubscriptionAccessor subscriptionAccessor) {
        this.forumAccessor = ForumAccessor;
        this.subscriptionAccessor = subscriptionAccessor;
    }

    public List<ForumDTO> getForums(SortType sortType) {
        return forumAccessor.get()
                .stream()
                .sorted(sortType.getComparator())
                .map(Forum::toApiModel)
                .collect(Collectors.toList());
    }

    public List<ForumDTO> getSubscriptions(UUID userUuid) {
        return forumAccessor.getByUser(userUuid)
                .stream()
                .map(Forum::toApiModel)
                .collect(Collectors.toList());
    }

    public UUID createForum(UUID userUuid, String username, String name, String description) {
        UUID forumUuid = UUID.randomUUID();
        Date now = new Date();
        Forum forum = new Forum(
                forumUuid,
                userUuid,
                username,
                name,
                description,
                0,
                now
        );
        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                userUuid,
                forumUuid,
                now
        );
        forumAccessor.create(forum);
        subscriptionAccessor.create(subscription);

        return forumUuid;
    }

    public UUID subscribe(UUID userUuid, UUID forumUuid) {
        forumAccessor.get(forumUuid).orElseThrow(() -> new PostServiceException(ExceptionType.FORUM_NOT_FOUND));

        Optional<Subscription> currentSubscription = subscriptionAccessor.getByUserAndForum(userUuid, forumUuid);
        if(currentSubscription.isPresent()) {
            throw new PostServiceException(ExceptionType.SUBSCRIPTION_ALREADY_EXISTS);
        }

        UUID subscriptionUuid = UUID.randomUUID();
        Subscription subscription = new Subscription(
                subscriptionUuid,
                userUuid,
                forumUuid,
                new Date()
        );

        subscriptionAccessor.create(subscription);
        return subscriptionUuid;
    }

    public void unsubscribe(UUID userUuid, UUID forumUuid) {
        Optional<Subscription> currentSubscription = subscriptionAccessor.getByUserAndForum(userUuid, forumUuid);
        currentSubscription.ifPresent(subscriptionAccessor::delete);
    }
}
