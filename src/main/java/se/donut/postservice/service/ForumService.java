package se.donut.postservice.service;

import lombok.extern.slf4j.Slf4j;
import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.api.ForumDTO;
import se.donut.postservice.model.domain.Forum;
import se.donut.postservice.model.domain.SortableForum;
import se.donut.postservice.model.domain.Subscription;
import se.donut.postservice.model.domain.User;
import se.donut.postservice.repository.postgresql.ForumDAO;
import se.donut.postservice.repository.postgresql.UserDAO;
import se.donut.postservice.resource.request.SortType;
import se.donut.postservice.util.DataValidator;

import java.util.*;
import java.util.stream.Collectors;

import static se.donut.postservice.exception.ExceptionType.*;

@Slf4j
public final class ForumService {

    private final UserDAO userDAO;
    private final ForumDAO forumDAO;

    public ForumService(UserDAO userDAO, ForumDAO forumDAO) {
        this.userDAO = userDAO;
        this.forumDAO = forumDAO;
    }

    public ForumDTO getForum(UUID forumUuid) {
        return getForum(forumUuid, null);
    }

    public ForumDTO getForum(UUID forumUuid, UUID userUuid) {
        Forum forum = forumDAO.getForum(forumUuid).orElseThrow(() ->
                new PostServiceException(
                        FORUM_NOT_FOUND,
                        String.format("Could not find forum with uuid %s", forumUuid)
                )
        );

        User author = userDAO.get(forum.getAuthorUuid()).orElseThrow(() ->
                new PostServiceException(
                        USER_NOT_FOUND,
                        String.format("Could not find user with uuid %s", forum.getAuthorUuid())
                )
        );

        Optional<Subscription> subscription = userUuid != null ?
                forumDAO.getSubscription(userUuid, forumUuid) : Optional.empty();

        return forum.toApiModel(author.getName(), new Date(), subscription.isPresent());
    }

    public List<ForumDTO> getAllForums(SortType sortType) {
        return getAllForums(sortType, null);
    }

    public List<ForumDTO> getAllForums(SortType sortType, UUID userUuid) {
        List<Forum> forums = forumDAO.getAllForums();
        if (forums.isEmpty()) {
            return new ArrayList<>();
        }

        Map<UUID, Subscription> subscriptions;
        if (userUuid != null) {
            subscriptions = forumDAO.getSubscriptionsByUser(userUuid)
                    .stream()
                    .collect(Collectors.toMap(Subscription::getForumUuid, s -> s));
        } else {
            subscriptions = new HashMap<>();
        }

        List<UUID> userUuids = forums.stream()
                .map(Forum::getAuthorUuid)
                .collect(Collectors.toList());

        Map<UUID, User> users = userDAO.get(userUuids);

        Date now = new Date();

        return forums.stream()
                .map(f -> new SortableForum(f, sortType))
                .sorted(sortType.getComparator())
                .map(f -> {
                    Optional<User> author = Optional.ofNullable(users.get(f.getAuthorUuid()));
                    String authorName = author.map(User::getName).orElse("[deleted]");
                    Boolean subscribed = userUuid != null && subscriptions.containsKey(f.getUuid());
                    return f.toApiModel(authorName, now, subscribed);
                })
                .collect(Collectors.toList());
    }

    public List<ForumDTO> getSubscriptions(UUID userUuid, SortType sortType) {
        List<Forum> forums = forumDAO.getSubscriptions(userUuid);
        if (forums.isEmpty()) {
            return new ArrayList<>();
        }

        List<UUID> userUuids = forums.stream()
                .map(Forum::getAuthorUuid)
                .collect(Collectors.toList());

        Map<UUID, User> users = userDAO.get(userUuids);

        Date now = new Date();

        return forums.stream()
                .map(f -> new SortableForum(f, sortType))
                .sorted(sortType.getComparator())
                .map(f -> {
                    User author = users.get(f.getAuthorUuid());
                    String authorName = author != null ? author.getName() : "[deleted]";
                    return f.toApiModel(authorName, now, true);
                })
                .collect(Collectors.toList());
    }

    public ForumDTO createForum(UUID userUuid, String userName, String name, String description) {
        DataValidator.validateForumName(name);
        description = DataValidator.validateForumDescription(description);
        Optional<Forum> forumWithSameName = forumDAO.getForumByName(name);
        if (forumWithSameName.isPresent()) {
            throw new PostServiceException(
                    FORUM_NAME_ALREADY_TAKEN,
                    String.format("Forum with name \"%s\" already exists.", name));
        }

        UUID forumUuid = UUID.randomUUID();
        Date now = new Date();
        Forum forum = new Forum(
                forumUuid,
                userUuid,
                name,
                description,
                1,
                now
        );
        Subscription subscription = new Subscription(
                userUuid,
                forumUuid
        );
        forumDAO.createForumAndSubscribe(forum, subscription);
        log.info(String.format("Forum '%s' was successfully created.", forum.getName()));

        return forum.toApiModel(userName, new Date(), true);
    }

    public void subscribe(UUID userUuid, UUID forumUuid) {
        Forum forum = forumDAO.getForum(forumUuid).orElseThrow(() -> new PostServiceException(
                FORUM_NOT_FOUND,
                String.format("Could not find forum with uuid %s.", forumUuid)
        ));

        Subscription subscription = new Subscription(userUuid, forumUuid);

        forumDAO.subscribe(subscription);
        log.info(String.format("User with uuid '%s' was successfully " +
                        "subscribed to forum '%s'.", userUuid, forum.getName())
        );
    }

    public void unsubscribe(UUID userUuid, UUID forumUuid) {
        forumDAO.unsubscribe(userUuid, forumUuid);
        log.info(String.format("User with uuid '%s' was successfully " +
                "unsubscribed from forum with uuid '%s'.", userUuid, forumUuid)
        );
    }
}
