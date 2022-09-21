package com.github.naxos84;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import com.github.naxos84.ai.Agent;
import com.github.naxos84.ai.AiGraph;
import com.github.naxos84.ai.AiTile;
import com.github.naxos84.ai.Direction;

import squidpony.squidmath.Coord;

public class SurvislandMap {
    private static final int CELL_SIZE = 64;

    Random random = new Random();

    private TmxMapLoader mapLoader = new TmxMapLoader();
    private OrthogonalTiledMapRenderer mapRenderer = new OrthogonalTiledMapRenderer(null);
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    private TiledMap map;

    private Array<Wall> walls = new Array<>();
    List<Coord> zSpawns = new ArrayList<>();
    List<Agent> agents = new ArrayList<>();
    private int gridWidth;
    private int gridHeight;
    private int width;
    private int height;
    private int mapTileWidth;
    private int mapTileHeight;
    private float halfMapTileWidth;
    private float halfMapTileHeight;

    private AiGraph uGraph;

    public void loadMap(String fileName) {
        this.map = mapLoader.load(fileName);
        this.mapRenderer.setMap(this.map);
        this.loadDimensions();
        this.loadWalls();
        createGraph();
        createZSpawns();
        for (int i = 0; i < 100; i++) {
            spawnZ();
        }
    }

    private void loadDimensions() {
        MapProperties prop = this.map.getProperties();
        this.gridWidth = prop.get("width", Integer.class);
        this.gridHeight = prop.get("height", Integer.class);

        this.width = gridWidth * CELL_SIZE;
        this.height = gridHeight * CELL_SIZE;

        this.mapTileWidth = map.getProperties().get("tilewidth", Integer.class);
        this.mapTileHeight = map.getProperties().get("tileheight", Integer.class);
        this.halfMapTileWidth = mapTileWidth / 2f;
        this.halfMapTileHeight = mapTileHeight / 2f;
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

                    Wall wall = new Wall(column, row, wallCollider, cell);
                    this.walls.add(wall);
                }
            }
        }
    }

    private void createGraph() {
        TiledMapTileLayer obstaclesLayer = (TiledMapTileLayer) map.getLayers().get("Walls");
        this.uGraph = new AiGraph(this.gridWidth, this.gridHeight);
        for (int row = 0; row < this.gridHeight; row++) {
            for (int column = 0; column < this.gridWidth; column++) {
                Cell cell = obstaclesLayer.getCell(column, row);
                if (cell == null) {
                    // we found a cell that is walkable
                    AiTile aiTile = new AiTile(column * mapTileWidth + halfMapTileWidth,
                            row * mapTileHeight + halfMapTileHeight, column, row);
                    this.uGraph.addTile(aiTile,
                            Direction.LEFT | Direction.BOTTOM | Direction.BOTTOM_LEFT | Direction.BOTTOM_RIGHT);
                } else {
                    // found a blocked cell -> ignore it
                }
            }
        }
    }

    private void createZSpawns() {
        TiledMapTileLayer zspawnLayer = (TiledMapTileLayer) map.getLayers().get("Z_Spawn");
        for (int row = 0; row < this.width; row++) {
            for (int column = 0; column < this.width; column++) {
                Cell cell = zspawnLayer.getCell(column, row);
                if (cell != null) {
                    zSpawns.add(Coord.get(column, row));
                }
            }
        }
        System.out.println("Created " + zSpawns.size() + " spawns.");
    }

    private void spawnZ() {
        int gridSpawnLocation = random.nextInt(0, zSpawns.size());
        Coord coord = zSpawns.get(gridSpawnLocation);
        agents.add(new Agent(uGraph, coord.x, coord.y));

    }

    public void render(OrthographicCamera camera, float delta) {
        this.mapRenderer.setView(camera);
        this.mapRenderer.render();
        shapeRenderer.setProjectionMatrix(camera.combined);
        uGraph.renderConnections(shapeRenderer);

        for (Agent agent : agents) {
            if (agent.isIdle) {
                agent.setGoal(uGraph.getRandomTile());
            }
            agent.step(delta);
            agent.render(shapeRenderer);

        }
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

    private Wall findWallByGridPosition(int gridX, int gridY) {
        for (Wall wall : walls) {
            if (wall.getGridX() == gridX && wall.getGridY() == gridY) {
                return wall;
            }
        }
        return null;
    }

    public void toggleDoorAt(float x, float y, Rectangle blockingCollider) {
        int gridX = (int) x / CELL_SIZE;
        int gridY = (int) y / CELL_SIZE;
        System.out.println("Toggling door at " + gridX + ":" + gridY);
        AiTile doorTile = this.uGraph.findTileByGridPosition(gridX, gridY);
        Wall wall = this.findWallByGridPosition(gridX, gridY);
        if (doorTile == null && wall != null) {
            // no tile exists yet --> door closed
            doorTile = new AiTile(gridX * mapTileWidth + halfMapTileWidth, gridY * mapTileHeight + halfMapTileHeight,
                    gridX,
                    gridY);
            this.uGraph.addTile(doorTile, Direction.TOP | Direction.BOTTOM | Direction.LEFT | Direction.RIGHT);

            if (wall.isDoor() && wall.getCollider().contains(x, y)) {
                if (blockingCollider == null) {
                    wall.toggle();
                } else if (!wall.getCollider().overlaps(blockingCollider)) {
                    wall.toggle();
                }
            }

        } else if (doorTile != null && wall != null) {
            Rectangle rect = new Rectangle(doorTile.x, doorTile.y, 64f, 64f);
            boolean closeDoor = true;
            for (Agent agent : agents) {
                if (rect.contains(agent.x, agent.y)) {
                    closeDoor = false;
                    break;
                }
            }
            Wall wallToClose = null;
            if (wall.isDoor() && wall.getCollider().contains(x, y)) {
                if (blockingCollider == null) {
                    System.out.println("Check why blocking collider is null");
                } else if (!wall.getCollider().overlaps(blockingCollider)) {
                    wallToClose = wall;
                    closeDoor &= true;
                    System.out.println("Found wall to close " + wall);
                    // wall.toggle();
                } else {
                    closeDoor = false;
                }
            }
            if (closeDoor) {
                wallToClose.toggle();
                this.uGraph.removeTile(doorTile);
                agents.forEach(Agent::recalculatePath);
            }

        } else {
            System.out.println("No door found to toggle");
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
