package se.donut.postservice.service;

import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.Direction;
import se.donut.postservice.model.api.PostDTO;
import se.donut.postservice.model.domain.*;
import se.donut.postservice.model.domain.SortablePost;
import se.donut.postservice.repository.postgresql.ForumDAO;
import se.donut.postservice.repository.postgresql.PostDAO;
import se.donut.postservice.repository.postgresql.UserDAO;
import se.donut.postservice.resource.request.SortType;

import java.util.*;
import java.util.stream.Collectors;

import static se.donut.postservice.exception.ExceptionType.FORUM_NOT_FOUND;
import static se.donut.postservice.exception.ExceptionType.POST_NOT_FOUND;

public class PostService {

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
                .orElseThrow(() -> new PostServiceException(POST_NOT_FOUND));

        Optional<User> author = userDAO.get(post.getAuthorUuid());

        Optional<Vote> userVote = userUuid != null ? postDAO.getVote(userUuid, postUuid) : Optional.empty();

        return post.toApiModel(
                author.map(User::getName).orElse(null),
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

    public List<PostDTO> getByAuthor(UUID authorUuid) {
        return getByAuthor(authorUuid, null);
    }

    public List<PostDTO> getByAuthor(UUID authorUuid, UUID userUuid) {
        List<Post> posts = postDAO.getByAuthor(authorUuid);
        return buildApiModels(posts, SortType.NEW, userUuid);
    }

    public List<PostDTO> getLiked(UUID likerUuid) {
        List<Post> posts = postDAO.getLiked(likerUuid);
        return buildApiModels(posts, SortType.NEW, null);

    }

    public List<PostDTO> getLiked(UUID likerUuid, UUID userUuid) {
        List<Post> posts = postDAO.getLiked(likerUuid);
        return buildApiModels(posts, SortType.NEW, userUuid);
    }

    public UUID createPost(UUID forumUuid, UUID authorUuid, String title, String content) {
        Forum forum = forumDAO.get(forumUuid).orElseThrow(() -> new PostServiceException(FORUM_NOT_FOUND));
        UUID postUuid = UUID.randomUUID();
        Post post = new Post(
                postUuid,
                authorUuid,
                content,
                0,
                forum.getUuid(),
                title,
                new Date()
        );
        postDAO.create(post);
        return postUuid;
    }

    public void vote(UUID forumUuid, UUID postUuid, UUID userUuid, Direction direction) {
        Vote vote = new Vote(postUuid, forumUuid, userUuid, direction);
        postDAO.voteAndUpdateScore(vote);
    }

    public void deleteVote(UUID userUuid, UUID postUuid) {
        postDAO.getVote(userUuid, postUuid).ifPresent(postDAO::deleteVoteAndUpdateScore);
    }

    private List<PostDTO> buildApiModels(List<Post> posts, SortType sortType, UUID userUuid) {
        if (posts.isEmpty()) {
            return new ArrayList<>();
        }

        List<UUID> postUuids = posts.stream()
                .map(Post::getUuid)
                .collect(Collectors.toList());

        Map<UUID, Vote> myVotes;
        if (userUuid != null) {
            myVotes = postDAO.getVotes(userUuid, postUuids).stream()
                    .collect(Collectors.toMap(
                            Vote::getTargetUuid,
                            x -> x
                    ));
        } else {
            myVotes = new HashMap<>();
        }

        List<UUID> authorUuids = posts.stream()
                .map(Post::getAuthorUuid)
                .collect(Collectors.toList());

        Map<UUID, User> authors = userDAO.get(authorUuids);

        return posts.stream()
                .map(p -> new SortablePost(p, sortType))
                .sorted(sortType.getComparator())
                .map(p -> {
                    User author = authors.get(p.getAuthorUuid());
                    Vote vote = myVotes.get(p.getUuid());
                    return p.toApiModel(
                            author != null ? author.getName() : null,
                            vote != null ? vote.getDirection() : null
                    );
                })
                .collect(Collectors.toList());
    }
}
