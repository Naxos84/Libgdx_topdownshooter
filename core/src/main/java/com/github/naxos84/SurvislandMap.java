package com.github.naxos84;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class SurvislandMap {
    private static final int CELL_SIZE = 64;

    private TmxMapLoader mapLoader = new TmxMapLoader();
    private OrthogonalTiledMapRenderer mapRenderer = new OrthogonalTiledMapRenderer(null);

    private TiledMap map;

    private Array<Wall> walls = new Array<>();
    private int width;
    private int height;
    private int mapTileWidth;
    private int mapTileHeight;

    public void loadMap(String fileName) {
        this.map = mapLoader.load(fileName);
        this.mapRenderer.setMap(this.map);
        this.loadDimensions();
        this.loadWalls();
    }

    private void loadDimensions() {
        MapProperties prop = this.map.getProperties();
        Integer mapWidth = prop.get("width", Integer.class);
        Integer mapHeight = prop.get("height", Integer.class);

        this.width = mapWidth * CELL_SIZE;
        this.height = mapHeight * CELL_SIZE;

        this.mapTileWidth = map.getProperties().get("tilewidth", Integer.class);
        this.mapTileHeight = map.getProperties().get("tileheight", Integer.class);
    }

    private void loadWalls() {
        TiledMapTileLayer wallsLayer = (TiledMapTileLayer) this.map.getLayers().get("Walls");
        for (int column = 0; column < wallsLayer.getWidth(); column++) {
            for (int row = 0; row < wallsLayer.getHeight(); row++) {
                Cell cell = wallsLayer.getCell(column, row);
                if (cell != null) {
                    Rectangle wallCollider = new Rectangle((float) column * wallsLayer.getTileWidth(),
                            (float) row * wallsLayer.getTileHeight(), wallsLayer.getTileWidth(),
                            wallsLayer.getTileHeight());

                    Wall wall = new Wall(wallCollider, cell);
                    this.walls.add(wall);
                }
            }
        }
    }

    public void render(OrthographicCamera camera) {
        this.mapRenderer.setView(camera);
        this.mapRenderer.render();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Vector2 getPlayerSpawn() {
        MapLayers mapLayers = map.getLayers();
        MapObjects spawnPoints = mapLayers.get("Spawnpoints").getObjects();
        for (MapObject spawnPoint : spawnPoints) {
            MapProperties spawnPointProperties = spawnPoint.getProperties();
            if (spawnPointProperties != null && spawnPointProperties.containsKey("playerSpawn")) {
                Boolean isPlayerSpawn = spawnPointProperties.get("playerSpawn", Boolean.class);
                if (Boolean.TRUE.equals(isPlayerSpawn)) {
                    Float x = spawnPointProperties.get("x", Float.class);
                    Float y = spawnPointProperties.get("y", Float.class);
                    return new Vector2(x, y);
                }
            }
        }

        Gdx.app.debug("SurvislandMap", "No spawn point found in map. Return map origin.");
        return Vector2.Zero;
    }

    // public Array<Wall> getWalls() {
    // return this.walls;
    // }

    public void toggleAllDoors() {
        for (Wall wall : walls) {
            boolean open = wall.isOpen();
            if (wall.isDoor()) {
                wall.setOpen(!open);
            }
        }
    }

    public void toggleDoorAt(float x, float y, Rectangle blockingCollider) {
        for (Wall wall : walls) {
            if (wall.isDoor() && wall.getCollider().contains(x, y)) {
                if (blockingCollider == null) {
                    wall.toggle();
                } else if (!wall.getCollider().overlaps(blockingCollider)) {
                    wall.toggle();
                }
            }
        }
    }

    public void renderWallsDebug(ShapeRenderer debugRenderer) {
        for (Wall wall : walls) {
            Rectangle wallCollider = wall.getCollider();
            if (wall.isCollidable()) {
                debugRenderer.rect(wallCollider.x, wallCollider.y, wallCollider.width, wallCollider.height);
            }
        }
    }

    public boolean isCollidingWithWall(Collidable collidable) {
        for (Wall wall : walls) {
            if (wall.collidesWidth(collidable)) {
                return true;
            }
        }
        return false;
    }

    public float getTileWidth() {
        return mapTileWidth;
    }

    public float getTileHeight() {
        return mapTileHeight;
    }

}
