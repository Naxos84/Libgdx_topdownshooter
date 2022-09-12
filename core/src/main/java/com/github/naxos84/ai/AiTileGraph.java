package com.github.naxos84.ai;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class AiTileGraph implements IndexedGraph<AiTile> {
    public AiTileHeuristic aiTileHeuristic = new AiTileHeuristic();
    public Array<AiTile> aiTiles = new Array<>();
    public Array<AiTileConnection> aiTileConnections = new Array<>();

    /** Map of AiTiles to AiTileConnections starting in that AiTile. */
    ObjectMap<AiTile, Array<Connection<AiTile>>> connectionsMap = new ObjectMap<>();

    private int lastNodeIndex = 0;

    public void addAiTile(AiTile aiTile) {
        aiTile.index = lastNodeIndex;
        lastNodeIndex++;

        aiTiles.add(aiTile);
    }

    public void connectAiTiles(AiTile fromTile, AiTile toTile, boolean bidirectional) {
        AiTileConnection aiTileConnection = new AiTileConnection(fromTile, toTile);
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

}
