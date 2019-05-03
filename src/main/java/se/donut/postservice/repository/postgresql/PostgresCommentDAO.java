package se.donut.postservice.repository.postgresql;

import org.jdbi.v3.core.Jdbi;
import se.donut.postservice.model.Direction;
import se.donut.postservice.model.domain.Comment;
import se.donut.postservice.model.domain.Vote;
import se.donut.postservice.repository.CommentAccessor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static se.donut.postservice.model.Direction.*;

public class PostgresCommentDAO extends PostgresAbstractDAO implements CommentAccessor {

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

    public List<Comment> getCommentsByPostUuid(UUID postUuid) {
        return jdbi.withHandle(handle ->
                handle.createQuery(
                        "SELECT * FROM comment WHERE post_uuid = :postUuid AND is_deleted = false"
                ).bind("postUuid", postUuid).mapTo(Comment.class).list()
        );
    }

    public void createComment(Comment comment) {
        jdbi.useHandle(handle ->
                handle.createUpdate("INSERT INTO comment " +
                        "(uuid, author_uuid, author_name, content, score, parent_uuid, post_uuid, created_at, is_deleted) " +
                        "VALUES " +
                        "(:uuid, :authorUuid, :authorName, :content, :score, :parentUuid, :postUuid, :createdAt, false)"
                ).bindBean(comment).execute()
        );
    }

    public void vote(Vote vote) {
        jdbi.useTransaction(handle -> {
                    handle.createUpdate("INSERT INTO comment_vote " +
                            "(uuid, target_uuid, user_uuid, direction, created_at, is_deleted) " +
                            "VALUES " +
                            "(:uuid, :targetUuid, :userUuid, :direction, :createdAt, false)"
                    ).bindBean(vote).execute();
                    handle.createUpdate("UPDATE comment SET score = score + :direction where uuid = :targetUuid")
                            .bind("direction", vote.getDirection().equals(UP) ? 1 : -1)
                            .bind("targetUuid", vote.getTargetUuid()).execute();
                }
        );
    }
}
