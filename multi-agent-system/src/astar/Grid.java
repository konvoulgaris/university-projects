package astar;

import java.util.ArrayList;

public class Grid {
    public Node[][] nodes;
    
    public PathFinder pathFinder = new PathFinder(this);

    private int n;
    
    public Grid(int n) {
        nodes = new Node[n][n];

        this.n = n;

        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                nodes[i][j] = new Node(i, j, 0);
            }
        }
    }

    public Grid(int n, int[][] tiles) {
        nodes = new Node[n][n];

        this.n = n;

        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                nodes[i][j] = new Node(i, j, tiles[i][j]);
            }
        }
    }

    public ArrayList<Node> getNeighbours(Node node) {
        ArrayList<Node> result = new ArrayList<Node>();

        // Top neighbour
        if(node.x - 1 >= 0 && node.x - 1 < n) {
            result.add(nodes[node.x - 1][node.y]);
        }
        
        // Right neighbour
        if(node.y + 1 >= 0 && node.y + 1 < n) {
            result.add(nodes[node.x][node.y + 1]);
        } 

        // Bottom neighbour
        if(node.x + 1 >= 0 && node.x + 1 < n) {
            result.add(nodes[node.x + 1][node.y]);
        }
        
        // Left neighbour
        if(node.y - 1 >= 0 && node.y - 1 < n) {
            result.add(nodes[node.x][node.y - 1]);
        } 

        return result;
    }

    @Override
    public String toString() {
        ArrayList<String> elements = new ArrayList<String>();

        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                elements.add(String.valueOf(nodes[i][j].type));
            }
        }

        int maxLength = Integer.MIN_VALUE;

        for(int n = 0; n < elements.size(); n++) {
            int length = elements.get(n).length();

            if(maxLength < length)
                maxLength = length;
        }

        String result = "Grid => {\n";

        for(int i = 0; i < n * n; i += n) {
            for(int j = 0; j < n; j++) {
                String element = elements.get(i + j);

                int spaces = maxLength - element.length();
                
                for(int space = 0; space < spaces; space++) {
                    result += " ";
                }

                result += elements.get(i + j) + " ";
            }

            result += "\n";
        }

        return result + "}\n";
    }
}
