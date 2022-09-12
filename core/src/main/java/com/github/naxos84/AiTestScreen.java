package com.github.naxos84;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.github.naxos84.ai.AiTile;
import com.github.naxos84.ai.AiTileConnection;
import com.github.naxos84.ai.AiTileGraph;

public class AiTestScreen implements Screen, InputProcessor {

    TmxMapLoader mapLoader = new TmxMapLoader();
    TiledMap map;
    OrthogonalTiledMapRenderer mapRenderer;
    OrthographicCamera camera;

    AiTileGraph aiTileGraph;
    ShapeRenderer shapeRenderer;
    SpriteBatch batch;
    BitmapFont font;
    GraphPath<AiTile> aiPath;

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
        this.map = mapLoader.load("tiled/aitest.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        int mapWidth = map.getProperties().get("width", Integer.class);
        int mapHeight = map.getProperties().get("height", Integer.class);
        int mapTileWidth = map.getProperties().get("tilewidth", Integer.class);
        camera = new OrthographicCamera();
        // show whole map
        camera.setToOrtho(false, mapWidth * mapTileWidth, mapHeight * mapTileWidth);

        TiledMapTileLayer obstaclesLayer = (TiledMapTileLayer) map.getLayers().get("Obstacles");

        aiTileGraph = new AiTileGraph();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setAutoShapeType(true);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        font = new BitmapFont();

        for (int row = 0; row < mapHeight; row++) {
            for (int column = 0; column < mapWidth; column++) {
                Cell cell = obstaclesLayer.getCell(column, row);
                if (cell == null) {
                    // we found a cell that is walkable
                    AiTile aiTile = new AiTile(column * 64 + 32, row * 64 + 32, 64, 64,
                            column + ":" + row);

                    AiTile leftNeighbour = getLeftNeighbour(column, row);
                    AiTile bottomNeighbour = getBottomNeighbour(column, row);
                    AiTile bottomLeftNeighbour = getBottomLeftNeighbour(column, row);
                    AiTile bottomRightNeighbour = getBottomRightNeighbour(column, row, mapWidth);
                    aiTileGraph.addAiTile(aiTile);
                    if (leftNeighbour != null) {
                        aiTileGraph.connectAiTiles(aiTile, leftNeighbour, true);
                    }
                    if (bottomNeighbour != null) {
                        aiTileGraph.connectAiTiles(aiTile, bottomNeighbour, true);
                    }
                    if (bottomLeftNeighbour != null) {
                        aiTileGraph.connectAiTiles(aiTile, bottomLeftNeighbour, true);
                    }
                    if (bottomRightNeighbour != null) {
                        aiTileGraph.connectAiTiles(aiTile, bottomRightNeighbour, true);
                    }
                } else {
                    // found a blocked cell -> ignore it
                }
            }
        }
        AiTile startTile = findTileByGridPosition(0, 0);
        AiTile targetTile = findTileByGridPosition(7, 13);

        aiPath = aiTileGraph.findPath(startTile, targetTile);

    }

    private AiTile getBottomNeighbour(int column, int row) {
        if (row == 0) {
            return null;
        }
        return findTileByGridPosition(column, row - 1);
    }

    private AiTile getLeftNeighbour(int column, int row) {
        if (column == 0) {
            return null;
        }
        return findTileByGridPosition(column - 1, row);
    }

    private AiTile getBottomLeftNeighbour(int column, int row) {
        if (row == 0 || column == 0) {
            return null;
        }
        return findTileByGridPosition(column - 1, row - 1);
    }

    private AiTile getBottomRightNeighbour(int column, int row, int mapWidth) {
        if (row == 0 || column == mapWidth) {
            return null;
        }
        return findTileByGridPosition(column + 1, row - 1);
    }

    private AiTile findTileByGridPosition(int column, int row) {
        String name = column + ":" + row;
        for (AiTile aiTile : aiTileGraph.aiTiles) {
            if (aiTile.name.equals(name)) {
                return aiTile;
            }
        }

        return null;
    }

    @Override
    public void render(float delta) {
        mapRenderer.setView(camera);
        mapRenderer.render();
        for (AiTileConnection aiTileConnection : aiTileGraph.aiTileConnections) {
            aiTileConnection.render(shapeRenderer);
        }

        // Draw all cities blue
        for (AiTile aiTile : aiTileGraph.aiTiles) {
            aiTile.render(shapeRenderer, batch, font, false);
        }

        // Draw cities in path green
        for (AiTile aiTile : aiPath) {
            aiTile.render(shapeRenderer, batch, font, true);
        }

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
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Vector3 mouseInWorlPosition = this.camera.unproject(new Vector3(screenX, screenY, 0));
        Vector2 mouseInWorldCoordinates = toWorldCoordinates(mouseInWorlPosition.x, mouseInWorlPosition.y);

        System.out.println(mouseInWorldCoordinates.x + ":" + mouseInWorldCoordinates.y);
        AiTile startTile = findTileByGridPosition(0, 0);
        AiTile targetTile = findTileByGridPosition((int) mouseInWorldCoordinates.x, (int) mouseInWorldCoordinates.y);
        if (targetTile != null) {
            aiPath = aiTileGraph.findPath(startTile, targetTile);
        }
        // TODO Auto-generated method stub
        return false;
    }

    public Vector2 toWorldCoordinates(float x, float y) {
        return new Vector2((float) Math.floor(x / 64), (float) Math.floor(y / 64));
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // TODO Auto-generated method stub
        return false;
    }

}
