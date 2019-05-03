package se.donut.postservice.repository.postgresql;

import org.jdbi.v3.core.Jdbi;

public class PostgresAbstractDAO {

    protected final Jdbi jdbi;

    public PostgresAbstractDAO(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public void beginTransaction() {
        jdbi.open();
    }

    public void endTransaction() {

    }

}
