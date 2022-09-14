package com.github.naxos84;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.pfa.GraphPath;
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
import com.github.naxos84.ai.AiTile;
import com.github.naxos84.ai.AiTileConnection;
import com.github.naxos84.ai.AiTileGraph;
import com.github.naxos84.logger.FileLogger;

public class AiTestScreen implements Screen, InputProcessor {

    TmxMapLoader mapLoader = new TmxMapLoader();
    TiledMap map;
    OrthogonalTiledMapRenderer mapRenderer;
    OrthographicCamera camera;

    AiTileGraph aiTileGraph;
    ShapeRenderer shapeRenderer;
    GraphPath<AiTile> aiPath;

    AiTile selectedTile = null;
    Queue<AiTile> selectedTiles = new Queue<AiTile>(2);

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

        agent = new Agent(aiTileGraph, 0, 0);

    }

    private void createGraph(int mapWidth, int mapHeight, TiledMapTileLayer obstaclesLayer) {
        this.aiTileGraph = new AiTileGraph();
        for (int row = 0; row < mapHeight; row++) {
            for (int column = 0; column < mapWidth; column++) {
                Cell cell = obstaclesLayer.getCell(column, row);
                if (cell == null) {
                    // we found a cell that is walkable
                    AiTile aiTile = new AiTile(column * mapTileWidth + halfMapTileWidth,
                            row * mapTileHeight + halfMapTileHeight, mapTileWidth, mapTileHeight,
                            column + ":" + row);

                    AiTile leftNeighbour = this.aiTileGraph.getLeftNeighbour(column, row);
                    AiTile bottomNeighbour = this.aiTileGraph.getBottomNeighbour(column, row);
                    AiTile bottomLeftNeighbour = this.aiTileGraph.getBottomLeftNeighbour(column, row);
                    AiTile bottomRightNeighbour = this.aiTileGraph.getBottomRightNeighbour(column, row, mapWidth);
                    this.aiTileGraph.addAiTile(aiTile);

                    this.aiTileGraph.connect(aiTile, leftNeighbour, true);
                    this.aiTileGraph.connect(aiTile, bottomNeighbour, true);
                    this.aiTileGraph.connect(aiTile, bottomLeftNeighbour, true);
                    this.aiTileGraph.connect(aiTile, bottomRightNeighbour, true);

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
        aiTileGraph.renderConnections(shapeRenderer);
        aiTileGraph.renderTiles(shapeRenderer, false);

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
        AiTile clickedTile = this.aiTileGraph.findTileByGridPosition(x, y);
        if (button == 1) {
            if (clickedTile == null) {
                // no tile exists yet --> door closed
                clickedTile = new AiTile(x * mapTileWidth + halfMapTileWidth, y * mapTileHeight + halfMapTileHeight,
                        mapTileWidth, mapTileHeight, x + ":" + y);
                this.aiTileGraph.addAiTile(clickedTile);
                AiTile topNeighour = this.aiTileGraph.getTopNeighbur(x, y, mapHeight);
                AiTile bottomNeighbour = this.aiTileGraph.getBottomNeighbour(x, y);
                AiTile leftNeighbour = this.aiTileGraph.getLeftNeighbour(x, y);
                AiTile rightNeighbour = this.aiTileGraph.getRightNeighbour(x, y, mapWidth);
                this.aiTileGraph.connect(topNeighour, clickedTile, true);
                this.aiTileGraph.connect(bottomNeighbour, clickedTile, true);
                this.aiTileGraph.connect(leftNeighbour, clickedTile, true);
                this.aiTileGraph.connect(rightNeighbour, clickedTile, true);

            } else {
                // existing tile --> open door
                this.aiTileGraph.remove(clickedTile);

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
