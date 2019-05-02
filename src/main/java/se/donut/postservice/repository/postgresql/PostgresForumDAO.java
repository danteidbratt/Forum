package se.donut.postservice.repository.postgresql;

import org.jdbi.v3.core.Jdbi;
import se.donut.postservice.model.domain.Forum;
import se.donut.postservice.repository.ForumAccessor;

public class PostgresForumDAO extends PostgresAbstractDAO implements ForumAccessor {

    public PostgresForumDAO(Jdbi jdbi) {
        super(jdbi);
    }

    public void createForum(Forum forum) {
        jdbi.useHandle(handle ->
                handle.createUpdate("INSERT INTO forum " +
                        "(uuid, created_by, description, created_at, is_deleted) " +
                        "VALUES " +
                        "(:uuid, :createdBy, :description, :createdAt, :isDeleted)"
                ).bindBean(forum)
                .execute()
        );
    }
}
