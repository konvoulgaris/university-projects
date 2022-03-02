import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import astar.*;

public class CarrierAgent extends Agent {
    private static final long serialVersionUID = 1L;

    public static int MAX_WEIGHT = 0;

    private static final int N = GridAgent.N;

    public static final int MOVE_TURNS = 20;
    public static final int REST_TURNS = 10;
    
    private static final int TOP    = 0;
    private static final int RIGHT  = 1;
    private static final int BOTTOM = 2;
    private static final int LEFT   = 3;

    private static final double MOTIVE_THRESHOLD = 0.15;

    private int id;

    private Grid visibility;
    private Grid memory = new Grid(N);

    private Position current;
    private Position charge;

    private int moves = MOVE_TURNS;
    private int rests = 0;
    private boolean wantsCharge = false;
    private boolean isResting = false;
    
    private int direction = TOP;
    
    private boolean isCarrying = false;
    
    private int maxWeight = 0;
    private ArrayList<Position> ignored = new ArrayList<Position>();
    private ArrayList<Package> ignoredPackages = new ArrayList<Package>();
    private Position destination;

    private boolean nextIsTurn90 = false;

    private boolean hasContract = false;
    private Position contractPack = null;

    private Package auctioned = null;
    private ArrayList<Offer> offers = new ArrayList<Offer>();

    public void setup() {
        // Register agent
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription service = new ServiceDescription();
        service.setType("agent");
        service.setName(getLocalName());
        template.setName(getAID());
        template.addServices(service);
        
        try {
            DFService.register(this, template);
        } catch(Exception e) {
            e.printStackTrace();
        }

        maxWeight = new Random().nextInt(Package.WEIGHT_BLACK + 1);
        MAX_WEIGHT = Math.max(maxWeight, MAX_WEIGHT);

        System.out.println(getLocalName() + " max weight: " + maxWeight);

        id = Integer.parseInt(getLocalName().split("-")[1]);

        addBehaviour(new CyclicBehaviour(this) {
            private static final long serialVersionUID = 1L;

            public void action() {
                ACLMessage receive = blockingReceive();
                
                String sender = receive.getSender().getLocalName();
                String message = receive.getContent();

                if(message.contains("IllegalMoveForward:")) {
                    nextIsTurn90 = true;
                }

                if(message.contains("Inform:")) {
                    parseInform(message.split(":")[1]);
                }

                if(message.contains("Position:")) {
                    parsePosition(message.split(":")[1]);
                }

                if(message.contains("VisibleGrid:")) {
                    parseVisibleGrid(message.split(":")[1]);
                }

                if(message.contains("Package:")) {
                    parsePackage(message.split(":")[1]);
                }

                if(message.contains("PackageNO:")) {
                    startAuction(message.split(":")[1]);
                }

                if(message.contains("PickAction:")) {
                    sendMessage(pickAction());
                }

                if(message.contains("Memory:")) {
                    parseMemory(message.split(":")[1]);
                }

                if(message.contains("Offer:")) {
                    offers.add(Offer.getOffer(sender, message));
                }

                if(message.contains("Auction:")) {
                    joinAuction(sender, message.split(":")[1]);
                }

                if(message.contains("Won:")) {
                    getAuction(message.split(":")[1]);
                }

                // Kill carrier agent
                if(message.equals("Die")) {
                    takeDown();
                    doDelete();

                    System.out.println(getLocalName() + " died");
                }
            }
        });
    }

    private void parseInform(String message) {
        int x = Integer.parseInt(message.split(",")[0]);
        int y = Integer.parseInt(message.split(",")[1]);

        charge = new Position(x, y);
    }

    private void parsePosition(String message) {
        int x = Integer.parseInt(message.split(",")[0]);
        int y = Integer.parseInt(message.split(",")[1]);

        current = new Position(x, y);
    }

    private void parseVisibleGrid(String message) {
        int[][] neighbours = new int[N][N];

        for(int i = 0; i < N; i++) {
            String[] line = message.split(",#")[i].split(",");

            for(int j = 0; j < N; j++) {
                neighbours[i][j] = Integer.parseInt(line[j]);

                if(neighbours[i][j] > GridAgent.NORMAL) {
                    shareMemory(neighbours[i][j]);
                }
            }
        }

        neighbours[charge.x][charge.y] = GridAgent.CHARGE;

        visibility = new Grid(N, neighbours);

        fillMemory();
    }

    private void parsePackage(String message) {
        int x = Integer.parseInt(message.split(",")[0]);
        int y = Integer.parseInt(message.split(",")[1]);
        
        destination = new Position(x, y);
        isCarrying = true;
    }

    private String pickAction() {
        if(offers.size() > 0) {
            assignContract();
            offers.clear();
            auctioned = null;
        }

        // System.out.println(getLocalName() + " pickAction()");
        // printGrid();

        if(moves < 0) {
            return "Dead";
        }


        if(isResting) {
            rests++;

            if(rests >= REST_TURNS) {
                moves = MOVE_TURNS;
                rests = 0;
                wantsCharge = false;
                isResting = false;
                return pickAction();
            } else {
                return ("Rest");
            }
        } else {
            if(wantsCharge) {
                String action = goToCharge();
                return action;
            }

            int distanceFromCharge = memory.pathFinder.findPath(current, charge).size();

            double motiveMove = 1.0;
            double motiveCharge = 0.0;

            if(moves != MOVE_TURNS) {
                motiveCharge = (double)(distanceFromCharge) / (double)(moves);
            }

            if(motiveMove - motiveCharge > MOTIVE_THRESHOLD) {
                if(isCarrying) {
                    if(current.equals(destination)) {
                        isCarrying = false;
                        return "PutDownPackage";
                    } else {
                        String action = goTo(destination);
                        return action;
                    }
                } else {
                    Position nearestPackage = getNearestPackage();
                    
                    if(hasContract) {
                        if(nearestPackage != null) {
                            if(memory.pathFinder.findPath(current, nearestPackage).size() >= memory.pathFinder.findPath(current, contractPack).size()) {
                                nearestPackage = contractPack;
                            } else {
                                contractPack = null;
                                hasContract = false;
                            }
                        }
                    }

                    if(current.equals(nearestPackage)) {
                        return ("PickUpPackage:" + maxWeight);
                    }

                    if(nearestPackage != null) {
                        String action = goTo(nearestPackage);
                        return action;
                    } else {
                        String action = goToRandom();
                        return action;
                    }
                }
            } else {
                String action = goToCharge();
                return action;
            }
        }
    }

    private String moveForward() {
        moves--;
        
        Position next = null;

        switch(direction) {
            case TOP:
                next = new Position(current.x - 1, current.y);
                break;

            case RIGHT:
            next = new Position(current.x, current.y + 1);
                break;

            case BOTTOM:
                next = new Position(current.x + 1, current.y);
                break;

            case LEFT:
                next = new Position(current.x, current.y - 1);
                break;
        }

        return ("MoveForward:" + next.x + "," + next.y);
    }

    private String turn90() {
        if(nextIsTurn90) {
            nextIsTurn90 = false;
        }

        if(direction == LEFT) {
            direction = TOP;
        } else {
            direction++;
        }

        return ("Turn90");
    }

    private String goTo(Position destination) {
        Position next = memory.pathFinder.findNextInPath(current, destination);

        if(isForward(next) && !nextIsTurn90) {
            return moveForward();
        } else {
            return turn90();
        }
    }

    private String goToRandom() {
        if(new Random().nextInt(3) != 0 && !isForwardObstacle() && !nextIsTurn90) {
            return moveForward();
        } else {
            return turn90();
        }
    }

    private String goToCharge() {
        wantsCharge = true;

        int distanceFromCharge = PathFinder.manhattanDistance(current, charge);

        if(distanceFromCharge == 0) {
            if(moves != MOVE_TURNS) {
                rests++;
                isResting = true;
                wantsCharge = false;
                return ("Rest");
            } else {
                return pickAction();
            }
        } else {
            return goTo(charge);
        }
    }

    private void shareMemory(int target) {
        String content = "Memory:";
        
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                content += memory.nodes[i][j].type + ",";
            }

            content += "#";
        }

        sendMessage(content, Integer.toString(target));
    }

    // ----------------------------------------------------------------------
    // --------------------------------------------------------
    // ------------------------------------------
    // ----------------------------
    // --------------
    // HELPER METHODS
    // --------------

    @SuppressWarnings("unused")
    private void printGrid() {
        int[][] tiles = new int[N][N];

        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                tiles[i][j] = memory.nodes[i][j].type;
            }
        }

        tiles[current.x][current.y] = Integer.parseInt(getLocalName().split("-")[1]);

        System.out.println(getLocalName() + "(" + moves + ") sees:\n" + new Grid(N, tiles));
    }

    private boolean isForward(Position target) {
        switch(direction) {
            case TOP:
                return ((current.x - 1 >= 0 && current.x - 1 < N) && (current.x - 1 == target.x && current.y == target.y));

            case RIGHT:
                return ((current.y + 1 >= 0 && current.y + 1 < N) && (current.x == target.x && current.y + 1 == target.y));
            
            case BOTTOM:
                return ((current.x + 1 >= 0 && current.x + 1 < N) && (current.x + 1 == target.x && current.y == target.y));
                
            case LEFT:
                return ((current.y - 1 >= 0 && current.y - 1 < N) && (current.x == target.x && current.y - 1 == target.y));

            default:
                return false;
        }
    }

    private boolean isForwardObstacle() {
        switch(direction) {
            case TOP:
                return !((current.x - 1 >= 0 && current.x - 1 < N) && (memory.nodes[current.x - 1][current.y].type != GridAgent.OBSTACLE));

            case RIGHT:
                return !((current.y + 1 >= 0 && current.y + 1 < N) && (memory.nodes[current.x][current.y + 1].type != GridAgent.OBSTACLE));
            
            case BOTTOM:
                return !((current.x + 1 >= 0 && current.x + 1 < N) && (memory.nodes[current.x + 1][current.y].type != GridAgent.OBSTACLE));
                
            case LEFT:
                return !((current.y - 1 >= 0 && current.y - 1 < N) && (memory.nodes[current.x][current.y - 1].type != GridAgent.OBSTACLE));

            default:
                return false;
        }
    }

    private Position getNearestPackage() {
        Position nearest = null;

        int nearestDistance = Integer.MAX_VALUE;

        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                Position candidate = new Position(i, j);
                
                if(!ignored.contains(candidate) && memory.nodes[i][j].type == GridAgent.PACKAGES) {
                    int distance = memory.pathFinder.findPath(current, new Position(i, j)).size();
    
                    if(nearestDistance > distance) {
                            nearest = candidate;
                            nearestDistance = distance;
                    }
                }
            }
        }

        return nearest;
    }

    // =========================
    // HELPER

    private void sendMessage(String content) {
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(new AID("Grid", AID.ISLOCALNAME));
        message.setContent(content);
        send(message);
    }

    private void sendMessage(String content, String agentId) {
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(new AID("Agent-" + agentId, AID.ISLOCALNAME));
        message.setContent(content);
        send(message);
    }

    private void fillMemory() {
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                if(visibility.nodes[i][j].type != Integer.MIN_VALUE) {
                    memory.nodes[i][j].type = visibility.nodes[i][j].type;
                }
            }
        }
    }

    private void parseMemory(String message) {
        for(int i = 0; i < N; i++) {
            String[] line = message.split(",#")[i].split(",");

            for(int j = 0; j < N; j++) {
                int x = Integer.parseInt(line[j]);

                if(x != GridAgent.NORMAL && x != id) {
                    memory.nodes[i][j].type = Integer.parseInt(line[j]);
                }
            }
        }
    }

    private void startAuction(String message) {
        int x = Integer.parseInt(message.split(",")[0]);
        int y = Integer.parseInt(message.split(",")[1]);
        int w = Integer.parseInt(message.split(",")[2]);

        auctioned = new Package(new Position(x, y), w);

        ignored.add(new Position(x, y));
        ignoredPackages.add(auctioned);

        ArrayList<String> agents = getAgents();

        if(agents.size() > 0) {
            for(String a : agents) {
                sendMessage("Auction:" + message, a);
            }
        }
    }

    private ArrayList<String> getAgents() {
        ArrayList<String> agents = new ArrayList<String>();

        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                if(memory.nodes[i][j].type > 0 && memory.nodes[i][j].type != id) {
                    agents.add(Integer.toString(memory.nodes[i][j].type));
                }
            }
        }

        return agents;
    }

    private void joinAuction(String sender, String details) {
        int x = Integer.parseInt(details.split(",")[0]);
        int y = Integer.parseInt(details.split(",")[1]);
        int w = Integer.parseInt(details.split(",")[2]);

        if(maxWeight >= w && !isCarrying) {
            sendMessage("Offer:" + memory.pathFinder.findPath(current, new Position(x, y)).size() + "," + memory.pathFinder.findPath(current, charge).size() + ",", sender.split("-")[1]);
        } else {
            sendMessage("OfferX:", sender);
        }
    }

    private void getAuction(String details) {
        int x = Integer.parseInt(details.split(",")[0]);
        int y = Integer.parseInt(details.split(",")[1]);

        hasContract = true;
        contractPack = new Position(x, y);
    }

    private void assignContract() {
        Offer best = Offer.findBest(offers);

        System.out.println("Agent-" + best.agent + " won an auction");
        sendMessage(String.format("Won:%d,%d", auctioned.current.x, auctioned.current.y), best.agent);
    }
}
