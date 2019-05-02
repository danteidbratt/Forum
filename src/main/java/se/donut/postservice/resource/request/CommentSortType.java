package se.donut.postservice.resource.request;

import se.donut.postservice.model.domain.Comment;

import java.util.Comparator;

public enum CommentSortType {

    TOP((a, b) -> b.getScore() - a.getScore()),
    NEW((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));

    private final Comparator<Comment> comparator;

    CommentSortType(Comparator<Comment> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Comment> getComparator() {
        return comparator;
    }
}
