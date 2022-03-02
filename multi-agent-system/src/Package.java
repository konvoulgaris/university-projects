import java.util.Random;

import astar.Position;

public class Package {
    static final int WEIGHT_WHITE  = 0;
    static final int WEIGHT_YELLOW = 1;
    static final int WEIGHT_CYAN   = 2;
    static final int WEIGHT_BLUE   = 3;
    static final int WEIGHT_RED    = 4;
    static final int WEIGHT_BLACK  = 5;

    public boolean carried = false;
    public int carrier = 0;

    public boolean delivered = false;

    public Position current;
    public Position destination;
    
    private int weight;

    public Package(Position current, Position destination, int weight) {
        this.current     = current;
        this.destination = destination;
        this.weight      = weight;
    }

    public Package(Position current, int weight) {
        this.current = current;
        this.weight = weight;

        destination = null;
    }

    public Package(Position current, Position destination) {
        this(current, destination, generateWeight());
    }

    public int getWeight() {
        return weight;
    }

    public static int generateWeight() {
        int weight = new Random().nextInt(CarrierAgent.MAX_WEIGHT + 1);
        // int weight = new Random().nextInt(CarrierAgent.MAX_WEIGHT + 5000);
        return weight;
    }
}
