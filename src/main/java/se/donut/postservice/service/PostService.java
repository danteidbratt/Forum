package se.donut.postservice.service;

import se.donut.postservice.exception.PostServiceException;
import se.donut.postservice.model.api.PostDTO;
import se.donut.postservice.model.domain.Forum;
import se.donut.postservice.model.domain.Post;
import se.donut.postservice.repository.ForumAccessor;
import se.donut.postservice.repository.PostAccessor;
import se.donut.postservice.resource.request.SortType;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static se.donut.postservice.exception.ExceptionType.FORUM_NOT_FOUND;
import static se.donut.postservice.exception.ExceptionType.POST_NOT_FOUND;

public class PostService {

    private final PostAccessor postAccessor;
    private final ForumAccessor forumAccessor;

    public PostService(PostAccessor postAccessor, ForumAccessor forumAccessor) {
        this.postAccessor = postAccessor;
        this.forumAccessor = forumAccessor;
    }

    public PostDTO getPost(UUID postUuid) {
        Post post = postAccessor.get(postUuid)
                .orElseThrow(() -> new PostServiceException(POST_NOT_FOUND));

        return post.toApiModel();
    }

    public List<PostDTO> getByForum(UUID forumUuid, SortType sortType) {
        return postAccessor.getByForum(forumUuid)
                .stream()
                .sorted(sortType.getComparator())
                .map(Post::toApiModel)
                .collect(Collectors.toList());
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
                new Date()
        );
        postAccessor.create(post);
        return postUuid;
    }

}
