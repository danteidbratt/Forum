package se.donut.postservice.repository.postgresql;

import org.jdbi.v3.core.Jdbi;
import se.donut.postservice.model.domain.Post;
import se.donut.postservice.repository.PostAccessor;

public class PostgresPostDAO extends PostgresAbstractDAO implements PostAccessor {

    public PostgresPostDAO(Jdbi jdbi) {
        super(jdbi);
    }

    public void createPost(Post post) {
        jdbi.useHandle(handle ->
                handle.createUpdate("INSERT INTO post " +
                        "(uuid, created_by, content, score, created_at, is_deleted, forum_uuid, title, link) " +
                        "VALUES " +
                        "(:uuid, :authorUuid, :content, :score, :createdAt, :isDeleted, :forumUuid, :title, :link)"
                ).bindBean(post).execute()
        );
    }
}
