package se.donut.postservice.repository.postgresql;

import org.jdbi.v3.core.Jdbi;
import se.donut.postservice.model.domain.Subscription;
import se.donut.postservice.repository.SubscriptionAccessor;

import java.util.Optional;
import java.util.UUID;

public class PostgresSubscriptionDAO extends PostgresAbstractDAO implements SubscriptionAccessor {

    public PostgresSubscriptionDAO(Jdbi jdbi) {
        super(jdbi);
    }

    @Override
    public Optional<Subscription> get(UUID uuid) {
        return jdbi.withHandle(handle ->
                handle.createQuery(
                        "SELECT * FROM subscription WHERE uuid = :uuid AND is_deleted = false"
                ).bind("uuid", uuid).mapTo(Subscription.class).findFirst()
        );
    }

    @Override
    public void create(Subscription subscription) {
        jdbi.useTransaction(handle -> {
                    handle.createUpdate("INSERT INTO subscription " +
                            "(uuid, user_uuid, forum_uuid, created_at, is_deleted) " +
                            "VALUES " +
                            "(:uuid, :userUuid, :forumUuid, :createdAt, false)"
                    ).bindBean(subscription).execute();
                    handle.createUpdate(
                            "UPDATE forum SET score = score + 1 WHERE uuid = :uuid"
                    ).bind("uuid", subscription.getForumUuid()).execute();
                }
        );
    }

    @Override
    public void delete(Subscription subscription) {
        jdbi.useTransaction(handle -> {
                    handle.createUpdate(
                            "UPDATE subscription SET is_deleted = true WHERE uuid = :uuid"
                    ).bind("uuid", subscription.getUuid()).execute();
                    handle.createUpdate(
                            "UPDATE forum SET subscribers = subscribers + 1 WHERE uuid = :uuid"
                    ).bind("uuid", subscription.getForumUuid());
                }
        );
    }

    @Override
    public Optional<Subscription> getByUserAndForum(UUID userUuid, UUID forumUuid) {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT * FROM subscription " +
                        "WHERE user_uuid = :userUuid AND forum_uuid = :forumUuid")
                        .bind("userUuid", userUuid)
                        .bind("forumUuid", forumUuid)
                        .mapTo(Subscription.class)
                        .findFirst()
        );
    }
}
