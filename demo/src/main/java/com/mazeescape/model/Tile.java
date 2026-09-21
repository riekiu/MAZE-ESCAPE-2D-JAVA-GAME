package com.mazeescape.model;

public class Tile {

    public enum Type {
        FLOOR,
        WALL
    }

    private final int row;
    private final int column;
    private final Type type;

    public Tile(int row, int column, Type type) {
        this.row = row;
        this.column = column;
        this.type = type == null ? Type.FLOOR : type;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Type getType() {
        return type;
    }

    public boolean isWall() {
        return type == Type.WALL;
    }
}
