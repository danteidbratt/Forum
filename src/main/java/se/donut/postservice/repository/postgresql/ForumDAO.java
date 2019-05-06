package se.donut.postservice.repository.postgresql;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import se.donut.postservice.model.domain.Forum;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ForumDAO {

    @SqlQuery("SELECT * FROM forum " +
            "WHERE uuid = :uuid AND is_deleted = false")
    Optional<Forum> get(@Bind("uuid") UUID uuid);

    @SqlUpdate("INSERT INTO forum " +
            "(uuid, name, author_uuid, content, score, created_at, is_deleted) " +
            "VALUES " +
            "(:uuid, :name, :authorUuid, :content, :score, :createdAt, false)")
    void create(@BindBean Forum forum);

    @SqlUpdate("UPDATE forum SET is_deleted = true WHERE uuid = :uuid")
    void delete(@BindBean Forum forum);

    @SqlQuery("SELECT * FROM forum " +
            "WHERE is_deleted = false")
    List<Forum> getAll();

    @SqlQuery("SELECT * FROM forum " +
            "WHERE is_deleted = false AND uuid IN (<uuids>)")
    List<Forum> get(@BindList("uuids") List<UUID> uuids);

}
