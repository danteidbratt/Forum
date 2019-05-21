package se.donut.postservice.service;

import lombok.extern.slf4j.Slf4j;
import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.Direction;
import se.donut.postservice.model.api.PostDTO;
import se.donut.postservice.model.domain.*;
import se.donut.postservice.repository.postgresql.ForumDAO;
import se.donut.postservice.repository.postgresql.PostDAO;
import se.donut.postservice.repository.postgresql.UserDAO;
import se.donut.postservice.resource.request.SortType;
import se.donut.postservice.util.DataValidator;

import java.util.*;
import java.util.stream.Collectors;

import static se.donut.postservice.exception.ExceptionType.FORUM_NOT_FOUND;
import static se.donut.postservice.exception.ExceptionType.POST_NOT_FOUND;

@Slf4j
public final class PostService {

    private final UserDAO userDAO;
    private final PostDAO postDAO;
    private final ForumDAO forumDAO;

    public PostService(UserDAO userDAO, PostDAO postAccessor, ForumDAO forumDAO) {
        this.userDAO = userDAO;
        this.postDAO = postAccessor;
        this.forumDAO = forumDAO;
    }

    public PostDTO getPost(UUID postUuid) {
        return getPost(postUuid, null);
    }

    public PostDTO getPost(UUID postUuid, UUID userUuid) {
        Post post = postDAO.get(postUuid)
                .orElseThrow(() -> new PostServiceException(
                        POST_NOT_FOUND,
                        String.format("Could not find post with uuid %s.", postUuid)
                ));

        Optional<User> author = userDAO.get(post.getAuthorUuid());

        Optional<Vote> userVote = userUuid != null ? postDAO.getVote(userUuid, postUuid) : Optional.empty();

        Optional<Forum> forum = forumDAO.getForum(post.getForumUuid());

        return post.toApiModel(
                forum.map(Forum::getName).orElse(null),
                author.map(User::getName).orElse(null),
                new Date(),
                userVote.map(Vote::getDirection).orElse(null)
        );
    }

    public List<PostDTO> getByForum(UUID forumUuid, SortType sortType) {
        return getByForum(forumUuid, sortType, null);
    }

    public List<PostDTO> getByForum(UUID forumUuid, SortType sortType, UUID userUuid) {
        List<Post> posts = postDAO.getByForum(forumUuid);
        return buildApiModels(posts, sortType, userUuid);
    }

    public List<PostDTO> getByAuthor(UUID authorUuid, SortType sortType) {
        return getByAuthor(authorUuid, sortType, null);
    }

    public List<PostDTO> getByAuthor(UUID authorUuid, SortType sortType, UUID userUuid) {
        List<Post> posts = postDAO.getByAuthor(authorUuid);
        return buildApiModels(posts, sortType, userUuid);
    }

    public List<PostDTO> getByLikes(UUID userUuid, SortType sortType) {
        return getByLikes(userUuid, sortType, null);
    }

    public List<PostDTO> getByLikes(UUID likerUuid, SortType sortType, UUID userUuid) {
        List<Post> posts = postDAO.getByLikes(likerUuid);
        return buildApiModels(posts, sortType, userUuid);
    }

    public List<PostDTO> getBySubscriptions(UUID userUuid, SortType sortType) {
        List<Post> posts = postDAO.getBySubscriptions(userUuid);
        return buildApiModels(posts, sortType, userUuid);
    }

    public List<PostDTO> getAll(SortType sortType) {
        return getAll(sortType, null);
    }

    public List<PostDTO> getAll(SortType sortType, UUID userUuid) {
        List<Post> posts = postDAO.getAll();
        return buildApiModels(posts, sortType, userUuid);
    }

    public PostDTO createPost(UUID forumUuid, UUID authorUuid, String authorName, String title, String content) {
        title = DataValidator.validatePostTitle(title);
        content = DataValidator.validatePostContent(content);
        Forum forum = forumDAO.getForum(forumUuid).orElseThrow(() -> new PostServiceException(
                FORUM_NOT_FOUND,
                String.format("Could not find forum with uuid %s.", forumUuid)));

        UUID postUuid = UUID.randomUUID();
        Post post = new Post(
                postUuid,
                authorUuid,
                content,
                0,
                forumUuid,
                title,
                new Date(),
                0);
        postDAO.create(post);
        log.info("Post was successfully created.");
        return post.toApiModel(forum.getName(), authorName, new Date(), null);
    }

    public void vote(UUID postUuid, UUID userUuid, Direction direction) {
        Vote vote = new Vote(postUuid, userUuid, direction);
        postDAO.voteAndUpdateScore(vote);
    }

    public void deleteVote(UUID userUuid, UUID postUuid) {
        postDAO.getVote(userUuid, postUuid).ifPresent(postDAO::deleteVoteAndUpdateScore);
    }

    private List<PostDTO> buildApiModels(List<Post> posts, SortType sortType, UUID userUuid) {
        if (posts.isEmpty()) {
            return new ArrayList<>();
        }

        List<UUID> forumUuids = posts.stream()
                .map(Post::getForumUuid)
                .collect(Collectors.toList());
        Map<UUID, Forum> forums = forumDAO.getForums(forumUuids)
                .stream()
                .collect(Collectors.toMap(AbstractEntity::getUuid, f -> f));

        Map<UUID, Vote> myVotes = userUuid != null ?
                postDAO.getVotes(userUuid, forumUuids).stream()
                        .collect(Collectors.toMap(
                                Vote::getTargetUuid,
                                x -> x
                        ))
                : new HashMap<>();

        List<UUID> authorUuids = posts.stream()
                .map(Post::getAuthorUuid)
                .collect(Collectors.toList());

        Map<UUID, User> authors = userDAO.get(authorUuids);

        Date now = new Date();

        return posts.stream()
                .map(p -> new SortablePost(p, sortType))
                .sorted(sortType.getComparator())
                .map(p -> {
                    String forumName = forums.get(p.getForumUuid()).getName();
                    String authorName = authors.get(p.getAuthorUuid()).getName();
                    Optional<Vote> vote = Optional.ofNullable(myVotes.get(p.getUuid()));
                    Direction voteDirection = vote.map(Vote::getDirection).orElse(null);
                    return p.toApiModel(forumName, authorName, now, voteDirection);
                })
                .collect(Collectors.toList());
    }
}
