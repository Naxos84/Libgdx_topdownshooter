package com.github.naxos84.ai;

import java.util.List;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import squidpony.squidai.graph.Edge;
import squidpony.squidai.graph.Heuristic;
import squidpony.squidai.graph.UndirectedGraph;

public class AiGraph extends UndirectedGraph<AiTile> {

    int mapWidth;
    int mapHeight;

    public AiGraph(int width, int height) {
        mapWidth = width;
        mapHeight = height;
    }

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
        for (AiTile aiTile : this.getVertices()) {
            if (aiTile.gridX == column && aiTile.gridY == row) {
                return aiTile;
            }
        }
        return null;
    }

    public void addTile(AiTile tile, int i) {
        this.addVertex(tile);

        if ((Direction.TOP & i) > 0) {
            AiTile topNeighbour = this.getTopNeighbur(tile.gridX, tile.gridY, mapHeight);
            this.connect(tile, topNeighbour);
        }
        if ((Direction.TOP_LEFT & i) > 0) {
            throw new UnsupportedOperationException("Top-left not implemented yet");
        }
        if ((Direction.LEFT & i) > 0) {
            AiTile leftNeighbour = this.getLeftNeighbour(tile.gridX, tile.gridY);
            this.connect(tile, leftNeighbour);
        }
        if ((Direction.BOTTOM_LEFT & i) > 0) {
            AiTile bottomLeftNeighbour = this.getBottomLeftNeighbour(tile.gridX, tile.gridY);
            this.connect(tile, bottomLeftNeighbour);
        }
        if ((Direction.BOTTOM & i) > 0) {
            AiTile bottomNeighbour = this.getBottomNeighbour(tile.gridX, tile.gridY);
            this.connect(tile, bottomNeighbour);
        }
        if ((Direction.BOTTOM_RIGHT & i) > 0) {
            AiTile bottomRightNeighbour = this.getBottomRightNeighbour(tile.gridX, tile.gridY, mapWidth);
            connect(tile, bottomRightNeighbour);
        }
        if ((Direction.RIGHT & i) > 0) {
            AiTile rightNeighbour = this.getRightNeighbour(tile.gridX, tile.gridY, mapWidth);
            this.connect(tile, rightNeighbour);
        }
        if ((Direction.TOP_RIGHT & i) > 0) {
            throw new UnsupportedOperationException("Top-right not implemented yet");
        }

    }

}
