package se.donut.postservice.repository.postgresql;

import org.jdbi.v3.sqlobject.CreateSqlObject;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;
import se.donut.postservice.model.domain.Post;
import se.donut.postservice.model.domain.Vote;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static se.donut.postservice.model.Direction.UP;

public interface PostDAO {

    @CreateSqlObject
    UserDAO getUserDAO();

    @SqlQuery("SELECT * FROM post " +
            "WHERE uuid = :uuid AND is_deleted = false")
    Optional<Post> get(@Bind("uuid") UUID uuid);

    @SqlQuery("SELECT * FROM post WHERE is_deleted = false")
    List<Post> getAll();

    @SqlQuery("SELECT p.* FROM post p " +
            "INNER JOIN forum f ON f.uuid = p.forum_uuid " +
            "INNER JOIN subscription s ON s.forum_uuid = f.uuid " +
            "WHERE s.user_uuid = :userUuid")
    List<Post> getBySubscriptions(@Bind("userUuid") UUID userUuid);

    @SqlUpdate("UPDATE post SET is_deleted = true WHERE uuid = :uuid")
    void delete(Post post);

    @SqlUpdate("INSERT INTO post " +
            "(uuid, author_uuid, content, score, forum_uuid, title, created_at, comment_count, is_deleted) " +
            "VALUES " +
            "(:uuid, :authorUuid, :content, :score, :forumUuid, :title, :createdAt, :commentCount, false)")
    void create(@BindBean Post post);

    @SqlQuery("SELECT p.*, u.name AS author_name FROM post p " +
            "INNER JOIN users u ON u.uuid = p.author_uuid " +
            "WHERE p.forum_uuid = :forumUuid AND p.is_deleted = false")
    List<Post> getByForum(@Bind("forumUuid") UUID forumUuid);

    @SqlQuery("SELECT v.* FROM post_vote v " +
            "INNER JOIN post p ON p.uuid = v.target_uuid " +
            "WHERE v.user_uuid = :userUuid " +
            "AND p.forum_uuid IN (<forumUuids>)")
    List<Vote> getVotes(
            @Bind("userUuid") UUID userUuid,
            @BindList("forumUuids") List<UUID> forumUuids
    );

    @SqlQuery("SELECT * FROM post_vote " +
            "WHERE user_uuid = :userUuid AND target_uuid = :postUuid")
    Optional<Vote> getVote(
            @Bind("userUuid") UUID userUuid,
            @Bind("postUuid") UUID postUuids
    );

    @SqlQuery("SELECT * FROM post " +
            "WHERE author_uuid = :authorUuid " +
            "AND is_deleted = false")
    List<Post> getByAuthor(@Bind("authorUuid") UUID authorUuid);

    @SqlQuery("SELECT * FROM post p " +
            "INNER JOIN post_vote v ON v.target_uuid = p.uuid " +
            "AND v.direction = 'UP' " +
            "WHERE v.user_uuid = :userUuid")
    List<Post> getByLikes(@Bind("userUuid") UUID userUuid);

    @SqlUpdate("UPDATE post SET comment_count = comment_count + :diff WHERE uuid = :postUuid")
    void updateCommentCount(
            @Bind("postUuid") UUID postUuid,
            @Bind("diff") int diff
    );

    @SqlUpdate("INSERT INTO post_vote " +
            "(target_uuid, user_uuid, direction) " +
            "VALUES " +
            "(:targetUuid, :userUuid, :direction) " +
            "ON CONFLICT DO NOTHING")
    int createVote(@BindBean Vote vote);

    @SqlUpdate("DELETE FROM post_vote " +
            "WHERE user_uuid = :userUuid " +
            "AND target_uuid = :targetUuid")
    int deleteVote(
            @BindBean Vote vote
    );

    @SqlUpdate("UPDATE post SET score = score + :diff WHERE uuid = :postUuid")
    void updateScoreOnPost(@Bind("postUuid") UUID postUuid, @Bind("diff") int diff);

    @Transaction
    default void voteAndUpdateScore(Vote vote) {
        int rowsAffected = createVote(vote);
        if (rowsAffected == 1) {
            updateScoreOnPost(vote.getTargetUuid(), vote.getDirection().equals(UP) ? 1 : -1);
        }
    }

    @Transaction
    default void deleteVoteAndUpdateScore(Vote vote) {
        int rowsAffected = deleteVote(vote);
        if (rowsAffected == 1) {
            updateScoreOnPost(vote.getTargetUuid(), vote.getDirection().equals(UP) ? -1 : 1);
        }
    }

}
