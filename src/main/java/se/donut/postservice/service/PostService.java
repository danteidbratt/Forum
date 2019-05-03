package se.donut.postservice.service;

import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.api.CommentDTO;
import se.donut.postservice.model.api.PostDTO;
import se.donut.postservice.model.domain.Forum;
import se.donut.postservice.model.domain.Post;
import se.donut.postservice.repository.ForumAccessor;
import se.donut.postservice.repository.PostAccessor;
import se.donut.postservice.resource.request.SortType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static se.donut.postservice.exception.ExceptionType.FORUM_NOT_FOUND;
import static se.donut.postservice.exception.ExceptionType.POST_NOT_FOUND;

public class PostService {

    private final PostAccessor postAccessor;
    private final CommentService commentService;
    private final ForumAccessor forumAccessor;

    public PostService(PostAccessor postAccessor, ForumAccessor forumAccessor, CommentService commentService) {
        this.postAccessor = postAccessor;
        this.forumAccessor = forumAccessor;
        this.commentService = commentService;
    }

    public PostDTO getPost(UUID postUuid, SortType sortType) {
        Post post = postAccessor.get(postUuid)
                .orElseThrow(() -> new PostServiceException(POST_NOT_FOUND));

        List<CommentDTO> commentTree = commentService.getCommentTreeByPost(postUuid, sortType);

        return post.toApiModel(commentTree);
    }

    public UUID createPost(UUID forumUuid, UUID authorUuid, String authorName, String title, String link, String content) {
        Forum forum = forumAccessor.get(forumUuid).orElseThrow(() -> new PostServiceException(FORUM_NOT_FOUND));
        UUID postUuid = UUID.randomUUID();
        Post post = new Post(
                postUuid,
                authorUuid,
                authorName,
                content,
                0,
                forum.getUuid(),
                title,
                link,
                Instant.now()
        );
        postAccessor.create(post);
        return postUuid;
    }

}
