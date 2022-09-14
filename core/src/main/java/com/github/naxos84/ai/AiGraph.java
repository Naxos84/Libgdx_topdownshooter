package com.github.naxos84.ai;

import java.util.List;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import squidpony.squidai.graph.Edge;
import squidpony.squidai.graph.Heuristic;
import squidpony.squidai.graph.UndirectedGraph;

public class AiGraph extends UndirectedGraph<AiTile> {

    Heuristic<AiTile> heuristic = new AiGraphHeuristic();

    public List<AiTile> findPath(AiTile startTile, AiTile targetTile) {
        return algorithms().findShortestPath(startTile, targetTile, heuristic);
    }

    // extract it to a graph renderer
    public void renderConnections(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 1);
        for (Edge<AiTile> aiTileConnection : this.getEdges()) {
            shapeRenderer.rectLine(aiTileConnection.getA().x, aiTileConnection.getA().y,
                    aiTileConnection.getB().x, aiTileConnection.getB().y, 2);
        }
        shapeRenderer.end();
    }

    // extract it to a graph renderer
    public void renderTiles(ShapeRenderer shapeRenderer, boolean inPath) {
        // Draw all cities blue
        for (AiTile aiTile : this.getVertices()) {
            aiTile.render(shapeRenderer, inPath);
        }
    }

    public void connect(AiTile from, AiTile to) {
        if (from == null || to == null) {
            return;
        }
        addEdge(from, to);
    }

    public AiTile getBottomNeighbour(int column, int row) {
        if (row == 0) {
            return null;
        }
        return findTileByGridPosition(column, row - 1);
    }

    public AiTile getLeftNeighbour(int column, int row) {
        if (column == 0) {
            return null;
        }
        return findTileByGridPosition(column - 1, row);
    }

    public AiTile getRightNeighbour(int column, int row, int mapWidth) {
        if (column == mapWidth) {
            return null;
        }
        return findTileByGridPosition(column + 1, row);
    }

    public AiTile getTopNeighbur(int column, int row, int mapHeight) {
        if (row == mapHeight) {
            return null;
        }
        return findTileByGridPosition(column, row + 1);
    }

    public AiTile getBottomLeftNeighbour(int column, int row) {
        if (row == 0 || column == 0) {
            return null;
        }
        return findTileByGridPosition(column - 1, row - 1);
    }

    public AiTile getBottomRightNeighbour(int column, int row, int mapWidth) {
        if (row == 0 || column == mapWidth) {
            return null;
        }
        return findTileByGridPosition(column + 1, row - 1);
    }

    // TODO can we make this private?
    public AiTile findTileByGridPosition(int column, int row) {
        String name = column + ":" + row;
        return this.findTileByName(name);
    }

    public AiTile findTileByName(String name) {

        for (AiTile aiTile : this.getVertices()) {
            if (aiTile.name.equals(name)) {
                return aiTile;
            }
        }

        return null;
    }

}
