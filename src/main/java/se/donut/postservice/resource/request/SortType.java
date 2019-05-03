package se.donut.postservice.resource.request;

import se.donut.postservice.model.domain.Submission;

import java.util.Comparator;

public enum SortType {

    TOP((a, b) -> b.getScore() - a.getScore()),
    NEW((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

    private final Comparator<Submission> comparator;

    SortType(Comparator<Submission> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Submission> getComparator() {
        return comparator;
    }
}
