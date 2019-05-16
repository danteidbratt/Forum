package se.donut.postservice.repository.postgresql;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlScript;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;
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
            "(target_uuid, user_uuid, direction) " +
            "VALUES " +
            "(:targetUuid, :userUuid, :direction) " +
            "ON CONFLICT DO NOTHING")
    int vote(@BindBean Vote vote);

    @SqlUpdate("DELETE FROM comment_vote " +
            "WHERE user_uuid = :userUuid " +
            "AND target_uuid = :targetUuid")
    int deleteVote(@BindBean Vote vote);

    @SqlQuery("SELECT * FROM comment_vote " +
            "WHERE user_uuid = :userUuid " +
            "AND target_uuid = :targetUuid")
    Optional<Vote> getVote(@Bind("userUuid") UUID userUuid, @Bind("targetUuid") UUID commentUuid);

    @SqlUpdate("UPDATE comment SET score = score + :diff WHERE uuid = :commentUuid")
    void updateScoreOnComment(@Bind("commentUuid") UUID commentUuid, @Bind("diff") int diff);

    @SqlQuery("SELECT v.* FROM comment_vote v " +
            "INNER JOIN comment c ON c.uuid = v.target_uuid " +
            "WHERE c.post_uuid = :postUuid")
//    @SqlQuery("SELECT * FROM comment_vote " +
//            "WHERE user_uuid = :userUuid " +
//            "AND target_parent_uuid = :postUuid")
    List<Vote> getVotes(
            @Bind("userUuid") UUID userUuid,
            @Bind("postUuid") UUID postUuid
    );

    @Transaction
    default void voteAndUpdateScore(Vote vote) {
        int rowsAffected = vote(vote);
        if (rowsAffected == 1) {
            updateScoreOnComment(vote.getTargetUuid(), vote.getDirection().getValue());
        }
    }

    @Transaction
    default void deleteVoteAndUpdateScore(Vote vote) {
        int rowsAffected = deleteVote(vote);
        if(rowsAffected == 1) {
            updateScoreOnComment(vote.getTargetUuid(), -vote.getDirection().getValue());
        }

    }

}
