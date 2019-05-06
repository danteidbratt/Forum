package se.donut.postservice.repository.postgresql;

import org.jdbi.v3.core.Jdbi;
import se.donut.postservice.model.domain.Forum;
import se.donut.postservice.repository.ForumAccessor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PostgresForumDAO extends PostgresAbstractDAO implements ForumAccessor {

    public PostgresForumDAO(Jdbi jdbi) {
        super(jdbi);
    }

    public void create(Forum forum) {
        jdbi.useHandle(handle -> {
                    handle.createUpdate("INSERT INTO forum " +
                            "(uuid, name, author_uuid, content, score, created_at, is_deleted) " +
                            "VALUES " +
                            "(:uuid, :name, :authorUuid, :content, :score, :createdAt, false)"
                    ).bindBean(forum).execute();
                }
        );
    }


    @Override
    public void delete(Forum forum) {
        jdbi.useHandle(handle ->
                handle.createQuery(
                        "UPDATE forum SET is_deleted = true WHERE uuid = :uuid"
                ).bind("uuid", forum.getUuid())
        );
    }

    @Override
    public Optional<Forum> get(UUID uuid) {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT f.*, u.name AS author_name FROM forum f " +
                        "INNER JOIN users u ON u.uuid = f.author_uuid " +
                        "WHERE f.uuid = :uuid AND f.is_deleted = false"
                ).bind("uuid", uuid).mapTo(Forum.class).findFirst()
        );
    }

    @Override
    public List<Forum> get() {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT f.*, u.name AS author_name FROM forum f " +
                        "INNER JOIN users u ON u.uuid = f.author_uuid " +
                        "WHERE f.is_deleted = false")
                        .mapTo(Forum.class).list()
        );
    }

    @Override
    public List<Forum> getByUser(UUID userUuid) {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT f.*, u.name AS author_name FROM forum f " +
                        "INNER JOIN subscription s ON s.forum_uuid = f.uuid " +
                        "INNER JOIN users u ON u.uuid = f.author_uuid " +
                        "WHERE " +
                        "s.user_uuid = :userUuid AND " +
                        "s.is_deleted = false AND " +
                        "f.is_deleted = false")
                        .bind("userUuid", userUuid)
                        .mapTo(Forum.class)
                        .list()
        );
    }
}
