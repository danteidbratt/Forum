package se.donut.postservice.repository.postgresql;

import org.jdbi.v3.core.Jdbi;
import se.donut.postservice.model.domain.Comment;
import se.donut.postservice.repository.CommentAccessor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PostgresCommentDAO extends PostgresAbstractDAO implements CommentAccessor {

    private static final String INSERT_ROOT_COMMENT = "INSERT INTO comment " +
            "(uuid, author_uuid, author_name, content, score, created_at, is_deleted, post_uuid) VALUES " +
            "(:uuid, :authorUuid, :authorName, :content, :score, :createdAt, :isDeleted, :postUuid)";

    private static final String INSERT_NESTED_COMMENT = "INSERT INTO comment " +
            "(uuid, author_uuid, author_name, content, score, created_at, is_deleted, parent_uuid, post_uuid) VALUES " +
            "(:uuid, :authorUuid, :authorName, :content, :score, :createdAt, :isDeleted, :parentUuid, :postUuid)";

    public PostgresCommentDAO(Jdbi jdbi) {
        super(jdbi);
    }


    @Override
    public Optional<Comment> getComment(UUID uuid) {
        return jdbi.withHandle(handle ->
                handle.createQuery(
                        "SELECT * FROM comment WHERE uuid = :uuid"
                ).bind("uuid", uuid).mapTo(Comment.class).findFirst()
        );
    }

    public List<Comment> getComments(UUID parentUUid) {
        List<Comment> comments = jdbi.withHandle(handle ->
                handle.createQuery(
                        "SELECT * FROM comment WHERE parent_uuid = :parentUuid"
                ).bind("parentUuid", parentUUid).mapTo(Comment.class).list()
        );
        return comments;
    }

    public void createComment(Comment comment) {
        jdbi.useHandle(handle ->
                handle.createUpdate(
                        comment.getParentUuid() == null ? INSERT_ROOT_COMMENT : INSERT_NESTED_COMMENT
                ).bindBean(comment).execute()
        );
    }
}
