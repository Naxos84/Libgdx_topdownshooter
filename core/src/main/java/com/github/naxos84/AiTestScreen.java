package com.github.naxos84;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Queue;
import com.github.naxos84.ai.Agent;
import com.github.naxos84.ai.AiGraph;
import com.github.naxos84.ai.AiTile;
import com.github.naxos84.logger.FileLogger;

public class AiTestScreen implements Screen, InputProcessor {

    TmxMapLoader mapLoader = new TmxMapLoader();
    TiledMap map;
    OrthogonalTiledMapRenderer mapRenderer;
    OrthographicCamera camera;

    ShapeRenderer shapeRenderer;
    List<AiTile> aiPath;

    AiGraph uGraph;

    AiTile selectedTile = null;
    Queue<AiTile> selectedTiles = new Queue<>(2);

    Agent agent;
    private Integer mapTileWidth;
    private Integer mapTileHeight;
    private float halfMapTileWidth;
    private float halfMapTileHeight;
    private Integer mapWidth;
    private Integer mapHeight;

    private FileLogger logger = new FileLogger();

    @Override
    public void show() {
        logger.log("Showing AITest Screen");
        Gdx.input.setInputProcessor(this);
        this.map = mapLoader.load("tiled/aitest.tmx");
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

        agent = new Agent(uGraph, 0, 0);

    }

    private void createGraph(int mapWidth, int mapHeight, TiledMapTileLayer obstaclesLayer) {
        this.uGraph = new AiGraph();
        for (int row = 0; row < mapHeight; row++) {
            for (int column = 0; column < mapWidth; column++) {
                Cell cell = obstaclesLayer.getCell(column, row);
                if (cell == null) {
                    // we found a cell that is walkable
                    AiTile aiTile = new AiTile(column * mapTileWidth + halfMapTileWidth,
                            row * mapTileHeight + halfMapTileHeight, mapTileWidth, mapTileHeight,
                            column + ":" + row);

                    AiTile leftNeighbour = this.uGraph.getLeftNeighbour(column, row);
                    AiTile bottomNeighbour = this.uGraph.getBottomNeighbour(column, row);
                    AiTile bottomLeftNeighbour = this.uGraph.getBottomLeftNeighbour(column, row);
                    AiTile bottomRightNeighbour = this.uGraph.getBottomRightNeighbour(column, row, mapWidth);
                    uGraph.addVertex(aiTile);
                    uGraph.connect(aiTile, leftNeighbour);
                    uGraph.connect(aiTile, bottomNeighbour);
                    uGraph.connect(aiTile, bottomLeftNeighbour);
                    uGraph.connect(aiTile, bottomRightNeighbour);

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
        uGraph.renderTiles(shapeRenderer, false);

        this.aiPath = agent.getCurrentPath();

        // Draw cities in path green
        if (aiPath != null) {
            for (AiTile aiTile : aiPath) {
                aiTile.render(shapeRenderer, true);
            }
        }

        agent.step(delta);
        agent.render(shapeRenderer);

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
                clickedTile = new AiTile(x * mapTileWidth + halfMapTileWidth, y * mapTileHeight + halfMapTileHeight,
                        mapTileWidth, mapTileHeight, x + ":" + y);
                this.uGraph.addVertex(clickedTile);
                AiTile topNeighbour = this.uGraph.getTopNeighbur(x, y, mapHeight);
                AiTile bottomNeighbour = this.uGraph.getBottomNeighbour(x, y);
                AiTile leftNeighbour = this.uGraph.getLeftNeighbour(x, y);
                AiTile rightNeighbour = this.uGraph.getRightNeighbour(x, y, mapWidth);
                this.uGraph.connect(clickedTile, topNeighbour);
                this.uGraph.connect(clickedTile, bottomNeighbour);
                this.uGraph.connect(clickedTile, leftNeighbour);
                this.uGraph.connect(clickedTile, rightNeighbour);

            } else {
                // existing tile --> open door
                this.uGraph.removeVertex(clickedTile);

            }
        } else {
            this.selectTile(clickedTile);
        }
        return false;
    }

    private void selectTile(AiTile clickedTile) {
        agent.setGoal(clickedTile);
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
