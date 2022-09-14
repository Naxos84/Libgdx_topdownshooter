package com.github.naxos84.ai;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class AiTileGraph implements IndexedGraph<AiTile> {
    private AiTileHeuristic aiTileHeuristic = new AiTileHeuristic();
    private Array<AiTile> aiTiles = new Array<>();
    private Array<Connection<AiTile>> aiTileConnections = new Array<>();

    /** Map of AiTiles to AiTileConnections starting in that AiTile. */
    private ObjectMap<AiTile, Array<Connection<AiTile>>> connectionsMap = new ObjectMap<>();

    private int lastNodeIndex = 0;

    public void addAiTile(AiTile aiTile) {
        aiTile.index = lastNodeIndex;
        lastNodeIndex++;

        aiTiles.add(aiTile);
    }

    private void connectAiTiles(AiTile fromTile, AiTile toTile, boolean bidirectional) {
        Connection<AiTile> aiTileConnection = new AiTileConnection(fromTile, toTile);
        if (!connectionsMap.containsKey(fromTile)) {
            connectionsMap.put(fromTile, new Array<>());
        }
        connectionsMap.get(fromTile).add(aiTileConnection);
        aiTileConnections.add(aiTileConnection);
        if (bidirectional) {
            connectAiTiles(toTile, fromTile, false);
        }
    }

    public GraphPath<AiTile> findPath(AiTile startTile, AiTile targetTile) {
        GraphPath<AiTile> tilePath = new DefaultGraphPath<>();
        new IndexedAStarPathFinder<>(this).searchNodePath(startTile, targetTile, aiTileHeuristic, tilePath);
        return tilePath;
    }

    @Override
    public int getIndex(AiTile node) {
        return node.index;
    }

    @Override
    public int getNodeCount() {
        return lastNodeIndex;
    }

    @Override
    public Array<Connection<AiTile>> getConnections(AiTile fromNode) {
        if (connectionsMap.containsKey(fromNode)) {
            return connectionsMap.get(fromNode);
        }

        return new Array<>(0);
    }

    @Override
    public String toString() {
        return "AiTileGraph [aiTileConnections=" + aiTileConnections + ", aiTiles=" + aiTiles + "]";
    }

    public AiTile findTileByName(String name) {

        for (AiTile aiTile : this.aiTiles) {
            if (aiTile.name.equals(name)) {
                return aiTile;
            }
        }

        return null;
    }

    // extract it to a graph renderer
    public void renderConnections(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 1);
        for (Connection<AiTile> aiTileConnection : this.aiTileConnections) {
            shapeRenderer.rectLine(aiTileConnection.getFromNode().x, aiTileConnection.getFromNode().y,
                    aiTileConnection.getToNode().x, aiTileConnection.getToNode().y, 2);
        }
        shapeRenderer.end();
    }

    // extract it to a graph renderer
    public void renderTiles(ShapeRenderer shapeRenderer, boolean inPath) {
        // Draw all cities blue
        for (AiTile aiTile : this.aiTiles) {
            aiTile.render(shapeRenderer, inPath);
        }
    }

    public void connect(AiTile aiTile, AiTile neighbour, boolean bidirectional) {
        if (aiTile != null && neighbour != null) {
            this.connectAiTiles(aiTile, neighbour, bidirectional);
        }
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

    public void remove(AiTile clickedTile) {
        disconnect(clickedTile);
        aiTiles.removeValue(clickedTile, true);
    }

    private void disconnect(AiTile fromTile) {
        Array<Connection<AiTile>> removedConnections = null;
        if (connectionsMap.containsKey(fromTile)) {
            removedConnections = connectionsMap.remove(fromTile);
        }
        if (removedConnections != null) {
            for (Connection<AiTile> connection : removedConnections) {

                disconnect(connection.getToNode(), connection.getFromNode());
                this.aiTileConnections.removeValue(connection, false);

            }
        }
    }

    private void disconnect(AiTile toTile, AiTile fromTile) {
        Array<Connection<AiTile>> connections = connectionsMap.get(toTile, null);
        if (connections != null) {
            for (Connection<AiTile> connection : connections) {
                if (connection.getToNode() == fromTile) {
                    // found a connection back
                    this.aiTileConnections.removeValue(connection, false);
                }
            }
        }

    }

}
