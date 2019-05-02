package se.donut.postservice.model.domain;

import lombok.Getter;
import se.donut.postservice.model.api.CommentDTO;
import se.donut.postservice.resource.request.CommentSortType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class Comment extends Entry {

    private final UUID parentUuid;
    private final UUID postUuid;

    public Comment(
            UUID uuid,
            UUID authorUuid,
            String authorName,
            String content,
            int score,
            Instant createdAt,
            Boolean isDeleted,
            UUID parentUuid,
            UUID postUuid
    ) {
        super(uuid, authorUuid, authorName, content, score, createdAt, isDeleted);
        this.parentUuid = parentUuid;
        this.postUuid = postUuid;
    }

    public CommentDTO toApiModel(List<CommentDTO> children) {
        return new CommentDTO(
                this.getUuid(),
                this.getAuthorUuid(),
                this.getAuthorName(),
                this.getContent(),
                this.getScore(),
                this.getCreatedAt(),
                children
        );
    }

    public static List<CommentDTO> buildCommentTree(UUID postUuid, List<Comment> comments, CommentSortType sortType) {
        Map<UUID, List<Comment>> commentMap = comments.stream()
                .sorted(sortType.getComparator())
                .collect(Collectors.groupingBy(Comment::getParentUuid));

        return recurse(commentMap, postUuid);
    }

    private static List<CommentDTO> recurse(Map<UUID, List<Comment>> commentMap, UUID parentUuid) {
        if (!commentMap.containsKey(parentUuid)) {
            return null;
        }
        List<CommentDTO> result = new ArrayList<>();
        for (Comment comment : commentMap.get(parentUuid)) {
            result.add(comment.toApiModel(recurse(commentMap, comment.getUuid())));
        }
        return result;
    }
}
