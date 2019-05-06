package se.donut.postservice.resource.request;

import se.donut.postservice.model.domain.Submission;

import java.util.Comparator;

public enum SortType {

    TOP("score", (a, b) -> b.getScore() - a.getScore()),
    NEW("created_at", (a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

    private final String columnName;
    private final Comparator<Submission> comparator;

    SortType(String columnName, Comparator<Submission> comparator) {
        this.columnName = columnName;
        this.comparator = comparator;
    }

    public Comparator<Submission> getComparator() {
        return comparator;
    }

    public String getColumnName() {
        return columnName;
    }
}
