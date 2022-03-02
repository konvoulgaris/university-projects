package astar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class PathFinder {
    private Grid grid;
    
    public PathFinder(Grid grid) {
        this.grid = grid;
    }

    public ArrayList<Position> findPath(Position start, Position end) {
        ArrayList<Position> path = new ArrayList<Position>();

        ArrayList<Node> pathNodes = findNodesInPath(start, end);

        if(pathNodes != null) {
            for(Node node : pathNodes) {
                path.add(new Position(node));
            }
        }

        return path;
    }

    public Position findNextInPath(Position start, Position end) {
        return new Position(findPath(start, end).get(0));
    }

    // Credit: https://github.com/patrykkrawczyk/2D-A-path-finding-in-Java
    private ArrayList<Node> findNodesInPath(Position start, Position end) {
        Node startNode = grid.nodes[start.x][start.y];
        Node endNode = grid.nodes[end.x][end.y];

        ArrayList<Node> opened = new ArrayList<Node>();
        opened.add(startNode);

        HashSet<Node> closed = new HashSet<Node>();

        while(opened.size() > 0) {
            Node current = opened.get(0);

            for(int n = 1; n < opened.size(); n++) {
                Node next = opened.get(n);

                if(current.getFCost() > next.getFCost() || current.getFCost() == next.getFCost() && current.hCost > next.hCost) {
                    current = next;
                }
            }

            opened.remove(current);
            closed.add(current);

            if(current == endNode) {
                return retracePathNodes(startNode, endNode);
            }

            ArrayList<Node> neighbours = grid.getNeighbours(current);

            for(Node neighbour : neighbours) {
                if(!neighbour.isWalkable() || closed.contains(neighbour)) {
                    continue;
                }

                int cost = current.gCost + manhattanDistance(current, neighbour) * (10 * (neighbour.isWalkable() ? 1 : 0));

                if(cost < neighbour.gCost || !opened.contains(neighbour)) {
                    neighbour.gCost = cost;
                    neighbour.hCost = manhattanDistance(neighbour, endNode);
                    neighbour.parent = current;

                    if(!opened.contains(neighbour)) {
                        opened.add(neighbour);
                    }
                }
            }
        }

        return null;
    }

    private ArrayList<Node> retracePathNodes(Node start, Node end) {
        ArrayList<Node> pathNodes = new ArrayList<Node>();
       
        Node current = end;

        while(current != start) {
            pathNodes.add(current);
            current = current.parent;
        }

        Collections.reverse(pathNodes);

        return pathNodes;
    }

    public static int manhattanDistance(Position a, Position b) {
        return (Math.abs(a.x - b.x) + Math.abs(a.y - b.y));
    }

    public static int manhattanDistance(Node a, Node b) {
        return manhattanDistance(new Position(a), new Position(b));
    }
}
