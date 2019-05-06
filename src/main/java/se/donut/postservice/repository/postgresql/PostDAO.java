package se.donut.postservice.repository.postgresql;

import org.jdbi.v3.sqlobject.config.KeyColumn;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import se.donut.postservice.model.domain.Post;
import se.donut.postservice.model.domain.Vote;
import se.donut.postservice.model.mapper.VoteMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface PostDAO {

    @SqlQuery("SELECT * FROM post " +
            "WHERE uuid = :uuid AND is_deleted = false")
    Optional<Post> get(@Bind("uuid") UUID uuid);

    @SqlUpdate("UPDATE post SET is_deleted = true WHERE uuid = :uuid")
    void delete(Post post);

    @SqlUpdate("INSERT INTO post " +
            "(uuid, author_uuid, content, score, forum_uuid, title, link, created_at, is_deleted) " +
            "VALUES " +
            "(:uuid, :authorUuid, :content, :score, :forumUuid, :title, :link, :createdAt, false)")
    void create(@BindBean Post post);

    @SqlQuery("SELECT p.*, u.name AS author_name FROM post p " +
            "INNER JOIN users u ON u.uuid = p.author_uuid " +
            "WHERE p.forum_uuid = :forumUuid AND p.is_deleted = false")
    List<Post> getByForum(@Bind("forumUuid") UUID forumUuid);

    @SqlQuery("SELECT * FROM post_vote " +
            "WHERE user_uuid = userUuid AND target_parent_uuid = :forumUuid")
    @KeyColumn("target_uuid")
    @RegisterConstructorMapper(VoteMapper.class)
    Map<UUID, Vote> getVotes(
            @Bind("userUuid") UUID userUuid,
            @Bind("forumUuid") UUID forumUuid
    );

    @SqlUpdate("INSERT INTO post_vote " +
            "(target_uuid, target_parent_uuid, user_uuid, direction) " +
            "VALUES " +
            "(:targetUuid, :targetParentUuid, :userUuid, :direction) " +
            "ON CONFLICT (user_uuid, target_uuid) DO UPDATE " +
            "SET direction = :direction")
    void vote(@BindBean Vote vote);

    @SqlUpdate("DELETE FROM post_vote " +
            "WHERE user_uuid = :userUuid " +
            "AND target_uuid = :targetUuid")
    void deleteVote(
            @Bind("userUuid") UUID userUuid,
            @Bind("targetUuid") UUID targetUuid
    );

}
