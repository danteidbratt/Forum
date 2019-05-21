package se.donut.postservice.model.domain;

import lombok.Getter;
import se.donut.postservice.model.Sortable;
import se.donut.postservice.resource.request.SortType;
import se.donut.postservice.util.Ranking;

import static se.donut.postservice.resource.request.SortType.HOT;

@Getter
public final class SortablePost extends Post implements Sortable {

    private final double heat;

    public SortablePost(
            Post post,
            SortType sortType) {
        super(
                post.getUuid(),
                post.getAuthorUuid(),
                post.getContent(),
                post.getScore(),
                post.getForumUuid(),
                post.getTitle(),
                post.getCreatedAt(),
                post.getCommentCount()
        );
        this.heat = sortType.equals(HOT) ? Ranking.calculateHeat(post) : 0;
    }

}
