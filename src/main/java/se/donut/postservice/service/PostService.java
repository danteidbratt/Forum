package se.donut.postservice.service;

import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.Direction;
import se.donut.postservice.model.api.PostDTO;
import se.donut.postservice.model.domain.Forum;
import se.donut.postservice.model.domain.Post;
import se.donut.postservice.model.domain.User;
import se.donut.postservice.model.domain.Vote;
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
        Post post = postDAO.get(postUuid)
                .orElseThrow(() -> new PostServiceException(POST_NOT_FOUND));
        Optional<User> author = userDAO.get(post.getAuthorUuid());

        if (author.isPresent()) {
            return post.toApiModel(author.get().getName());
        }

        return post.toApiModel();
    }

    public List<PostDTO> getByForum(UUID forumUuid, SortType sortType) {
        List<Post> posts = postDAO.getByForum(forumUuid);
        List<UUID> userUuids = posts.stream()
                .map(Post::getAuthorUuid)
                .collect(Collectors.toList());
        Map<UUID, User> users = userDAO.get(userUuids);

        return posts.stream()
                .sorted(sortType.getComparator())
                .map(p -> {
                    User user = users.get(p.getAuthorUuid());
                    return p.toApiModel(
                            user != null ? user.getName() : null
                    );
                })
                .collect(Collectors.toList());
    }

    public List<PostDTO> getByForum(UUID userUuid, UUID forumUuid, SortType sortType) {
        List<Post> posts = postDAO.getByForum(forumUuid);
        Map<UUID, Vote> votes = postDAO.getVotes(userUuid, forumUuid);
        List<UUID> userUuids = posts.stream()
                .map(Post::getAuthorUuid)
                .collect(Collectors.toList());
        Map<UUID, User> users = userDAO.get(userUuids);

        return posts.stream()
                .sorted(sortType.getComparator())
                .map(p -> {
                    User user = users.get(p.getAuthorUuid());
                    Vote vote = votes.get(p.getUuid());
                    return p.toApiModel(
                            user != null ? user.getName() : null,
                            vote != null ? vote.getDirection() : null
                    );
                })
                .collect(Collectors.toList());
    }

    public UUID createPost(UUID forumUuid, UUID authorUuid, String authorName, String title, String link, String content) {
        Forum forum = forumDAO.get(forumUuid).orElseThrow(() -> new PostServiceException(FORUM_NOT_FOUND));
        UUID postUuid = UUID.randomUUID();
        Post post = new Post(
                postUuid,
                authorUuid,
                content,
                0,
                forum.getUuid(),
                title,
                link,
                new Date()
        );
        postDAO.create(post);
        return postUuid;
    }

    public void vote(UUID forumUuid, UUID postUuid, UUID userUuid, Direction direction) {
        Vote vote = new Vote(postUuid, forumUuid, userUuid, direction);
        postDAO.vote(vote);
    }

    public void deleteVote(UUID userUuid, UUID postUuid) {
        postDAO.deleteVote(userUuid, postUuid);
    }

}
