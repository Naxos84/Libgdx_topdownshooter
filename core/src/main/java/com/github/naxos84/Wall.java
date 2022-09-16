package com.github.naxos84;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;

public class Wall implements Collidable {

    private Rectangle wallCollider;
    private Cell cell;
    private boolean isDoor;

    public Wall(Rectangle wallCollider, Cell cell) {
        this.wallCollider = wallCollider;
        this.cell = cell;

        if (cell.getTile().getProperties().containsKey("isDoor")) {
            this.isDoor = cell.getTile().getProperties().get("isDoor", Boolean.class);
        }
    }

    @Override
    public Rectangle getCollider() {
        return wallCollider;
    }

    public void setWallCollider(Rectangle wallCollider) {
        this.wallCollider = wallCollider;
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public void toggle() {
        if (isDoor) { // TODO add interface toggleable
            setOpen(!isOpen());
        }
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
        int currentRotation = this.cell.getRotation();
        return currentRotation != 0;
    }

    public boolean isDoor() {
        return this.isDoor;
    }

    public boolean collidesWidth(Collidable collidable) {
        if (isCollidable()) {
            return this.wallCollider.overlaps(collidable.getCollider());
        }
        return false;

    }

    public boolean isCollidable() {

        return !(isDoor() && isOpen());
    }

}
