package se.donut.postservice.service;

import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.api.ForumDTO;
import se.donut.postservice.model.domain.Forum;
import se.donut.postservice.model.domain.SortableForum;
import se.donut.postservice.model.domain.Subscription;
import se.donut.postservice.model.domain.User;
import se.donut.postservice.repository.postgresql.ForumDAO;
import se.donut.postservice.repository.postgresql.UserDAO;
import se.donut.postservice.resource.request.SortType;

import java.util.*;
import java.util.stream.Collectors;

import static se.donut.postservice.exception.ExceptionType.FORUM_NAME_ALREADY_TAKEN;
import static se.donut.postservice.exception.ExceptionType.FORUM_NOT_FOUND;

public class ForumService {

    private final UserDAO userDAO;
    private final ForumDAO forumDAO;

    public ForumService(UserDAO userDAO, ForumDAO forumDAO) {
        this.userDAO = userDAO;
        this.forumDAO = forumDAO;
    }

    public List<ForumDTO> getAllForums(SortType sortType) {
        return getAllForums(sortType, null);
    }

    public List<ForumDTO> getAllForums(SortType sortType, UUID userUuid) {
        Map<UUID, Subscription> subscriptions;

        if (userUuid != null) {
            subscriptions = forumDAO.getSubscriptionsByUser(userUuid)
                    .stream()
                    .collect(Collectors.toMap(Subscription::getForumUuid, s -> s));
        } else {
            subscriptions = new HashMap<>();
        }

        List<Forum> forums = forumDAO.getAllForums();

        List<UUID> userUuids = forums.stream()
                .map(Forum::getAuthorUuid)
                .collect(Collectors.toList());

        Map<UUID, User> users = userDAO.get(userUuids);

        return forums.stream()
                .map(f -> new SortableForum(f, sortType))
                .sorted(sortType.getComparator())
                .map(f -> f.toApiModel(
                        users.get(f.getAuthorUuid()).getName(),
                        userUuid != null ? subscriptions.containsKey(f.getUuid()) : null))
                .collect(Collectors.toList());
    }

    public List<ForumDTO> getSubscriptions(UUID userUuid, SortType sortType) {
        List<Forum> forums = forumDAO.getSubscriptions(userUuid);

        List<UUID> userUuids = forums.stream()
                .map(Forum::getAuthorUuid)
                .collect(Collectors.toList());

        Map<UUID, User> users = userDAO.get(userUuids);

        return forums.stream()
                .map(f -> new SortableForum(f, sortType))
                .sorted(sortType.getComparator())
                .map(f -> f.toApiModel(users.get(f.getAuthorUuid()).getName(), true))
                .collect(Collectors.toList());
    }

    public UUID createForum(UUID userUuid, String name, String description) {
        Optional<Forum> forumWithSameName = forumDAO.getForumByName(name);
        if (forumWithSameName.isPresent()) {
            throw new PostServiceException(FORUM_NAME_ALREADY_TAKEN);
        }

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
        forumDAO.createForum(forum);
        forumDAO.subscribe(subscription);

        return forumUuid;
    }

    public void subscribe(UUID userUuid, UUID forumUuid) {
        forumDAO.getForum(forumUuid).orElseThrow(() -> new PostServiceException(FORUM_NOT_FOUND));

        Subscription subscription = new Subscription(userUuid, forumUuid);

        forumDAO.subscribe(subscription);
    }

    public void unsubscribe(UUID userUuid, UUID forumUuid) {
        forumDAO.unsubscribe(userUuid, forumUuid);
    }
}
