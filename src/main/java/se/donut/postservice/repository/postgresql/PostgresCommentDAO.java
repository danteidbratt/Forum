package se.donut.postservice.repository.postgresql;

import org.jdbi.v3.core.Jdbi;
import se.donut.postservice.model.domain.Comment;
import se.donut.postservice.repository.CommentAccessor;

import java.util.List;
import java.util.UUID;

public class PostgresCommentDAO extends PostgresAbstractDAO implements CommentAccessor {

    private static final String INSERT_ROOT_COMMENT = "INSERT INTO comment " +
            "(uuid, created_by, content, score, created_at, is_deleted, post_uuid) VALUES " +
            "(:uuid, :authorUuid, :content, :score, :createdAt, :isDeleted, :postUuid)";

    private static final String INSERT_NESTED_COMMENT = "INSERT INTO comment " +
            "(uuid, created_by, content, score, created_at, is_deleted, parent_uuid, post_uuid) VALUES " +
            "(:uuid, :authorUuid, :content, :score, :createdAt, :isDeleted, :parentUuid, :postUuid)";

    public PostgresCommentDAO(Jdbi jdbi) {
        super(jdbi);
    }

    @Override
    public List<Comment> getComments(UUID forumUuid, UUID postUuid, List<UUID> path) {
        return null;
    }

    public void createComment(Comment comment) {
        jdbi.useHandle(handle ->
                handle.createUpdate(comment.getParentUuid() == null ? INSERT_ROOT_COMMENT : INSERT_NESTED_COMMENT
                ).bindBean(comment).execute()
        );
    }
}
