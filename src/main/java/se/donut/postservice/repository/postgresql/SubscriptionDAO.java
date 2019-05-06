package se.donut.postservice.repository.postgresql;

import org.jdbi.v3.sqlobject.config.KeyColumn;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import se.donut.postservice.model.domain.Subscription;
import se.donut.postservice.model.mapper.SubscriptionMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionDAO {

    @SqlUpdate("INSERT INTO subscription " +
            "(user_uuid, forum_uuid) " +
            "VALUES " +
            "(:userUuid, :forumUuid)")
    void create(@BindBean Subscription subscription);

    @SqlUpdate("DELETE FROM subscription " +
            "WHERE user_uuid = :userUuid AND forum_uuid = :forumUuid")
    void delete(@BindBean Subscription subscription);

    @SqlQuery("SELECT * FROM subscription " +
            "WHERE user_uuid = :userUuid")
    List<Subscription> getByUser(@Bind("userUuid") UUID userUuid);

    @SqlQuery("SELECT * FROM subscription " +
            "WHERE user_uuid = :userUuid AND forum_uuid = :forumUuid")
    Optional<Subscription> getByUserAndForum(
            @Bind("userUuid") UUID userUuid,
            @Bind("forumUuid") UUID forumUuid
    );
}
