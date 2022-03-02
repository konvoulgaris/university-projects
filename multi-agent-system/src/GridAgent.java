import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import tools.Logger;
import astar.*;

public class GridAgent extends Agent {
    private static final long serialVersionUID = 1L;
    
    public static final int N          = 20;
    public static final int NOBSTACLES = N;
    public static final int NPACKAGES  = N;

    private static final int VISIBILITY = 3;

    private static final int MAX_TURNS = 100;

    public static final int NORMAL   =  0;
    public static final int OBSTACLE = -1;
    public static final int PACKAGES = -2;
    public static final int CHARGE   = -3;

    public static final int WEIGHT_WHITE  = 0;
    public static final int WEIGHT_YELLOW = 1;
    public static final int WEIGHT_CYAN   = 2;
    public static final int WEIGHT_BLUE   = 3;
    public static final int WEIGHT_RED    = 4;
    public static final int WEIGHT_BLACK  = 5;

    private Grid grid;
    
    private Position charge;
    private Package[] packages = new Package[NPACKAGES];
    private int nAgents;
    private String[] agents;
    private Position[] agentPositions;

    private int turns = 0;
    private boolean informed = false;
    
    private int nDelivered = 0;

    private ArrayList<String> auctionsToHandle = new ArrayList<String>();

    private Logger logger = new Logger("out.txt", true);

    public void setup() {
        initialize();
        
        addBehaviour(new CyclicBehaviour(this) {
            private static final long serialVersionUID = 1L;

            public void action() {
                loop();
            }
        });
    }

    private void generateGrid() {
        grid = new Grid(N);

        Random random = new Random();

        // Place obstacles
        for(int n = 0; n < NOBSTACLES; n++) {
            int x;
            int y;
            
            do {
                x = random.nextInt(N);
                y = random.nextInt(N);
            } while(grid.nodes[x][y].type != NORMAL);
            
            grid.nodes[x][y].type = OBSTACLE;
        }
        
        // Place charge station
        charge = new Position(0, 0);

        do {
            charge.x = random.nextInt(N);
            charge.y = random.nextInt(N);
        } while(grid.nodes[charge.x][charge.y].type != NORMAL);

        grid.nodes[charge.x][charge.y].type = CHARGE;

        // Place packages
        for(int n = 0; n < NPACKAGES; n++) {
            Position origin = new Position(0, 0);

            do {
                origin.x = random.nextInt(N);
                origin.y = random.nextInt(N);
            } while(grid.nodes[origin.x][origin.y].type != NORMAL || PathFinder.manhattanDistance(origin, charge) >= CarrierAgent.MOVE_TURNS);

            Position destination = new Position(0, 0);

            do {
                destination.x = random.nextInt(N);
                destination.y = random.nextInt(N);
            } while(grid.nodes[destination.x][destination.y].type != NORMAL || origin.equals(destination) || PathFinder.manhattanDistance(destination, charge) >= CarrierAgent.MOVE_TURNS);

            packages[n] = new Package(origin, destination);
        }

        // Place agents
        for(int i = 0; i < nAgents; i++) {
            Position position = new Position(0, 0);

            do {
                position.x = random.nextInt(N);
                position.y = random.nextInt(N);
            } while(grid.nodes[position.x][position.y].type != NORMAL || PathFinder.manhattanDistance(position, charge) >= CarrierAgent.MOVE_TURNS);

            agentPositions[i] = position;
        }
    }

    private ArrayList<Node> generateNeighbours(Position agentPosition) {
        Node target = grid.nodes[agentPosition.x][agentPosition.y];
        
        ArrayList<Node> neighbours = new ArrayList<Node>();

        if(VISIBILITY > 0) {
            neighbours.addAll(grid.getNeighbours(target));

            if(VISIBILITY > 1) {
                for(int n = 1; n < VISIBILITY; n++) {
                    ArrayList<Node> extension = new ArrayList<Node>();
                    ArrayList<Node> extensionNeighbours = new ArrayList<Node>();
                    
                    for(Node neighbour : neighbours) {
                        extensionNeighbours.addAll(grid.getNeighbours(neighbour));
                    }

                    for(Node neighbour : extensionNeighbours) {
                        if(extension.contains(neighbour) || neighbours.contains(neighbour) || PathFinder.manhattanDistance(target, neighbour) > VISIBILITY) {
                            continue;
                        } else {
                            extension.add(neighbour);
                        }
                    }

                    neighbours.addAll(extension);
                }
            }
        }

        return neighbours;
    }

    private int[][] generateVisibleGrid(String agent) {
        int[][] visibleGrid = new int[N][N];

        Position current = agentPositions[getAgentIndex(agent)];

        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                if(PathFinder.manhattanDistance(current, new Position(i, j)) <= VISIBILITY) {
                    visibleGrid[i][j] = NORMAL;
                } else {
                    visibleGrid[i][j] = Integer.MIN_VALUE;
                }
            }
        }

        for(Node neighbour : generateNeighbours(current)) {
            visibleGrid[neighbour.x][neighbour.y] = neighbour.type;
        }

        for(String a : agents) {
            if(a.equals(agent)) {
                continue;
            } else {
                Position target = agentPositions[getAgentIndex(a)];

                if(PathFinder.manhattanDistance(current, target) <= VISIBILITY && !target.equals(charge)) {
                    int agentId = getAgentID(a);
                    visibleGrid[target.x][target.y] = agentId;
                }
            }
        }

        for(Package p : packages) {
            if(PathFinder.manhattanDistance(current, p.current) <= VISIBILITY && (!p.carried || !p.delivered)) {
                visibleGrid[p.current.x][p.current.y] = PACKAGES;
            }
        }

        return visibleGrid;
    }

    private void processPickAction(String agent, String message) {
        if(message.contains("MoveForward:")) {
            moveForwardAction(agent, message);
        }

        if(message.contains("PickUpPackage:")) {
            pickUpPackageAction(agent, message);
        }

        if(message.contains("PutDownPackage")) {
            putDownPackageAction(agent, message);
        }
    }

    private void moveForwardAction(String agent, String message) {
        String sMove = message.split(":")[1];

        int x = Integer.parseInt(sMove.split(",")[0]);
        int y = Integer.parseInt(sMove.split(",")[1]);

        if(grid.nodes[x][y].isWalkable()) {
            agentPositions[getAgentIndex(agent)] = new Position(x, y);
        } else {
            sendMessage(agent, "IllegalMoveForward");
        }
    }

    private void pickUpPackageAction(String agent, String message) {
        Position agentPosition = findAgent(agent);
            
        int agentMaxWeight = Integer.parseInt(message.split(":")[1]);

        for(Package p : packages) {
            if(agentPosition.equals(p.current)) {
                if(p.getWeight() <= agentMaxWeight) {
                    sendMessage(agent, String.format("Package:%d,%d", p.destination.x, p.destination.y));
                    p.carried = true;
                    p.carrier = getAgentID(agent);
                } else {
                    sendMessage(agent, String.format("PackageNO:%d,%d,%d", p.current.x, p.current.y, p.getWeight()));
                }
            }
        }
    }

    private void putDownPackageAction(String agent, String message) {
        int agentID = getAgentID(agent);
            
        Position agentPosition = findAgent(agent);
        
        for(Package p : packages) {
            if(agentID == p.carrier) {
                if(agentPosition.equals(p.destination)) {
                    p.carried = false;
                    p.carrier = 0;
                    p.delivered = true;
                    nDelivered++;
                    logger.write(turns + "," + nDelivered);
                } else {
                    p.carried = false;
                    p.carrier = 0;
                    p.current = new Position(agentPosition);
                }
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(50);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void initialize() {
        sleep();

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription service = new ServiceDescription();
        service.setType("agent");
        template.addServices(service);
        
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            nAgents = result.length;
            agents = new String[nAgents];
            agentPositions = new Position[nAgents];

            for(int i = 0; i < nAgents; i++) {
                agents[i] = result[i].getName().getLocalName();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        Arrays.sort(agents);

        generateGrid();
        // printGrid();
    }

    private void inform() {
        if(!informed) {
            for(String agent : agents) {
                sendMessage(agent, String.format("Inform:%d,%d", charge.x, charge.y));
            }

            informed = true;
        }
    }

    private void finish() {
        sleep();
        
        System.out.println("Delivered " + nDelivered + " packages");

        for(String agent : agents) {
            sendMessage(agent, "Die");
        }
        
        logger.destroy();

        doDelete();
    }

    private void loop() {
        sleep();
        inform();

        if(turns < MAX_TURNS) {
            System.out.println("Turn: " + (turns + 1) + "\n-----");

            for(String agent : agents) {
                Position current = agentPositions[getAgentIndex(agent)];

                sendMessage(agent, String.format("Position:%d,%d", current.x, current.y));

                int[][] visibleGrid = generateVisibleGrid(agent);

                String message = "VisibleGrid:";

                for(int i = 0; i < N; i++) {
                    for(int j = 0; j < N; j++) {
                        message += visibleGrid[i][j] + ",";
                    }

                    message += "#";
                }

                sendMessage(agent, message);
                sendMessage(agent, "PickAction:");
            }

            for(int i = 0; i < nAgents; i++) {
                ACLMessage message = blockingReceive();
                
                String agent = message.getSender().getLocalName();
                String content = message.getContent();

                System.out.println(agent + " does: " + content);

                processPickAction(agent, content);
            }

            // printGrid();

            System.out.println("\n");

            auctionsToHandle.clear();

            turns++;
        } else {
            finish();
        }
    }

    private void printGrid() {
        int[][] tiles = new int[N][N];

        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                tiles[i][j] = grid.nodes[i][j].type;
            }
        }

        for(Package p : packages) {
            tiles[p.current.x][p.current.y] = PACKAGES;
        }

        for(String agent : agents) {
            Position p = agentPositions[getAgentIndex(agent)];
            tiles[p.x][p.y] = getAgentID(agent);
        }

        System.out.println(new Grid(N, tiles));
    }

    private int getAgentID(String agent) {
        return Integer.parseInt(agent.split("-")[1]);
    }

    private int getAgentIndex(String agent) {
        for(int i = 0; i < nAgents; i++) {
            if(agent.equals(agents[i])) {
                return i;
            }
        }

        return -1;
    }

    private Position findAgent(String agent) {
        return agentPositions[getAgentIndex(agent)];
    }

    private void sendMessage(String agent, String content) {
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(new AID(agent, AID.ISLOCALNAME));
        message.setContent(content);
        send(message);
    }
}
