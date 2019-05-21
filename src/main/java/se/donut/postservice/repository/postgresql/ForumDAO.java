package se.donut.postservice.repository.postgresql;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;
import se.donut.postservice.model.domain.Forum;
import se.donut.postservice.model.domain.Subscription;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ForumDAO {

    @SqlQuery("SELECT * FROM forum " +
            "WHERE uuid = :uuid AND is_deleted = false")
    Optional<Forum> getForum(@Bind("uuid") UUID uuid);

    @SqlQuery("SELECT * FROM forum " +
            "WHERE name = :name AND is_deleted = false")
    Optional<Forum> getForumByName(@Bind("name") String name);

    @SqlQuery("SELECT * FROM forum " +
            "WHERE is_deleted = false AND uuid IN (<uuids>)")
    List<Forum> getForums(@BindList("uuids") List<UUID> uuids);

    @SqlQuery("SELECT * FROM forum " +
            "WHERE is_deleted = false")
    List<Forum> getAllForums();

    @SqlUpdate("INSERT INTO forum " +
            "(uuid, name, author_uuid, content, score, created_at, is_deleted) " +
            "VALUES " +
            "(:uuid, :name, :authorUuid, :content, :score, :createdAt, false)")
    void createForum(@BindBean Forum forum);

    @SqlUpdate("UPDATE forum SET is_deleted = true WHERE uuid = :uuid")
    void deleteForum(@BindBean Forum forum);

    @SqlUpdate("UPDATE forum SET score = score + 1 WHERE uuid = :forumUuid")
    int addForumScore(@Bind("forumUuid") UUID forumUuid);

    @SqlUpdate("UPDATE forum SET score = score - 1 WHERE uuid = :forumUuid")
    int subtractForumScore(@Bind("forumUuid") UUID forumUuid);

    @SqlQuery("SELECT * FROM subscription " +
            "WHERE user_uuid = :userUuid AND forum_uuid = :forumUuid")
    Optional<Subscription> getSubscription(
            @Bind("userUuid") UUID userUuid,
            @Bind("forumUuid") UUID forumUuid
    );

    @SqlQuery("SELECT f.* FROM forum f " +
            "INNER JOIN subscription s ON s.forum_uuid = f.uuid " +
            "WHERE s.user_uuid = :userUuid")
    List<Forum> getSubscriptions(@Bind("userUuid") UUID userUuid);

    @SqlUpdate("INSERT INTO subscription " +
            "(user_uuid, forum_uuid) " +
            "VALUES " +
            "(:userUuid, :forumUuid) " +
            "ON CONFLICT DO NOTHING")
    int createSubscription(@BindBean Subscription subscription);

    @SqlUpdate("DELETE FROM subscription " +
            "WHERE user_uuid = :userUuid " +
            "AND forum_uuid = :forumUuid")
    int deleteSubscription(
            @Bind("userUuid") UUID userUuid,
            @Bind("forumUuid") UUID forumUuid
    );

    @SqlQuery("SELECT * FROM subscription " +
            "WHERE user_uuid = :userUuid")
    List<Subscription> getSubscriptionsByUser(@Bind("userUuid") UUID userUuid);

    @Transaction
    default void createForumAndSubscribe(Forum forum, Subscription subscription) {
        createForum(forum);
        createSubscription(subscription);
    }

    @Transaction
    default void subscribe(Subscription subscription) {
        int rowsAffected = createSubscription(subscription);
        if (rowsAffected == 1) {
            addForumScore(subscription.getForumUuid());
        }
    }

    @Transaction
    default void unsubscribe(UUID userUuid, UUID forumUuid) {
        int rowsAffected = deleteSubscription(userUuid, forumUuid);
        if (rowsAffected == 1) {
            subtractForumScore(forumUuid);
        }
    }

}
