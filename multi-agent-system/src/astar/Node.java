package astar;

public class Node {
    public int x;
    public int y;

    public int type;

    public int gCost;
    public int hCost;
    
    public Node parent;

    public Node(int x, int y, int type) {
        this.x = x;
        this.y = y;
        
        this.type = type;
    }

    public Node(Node other) {
        this.x = other.x;
        this.y = other.y;

        this.type = other.type;

        this.gCost = other.gCost;
        this.hCost = other.hCost;

        this.parent = other.parent;
    }

    public int getFCost() {
        return (gCost + hCost);
    }

    public boolean isWalkable() {
        return type != -1;
    }

    @Override
    public String toString() {
        return String.format("Node => { (%d, %d) = %d }", x, y, type);
    }
}
