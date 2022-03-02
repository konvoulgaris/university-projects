package astar;

public class Position {
    public int x;
    public int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position(Position other) {
        x = other.x;
        y = other.y;
    }
    
    public Position(Node node) {
        x = node.x;
        y = node.y;
    }

    @Override
    public boolean equals(Object object) {
        if(object instanceof Position) {
            Position other = (Position)(object);
            return (x == other.x && y == other.y);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("Position => (%d, %d)", x, y);
    }
}
