package se.donut.postservice.repository.postgresql;

import org.jdbi.v3.core.Jdbi;
import se.donut.postservice.model.domain.Post;
import se.donut.postservice.repository.PostAccessor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PostgresPostDAO extends PostgresAbstractDAO implements PostAccessor {

    public PostgresPostDAO(Jdbi jdbi) {
        super(jdbi);
    }

    public Optional<Post> get(UUID uuid) {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT * FROM post " +
                        "WHERE uuid = :uuid AND is_deleted = false")
                        .bind("uuid", uuid)
                        .mapTo(Post.class)
                        .findFirst()
        );
    }

    // TODO: Also delete comments
    public void delete(Post post) {
        jdbi.useHandle(handle ->
                handle.createUpdate(
                        "UPDATE post SET is_deleted = true WHERE uuid = :uuid")
                        .bind("uuid", post.getUuid())
                        .execute()
        );
    }

    public void create(Post post) {
        jdbi.useHandle(handle ->
                handle.createUpdate("INSERT INTO post " +
                        "(uuid, author_uuid, content, score, forum_uuid, title, link, created_at, is_deleted) " +
                        "VALUES " +
                        "(:uuid, :authorUuid, :content, :score, :forumUuid, :title, :link, :createdAt, false)")
                        .bindBean(post)
                        .execute()
        );
    }

    @Override
    public List<Post> getByForum(UUID forumUuid) {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT p.*, u.name AS author_name FROM post p " +
                        "INNER JOIN users u ON u.uuid = p.author_uuid " +
                        "WHERE p.forum_uuid = :forumUuid AND p.is_deleted = false")
                        .bind("forumUuid", forumUuid)
                        .mapTo(Post.class)
                        .list()
        );
    }
}
