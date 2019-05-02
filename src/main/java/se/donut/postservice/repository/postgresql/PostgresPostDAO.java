package se.donut.postservice.repository.postgresql;

import org.jdbi.v3.core.Jdbi;
import se.donut.postservice.model.domain.Post;
import se.donut.postservice.repository.PostAccessor;

import java.util.Optional;
import java.util.UUID;

public class PostgresPostDAO extends PostgresAbstractDAO implements PostAccessor {

    public PostgresPostDAO(Jdbi jdbi) {
        super(jdbi);
    }

    public Optional<Post> getPost(UUID uuid) {
        return jdbi.withHandle(handle ->
                handle.createQuery(
                        "SELECT * FROM post WHERE uuid = :uuid AND is_deleted = false"
                ).bind("uuid", uuid).mapTo(Post.class).findFirst()
        );
    }

    // TODO: Also delete comments
    public void deletePost(UUID uuid) {
        jdbi.useHandle(handle ->
                handle.createUpdate(
                        "UPDATE post SET is_deleted = true WHERE uuid = :uuid"
                ).bind("uuid", uuid).execute()
        );
    }

    public void createPost(Post post) {
        jdbi.useHandle(handle ->
                handle.createUpdate("INSERT INTO post " +
                        "(uuid, author_uuid, author_name, content, score, created_at, is_deleted, forum_uuid, title, link) " +
                        "VALUES " +
                        "(:uuid, :authorUuid, :authorName, :content, :score, :createdAt, :isDeleted, :forumUuid, :title, :link)"
                ).bindBean(post).execute()
        );
    }
}
