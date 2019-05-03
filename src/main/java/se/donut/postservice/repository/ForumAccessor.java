package se.donut.postservice.repository;

import se.donut.postservice.model.domain.Forum;

import java.util.List;


public interface ForumAccessor extends EntityAccessor<Forum> {

    List<Forum> get();

}
