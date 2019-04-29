package se.donut.postservice.service;

import se.donut.postservice.model.api.CommentDTO;
import se.donut.postservice.model.domain.Comment;
import se.donut.postservice.model.domain.Post;
import se.donut.postservice.repository.CommentAccessor;
import se.donut.postservice.repository.PostAccessor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PostService {

    private final PostAccessor postAccessor;
    private final CommentAccessor commentAccessor;

    public PostService(PostAccessor postAccessor, CommentAccessor commentAccessor) {
        this.postAccessor = postAccessor;
        this.commentAccessor = commentAccessor;
    }

    public List<CommentDTO> getCommentsChildren(UUID forumUuid, UUID postUuid, List<UUID> path) {
        List<Comment> comments = commentAccessor.getComments(forumUuid, postUuid, path);
        List<CommentDTO> commentDTOs = comments.stream()
                .map(Comment::toApiModel)
                .collect(Collectors.toList());
        return commentDTOs;
    }

    public UUID createPost(UUID authorUuid, String authorName, UUID forumUuid, String title, String link, String content) {
        UUID postUuid = UUID.randomUUID();
        Post post = new Post(
                postUuid,
                authorUuid,
                authorName,
                content,
                0,
                Instant.now(),
                false,
                forumUuid,
                title,
                link
        );
        postAccessor.createPost(post);
        return postUuid;
    }

    public UUID createComment(UUID postUUid, UUID parentUuid, UUID authorUuid, String authorName, String content) {
        UUID commentUuid = UUID.randomUUID();
        Comment comment = new Comment(
                commentUuid,
                authorUuid,
                authorName,
                content,
                0,
                Instant.now(),
                false,
                parentUuid,
                postUUid
        );
        commentAccessor.createComment(comment);
        return commentUuid;
    }
}
