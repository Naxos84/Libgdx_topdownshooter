package com.github.naxos84.map;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.github.naxos84.ai.AiTile;

public class Door extends Wall implements Toggleable {

    public Door(int gridX, int gridY, Rectangle wallCollider, Cell cell, AiTile tile) {
        super(gridX, gridY, wallCollider, cell, tile);
    }

    @Override
    public void toggle() {
        System.out.println("Toggling door" + this);
        setOpen(!isOpen());
    }

    public void setOpen(boolean isOpen) {
        int currentRotation = this.cell.getRotation();
        if (isOpen) {
            this.cell.setRotation(currentRotation + 1);
        } else {
            this.cell.setRotation(currentRotation - 1);
        }
    }

    public boolean isOpen() {

        return !isClosed();
    }

    public boolean isClosed() {
        int currentRotation = this.cell.getRotation();
        return currentRotation == 0;
    }

    @Override
    public boolean isCollidable() {
        return !isOpen();
    }

    @Override
    public String toString() {
        return String.format("Door: %f:%f - %f:%f (isOpen: %b)", wallCollider.x, wallCollider.y, wallCollider.width,
                wallCollider.height, isOpen());
    }

}
