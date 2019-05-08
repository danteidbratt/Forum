package se.donut.postservice.repository.postgresql;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import se.donut.postservice.model.domain.Comment;
import se.donut.postservice.model.domain.Vote;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentDAO {

    @SqlQuery("SELECT * FROM comment " +
            "WHERE uuid = :uuid AND is_deleted = false")
    Optional<Comment> getComment(@Bind("uuid") UUID uuid);

    @SqlQuery("SELECT * FROM comment " +
            "WHERE post_uuid = :postUuid")
    List<Comment> getCommentsByPostUuid(@Bind("postUuid") UUID postUuid);

    @SqlUpdate("INSERT INTO comment " +
            "(uuid, author_uuid, content, score, parent_uuid, post_uuid, created_at, is_deleted) " +
            "VALUES " +
            "(:uuid, :authorUuid, :content, :score, :parentUuid, :postUuid, :createdAt, false)")
    void createComment(@BindBean Comment comment);

    @SqlUpdate("INSERT INTO comment_vote " +
            "(target_uuid, target_parent_uuid, user_uuid, direction) " +
            "VALUES " +
            "(:targetUuid, :targetParentUuid, :userUuid, :direction) " +
            "ON CONFLICT (user_uuid, target_uuid) DO UPDATE " +
            "SET direction = :direction")
    void vote(@BindBean Vote vote);

    @SqlUpdate("DELETE FROM comment_vote" +
            "WHERE user_uuid = :userUuid AND target_uuid = :commentUuid")
    void deleteVote(
            @Bind("userUuid") UUID userUuid,
            @Bind("commentUuid") UUID commentUuid
    );

    @SqlQuery("SELECT * FROM comment_vote " +
            "WHERE user_uuid = :userUuid " +
            "AND target_parent_uuid = :postUuid")
    List<Vote> getVotes(
            @Bind("userUuid") UUID userUuid,
            @Bind("postUuid") UUID postUuid
    );

}
