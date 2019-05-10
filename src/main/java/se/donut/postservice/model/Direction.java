package se.donut.postservice.model;

public enum Direction {

    UP(1), DOWN(-1);

    private final int value;

    Direction(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
