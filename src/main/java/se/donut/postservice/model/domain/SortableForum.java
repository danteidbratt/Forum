package se.donut.postservice.model.domain;

import lombok.Getter;
import se.donut.postservice.model.Sortable;
import se.donut.postservice.resource.request.SortType;
import se.donut.postservice.util.Ranking;

import static se.donut.postservice.resource.request.SortType.*;

@Getter
public class SortableForum extends Forum implements Sortable {

    private final double heat;

    public SortableForum(Forum forum, SortType sortType) {
        super(
                forum.getUuid(),
                forum.getAuthorUuid(),
                forum.getName(),
                forum.getContent(),
                forum.getScore(),
                forum.getCreatedAt()
        );
        this.heat = sortType.equals(HOT) ? Ranking.calculateHeat(forum) : 0;
    }
}
