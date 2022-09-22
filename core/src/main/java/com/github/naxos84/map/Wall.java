package com.github.naxos84.map;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.github.naxos84.Collidable;
import com.github.naxos84.ai.AiTile;

public class Wall extends MapTile implements Collidable {

    protected Rectangle wallCollider;
    protected Cell cell;
    protected int gridX;
    protected int gridY;

    public Wall(int gridX, int gridY, Rectangle wallCollider, Cell cell, AiTile tile) {
        super(tile);
        this.gridX = gridX;
        this.gridY = gridY;
        this.wallCollider = wallCollider;
        this.cell = cell;
    }

    public int getGridX() {
        return this.gridX;
    }

    public int getGridY() {
        return this.gridY;
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

    public boolean collidesWidth(Collidable collidable) {
        if (isCollidable()) {
            return this.wallCollider.overlaps(collidable.getCollider());
        }
        return false;

    }

    public boolean isCollidable() {

        return true;
    }

    @Override
    public String toString() {
        return String.format("Wall: %f:%f - %f:%f", wallCollider.x, wallCollider.y, wallCollider.width,
                wallCollider.height);
    }

}
