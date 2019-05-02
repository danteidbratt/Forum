package se.donut.postservice.service;

import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.api.CommentDTO;
import se.donut.postservice.model.api.PostThreadDTO;
import se.donut.postservice.model.domain.Comment;
import se.donut.postservice.model.domain.Post;
import se.donut.postservice.repository.CommentAccessor;
import se.donut.postservice.repository.PostAccessor;
import se.donut.postservice.resource.request.CommentSortType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static se.donut.postservice.exception.ExceptionType.POST_NOT_FOUND;

public class PostService {

    private final PostAccessor postAccessor;
    private final CommentAccessor commentAccessor;

    public PostService(PostAccessor postAccessor, CommentAccessor commentAccessor) {
        this.postAccessor = postAccessor;
        this.commentAccessor = commentAccessor;
    }

    public PostThreadDTO getPost(UUID postUuid, CommentSortType commentSortType) {
        Post post = postAccessor.getPost(postUuid)
                .orElseThrow(() -> new PostServiceException(POST_NOT_FOUND));

        List<Comment> comments = commentAccessor.getCommentsByPostUuid(postUuid);

        List<CommentDTO> commentTree = Comment.buildCommentTree(postUuid, comments, commentSortType);

        return post.toApiModel(commentTree);

    }

    public UUID createPost(UUID forumUuid, UUID authorUuid, String authorName, String title, String link, String content) {
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

}
