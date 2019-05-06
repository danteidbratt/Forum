package se.donut.postservice.service;

import se.donut.postservice.exception.ExceptionType;
import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.api.ForumDTO;
import se.donut.postservice.model.domain.Forum;
import se.donut.postservice.model.domain.Subscription;
import se.donut.postservice.model.domain.User;
import se.donut.postservice.repository.postgresql.ForumDAO;
import se.donut.postservice.repository.postgresql.SubscriptionDAO;
import se.donut.postservice.repository.postgresql.UserDAO;
import se.donut.postservice.resource.request.SortType;

import java.util.*;
import java.util.stream.Collectors;

public class ForumService {

    private final UserDAO userDAO;
    private final ForumDAO forumDAO;
    private final SubscriptionDAO subscriptionDAO;

    public ForumService(UserDAO userDAO, ForumDAO forumDAO, SubscriptionDAO subscriptionDAO) {
        this.userDAO = userDAO;
        this.forumDAO = forumDAO;
        this.subscriptionDAO = subscriptionDAO;
    }

    public List<ForumDTO> getAllForums(SortType sortType) {
        List<Forum> forums = forumDAO.getAll();
        List<UUID> userUuids = forums.stream()
                .map(Forum::getAuthorUuid)
                .collect(Collectors.toList());
        Map<UUID, User> users = userDAO.get(userUuids);
        return forums.stream()
                .sorted(sortType.getComparator())
                .map(f -> f.toApiModel(users.get(f.getAuthorUuid()).getName()))
                .collect(Collectors.toList());
    }

    public List<ForumDTO> getAllForums(UUID userUuid, SortType sortType) {
        Map<UUID, Subscription> subscriptions = subscriptionDAO.getByUser(userUuid);
        List<Forum> forums = forumDAO.getAll();
        List<UUID> userUuids = forums.stream()
                .map(Forum::getAuthorUuid)
                .collect(Collectors.toList());
        Map<UUID, User> users = userDAO.get(userUuids);
        return forums.stream()
                .sorted(sortType.getComparator())
                .map(f -> f.toApiModel(
                        users.get(f.getAuthorUuid()).getName(),
                        subscriptions.containsKey(f.getUuid())))
                .collect(Collectors.toList());
    }

    public List<ForumDTO> getSubscriptions(UUID userUuid, SortType sortType) {
        Map<UUID, Subscription> subscriptions = subscriptionDAO.getByUser(userUuid);
        List<UUID> forumUuids = subscriptions.values().stream()
                .map(Subscription::getForumUuid)
                .collect(Collectors.toList());
        List<Forum> forums = forumDAO.get(forumUuids);
        List<UUID> userUuids = forums.stream()
                .map(Forum::getAuthorUuid)
                .collect(Collectors.toList());
        Map<UUID, User> users = userDAO.get(userUuids);

        return forumDAO.get(forumUuids).stream()
                .sorted(sortType.getComparator())
                .map(f -> f.toApiModel(users.get(f.getAuthorUuid()).getName(), true))
                .collect(Collectors.toList());
    }

    public UUID createForum(UUID userUuid, String name, String description) {
        UUID forumUuid = UUID.randomUUID();
        Date now = new Date();
        Forum forum = new Forum(
                forumUuid,
                userUuid,
                name,
                description,
                0,
                now
        );
        Subscription subscription = new Subscription(
                userUuid,
                forumUuid
        );
        forumDAO.create(forum);
        subscriptionDAO.create(subscription);

        return forumUuid;
    }

    public void subscribe(UUID userUuid, UUID forumUuid) {
        forumDAO.get(forumUuid).orElseThrow(() -> new PostServiceException(ExceptionType.FORUM_NOT_FOUND));

        Optional<Subscription> currentSubscription = subscriptionDAO.getByUserAndForum(userUuid, forumUuid);

        if (currentSubscription.isPresent()) {
            return;
        }

        Subscription subscription = new Subscription(
                userUuid,
                forumUuid
        );

        subscriptionDAO.create(subscription);
    }

    public void unsubscribe(UUID userUuid, UUID forumUuid) {
        Optional<Subscription> currentSubscription = subscriptionDAO.getByUserAndForum(userUuid, forumUuid);
        currentSubscription.ifPresent(subscriptionDAO::delete);
    }
}
