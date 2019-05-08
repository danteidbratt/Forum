package se.donut.postservice.model.domain;

import lombok.Getter;
import se.donut.postservice.model.Sortable;
import se.donut.postservice.resource.request.SortType;
import se.donut.postservice.util.Ranking;

import static se.donut.postservice.resource.request.SortType.HOT;

@Getter
public class SortableComment extends Comment implements Sortable {

    private final double heat;

    public SortableComment(Comment comment, SortType sortType) {
        super(
                comment.getUuid(),
                comment.getAuthorUuid(),
                comment.getContent(),
                comment.getScore(),
                comment.getParentUuid(),
                comment.getPostUuid(),
                comment.getCreatedAt()
        );
        this.heat = sortType.equals(HOT) ? Ranking.calculateHeat(comment) : 0;
    }
}
