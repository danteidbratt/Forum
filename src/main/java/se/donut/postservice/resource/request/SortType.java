package se.donut.postservice.resource.request;

import se.donut.postservice.model.Sortable;

import java.util.Comparator;

public enum SortType {

    TOP((a, b) -> b.getScore() - a.getScore()),
    NEW((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt())),
    HOT((a, b) -> Double.compare(b.getHeat(), a.getHeat()));

    private final Comparator<Sortable> comparator;

    SortType(Comparator<Sortable> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Sortable> getComparator() {
        return comparator;
    }

}
