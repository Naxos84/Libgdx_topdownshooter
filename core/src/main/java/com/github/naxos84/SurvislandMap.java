package com.github.naxos84;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
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

    public void loadMap(String fileName) {
        this.map = mapLoader.load(fileName);
        this.mapRenderer.setMap(this.map);
    }

    public void render(OrthographicCamera camera) {
        this.mapRenderer.setView(camera);
        this.mapRenderer.render();
    }

    public Integer getWidth() {
        MapProperties prop = this.map.getProperties();
        Integer mapWidth = prop.get("width", Integer.class);

        return mapWidth * CELL_SIZE;
    }

    public Integer getHeight() {
        MapProperties prop = this.map.getProperties();
        Integer mapHeight = prop.get("height", Integer.class);

        return mapHeight * CELL_SIZE;
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

    public Array<Wall> getWalls() {
        Array<Wall> walls = new Array<>();
        TiledMapTileLayer wallsLayer = (TiledMapTileLayer) this.map.getLayers().get("Walls");
        for (int column = 0; column < wallsLayer.getWidth(); column++) {
            for (int row = 0; row < wallsLayer.getHeight(); row++) {
                Cell cell = wallsLayer.getCell(column, row);
                if (cell != null) {
                    Rectangle wallCollider = new Rectangle((float) column * wallsLayer.getTileWidth(),
                            (float) row * wallsLayer.getTileHeight(), wallsLayer.getTileWidth(),
                            wallsLayer.getTileHeight());

                    Wall wall = new Wall(wallCollider, cell);
                    walls.add(wall);
                }
            }
        }

        return walls;
    }

}
