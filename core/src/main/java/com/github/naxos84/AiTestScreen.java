package com.github.naxos84;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Queue;
import com.github.naxos84.ai.Agent;
import com.github.naxos84.ai.AiGraph;
import com.github.naxos84.ai.AiTile;
import com.github.naxos84.ai.Direction;
import com.github.naxos84.logger.FileLogger;

import squidpony.squidmath.Coord;

public class AiTestScreen implements Screen, InputProcessor {

    TmxMapLoader mapLoader = new TmxMapLoader();
    TiledMap map;
    OrthogonalTiledMapRenderer mapRenderer;
    OrthographicCamera camera;

    ShapeRenderer shapeRenderer;
    private Random random = new Random();

    AiGraph uGraph;

    List<Agent> agents = new ArrayList<>();
    List<Coord> zSpawns = new ArrayList<>();

    private Integer mapTileWidth;
    private Integer mapTileHeight;
    private float halfMapTileWidth;
    private float halfMapTileHeight;
    private Integer mapWidth;
    private Integer mapHeight;

    List<AiTile> doorsToClose = new ArrayList<>();

    private FileLogger logger = new FileLogger();

    @Override
    public void show() {
        logger.log("Showing AITest Screen");
        Gdx.input.setInputProcessor(this);
        this.map = mapLoader.load("tiled/aitest.tmx");
        for (MapLayer layer : map.getLayers()) {
            if (Boolean.TRUE == layer.getProperties().get("skipRender", Boolean.class)) {
                layer.setVisible(false);
            }
        }
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        this.mapWidth = map.getProperties().get("width", Integer.class);
        this.mapHeight = map.getProperties().get("height", Integer.class);
        this.mapTileWidth = map.getProperties().get("tilewidth", Integer.class);
        this.mapTileHeight = map.getProperties().get("tileheight", Integer.class);
        this.halfMapTileWidth = mapTileWidth / 2f;
        this.halfMapTileHeight = mapTileHeight / 2f;
        camera = new OrthographicCamera();
        // show whole map
        camera.setToOrtho(false, mapWidth * mapTileWidth, mapHeight * mapTileWidth);

        TiledMapTileLayer obstaclesLayer = (TiledMapTileLayer) map.getLayers().get("Obstacles");

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setAutoShapeType(true);

        createGraph(mapWidth, mapHeight, obstaclesLayer);
        getZSpawns();
        for (int i = 0; i < 100; i++) {
            spawnZ();
        }

        // agents.add(new Agent(uGraph, 0, 0));
        // agents.add(new Agent(uGraph, 0, 0));

    }

    private void spawnZ() {
        int gridSpawnLocation = random.nextInt(0, zSpawns.size());
        Coord coord = zSpawns.get(gridSpawnLocation);
        agents.add(new Agent(uGraph, coord.x, coord.y));

    }

    private void getZSpawns() {
        TiledMapTileLayer zspawnLayer = (TiledMapTileLayer) map.getLayers().get("Z_Spawn");
        for (int row = 0; row < mapHeight; row++) {
            for (int column = 0; column < mapWidth; column++) {
                Cell cell = zspawnLayer.getCell(column, row);
                if (cell != null) {
                    zSpawns.add(Coord.get(column, row));
                }
            }
        }
    }

    private void createGraph(int mapWidth, int mapHeight, TiledMapTileLayer obstaclesLayer) {
        this.uGraph = new AiGraph(mapWidth, mapHeight);
        for (int row = 0; row < mapHeight; row++) {
            for (int column = 0; column < mapWidth; column++) {
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

    @Override
    public void render(float delta) {
        mapRenderer.setView(camera);
        mapRenderer.render();
        uGraph.renderConnections(shapeRenderer);
        // uGraph.renderTiles(shapeRenderer, false);

        boolean tileHasBeenRemoved = false;
        doorsToClose.forEach(d -> d.canBeRemoved = true);
        doorsToClose.forEach(d -> {
            shapeRenderer.begin(ShapeType.Filled);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(d.x - 32, d.y - 32, 64f, 64f);
            shapeRenderer.end();
        });
        for (Agent agent : agents) {
            if (agent.isIdle) {
                agent.setGoal(uGraph.getRandomTile());
            }
            agent.step(delta);
            agent.render(shapeRenderer);
            Rectangle rect = new Rectangle();
            doorsToClose.forEach(d -> {
                rect.set(d.x - 32, d.y - 32, 64f, 64f);
                if (rect.contains(agent.x, agent.y)) {
                    d.canBeRemoved &= false;
                }

            });

        }
        for (Iterator<AiTile> doorIterator = doorsToClose.iterator(); doorIterator.hasNext();) {
            AiTile door = doorIterator.next();
            if (door.canBeRemoved) {
                tileHasBeenRemoved |= true;
                uGraph.removeTile(door);
                doorIterator.remove();
            }
        }
        if (tileHasBeenRemoved) {
            agents.forEach(Agent::recalculatePath);
        }

        // for each agent
        // for each door
        // if agent overlaps with a door
        // do nothing
        // else
        // markDoorTobeClosed
        // close all doors that are marked to be closed
        // for each agent
        // recalculatePath
    }

    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        mapRenderer.dispose();
        // TODO Auto-generated method stub

    }

    @Override
    public boolean keyDown(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // button= 0 -> leftclick ; 1 -> rightclick
        Vector2 clickedWorldCoordinate = toWorldCoordinates(screenX, screenY);
        int x = (int) clickedWorldCoordinate.x;
        int y = (int) clickedWorldCoordinate.y;
        AiTile clickedTile = this.uGraph.findTileByGridPosition(x, y);
        if (button == 1) {
            if (clickedTile == null) {
                // no tile exists yet --> door closed
                clickedTile = new AiTile(x * mapTileWidth + halfMapTileWidth, y * mapTileHeight + halfMapTileHeight, x,
                        y);
                this.uGraph.addTile(clickedTile, Direction.TOP | Direction.BOTTOM | Direction.LEFT | Direction.RIGHT);

            } else {
                doorsToClose.add(clickedTile);
                // existing tile --> open door

            }
        } else {
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // TODO Auto-generated method stub
        return false;
    }

    public Vector2 toWorldCoordinates(float x, float y) {
        Vector3 mouseInWorldPosition = this.camera.unproject(new Vector3(x, y, 0));
        return new Vector2((float) Math.floor(mouseInWorldPosition.x / mapTileWidth),
                (float) Math.floor(mouseInWorldPosition.y / mapTileHeight));
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // TODO Auto-generated method stub
        return false;
    }

}
