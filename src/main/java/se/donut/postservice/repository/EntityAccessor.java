package se.donut.postservice.repository;

import se.donut.postservice.model.domain.AbstractEntity;

import java.util.Optional;
import java.util.UUID;

public interface EntityAccessor<T extends AbstractEntity> {

    Optional<T> get(UUID uuid);

    void create(T entity);

    void delete(T entity);

}
