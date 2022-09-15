package com.github.naxos84.ai;

public final class Direction {

    public static final int TOP = 1;
    public static final int TOP_LEFT = 1 << 1;
    public static final int LEFT = 1 << 2;
    public static final int BOTTOM_LEFT = 1 << 3;
    public static final int BOTTOM = 1 << 4;
    public static final int BOTTOM_RIGHT = 1 << 5;
    public static final int RIGHT = 1 << 6;
    public static final int TOP_RIGHT = 1 << 7;

    private Direction() {
    }
}
