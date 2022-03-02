import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Iterator;

import geometry.*;

public class QuadTree {
  private static final int TOP_RIGHT = 0;
  private static final int BOTTOM_RIGHT = 1;
  private static final int BOTTOM_LEFT = 2;
  private static final int TOP_LEFT = 3;

  private BoundingBox bb;

  private Geometry[] elements;
  private int nElements = 0;
  private int totalElements = 0;
  private int maxElements;

  // Nodes declared (mentally) clock-wise
  private QuadTree[] nodes = new QuadTree[4];

  private int depth = 0;

  public QuadTree(BoundingBox bb, int maxElements) {
    this.bb = bb;
    this.elements = new Geometry[maxElements];
    this.maxElements = maxElements;
  }

  private QuadTree(BoundingBox bb, int maxElements, int depth) {
    this(bb, maxElements);
    this.depth = depth;
  }

  public void insert(Geometry g) {
    // Not in BoundingBox. Skip!
    if(!bb.bounds(g)) {
      return;
    }

    if(nElements < maxElements) {
        elements[nElements] = g;
        nElements++;
    } else {
      if(nodes[0] == null) {
        split();
      }

      for(QuadTree node : nodes) {
        node.insert(g);
      }
    }

    totalElements++;
  }

  public void insertMany(ArrayList<Geometry> gs) {
    for(Geometry g : gs) {
      insert(g);
    }
  }

  public HashSet<Geometry> rangeQuery(Point centre, double radius) {
    HashSet<Geometry> found = new HashSet<Geometry>();
    
    if(nElements == 0 || !bb.inRadiusOf(centre, radius)) {
      return found;
    }
    
    for(int i = 0; i < nElements; i++) {
      if(elements[i].calculateDistance(centre) <= radius) {
        found.add(elements[i]);
      }
    }

    if(nodes[0] != null) {
      for(QuadTree node : nodes) {
        found.addAll(node.rangeQuery(centre, radius));
      }
    }

    return found;
  }

  public HashSet<Geometry> kNearestQuery(Point centre, int k) {
    HashSet<Geometry> nearest = new HashSet<Geometry>();
    
    double xAverage = (bb.max.x + bb.min.x) / totalElements;
    double yAverage = (bb.max.y + bb.min.y) / totalElements;
    
    double range = ((xAverage + yAverage) / 2) * k;
    
    HashSet<Geometry> inRange = new HashSet<>();
    
    do {
        inRange = rangeQuery(centre, range);
        range += range;
    } while(inRange.size() < k);

    PriorityQueue<Geometry> queue = new PriorityQueue<Geometry>(k, new Comparator<Geometry>() {
      @Override
      public int compare(Geometry a, Geometry b) {
        if(a == null) {
          return 1;
        }

        if(b == null) {
          return -1;
        }

        return (int) (a.calculateDistance(centre) - b.calculateDistance(centre));
      }
    });

    queue.addAll(inRange);

    Iterator<Geometry> iterator = queue.iterator();

    while(iterator.hasNext() && nearest.size() < k) {
      nearest.add(iterator.next());
    }

    return nearest;
  }

  // Splits the QuadTree into four new nodes
  private void split() {
    double xMin = bb.min.x;
    double yMin = bb.min.y;
    double xCentre = bb.getCentre().x;
    double yCentre = bb.getCentre().y;
    double xMax = bb.max.x;
    double yMax = bb.max.y;

    nodes[TOP_RIGHT] = new QuadTree(new BoundingBox(xCentre, yCentre, xMax, yMax), maxElements, depth + 1);
    nodes[BOTTOM_RIGHT] = new QuadTree(new BoundingBox(xCentre, yMin, xMax, yCentre), maxElements, depth + 1);
    nodes[BOTTOM_LEFT] = new QuadTree(new BoundingBox(xMin, yMin, xCentre, yCentre), maxElements, depth + 1);
    nodes[TOP_LEFT] = new QuadTree(new BoundingBox(xMin, yCentre, xCentre, yMax), maxElements, depth + 1);
  }

  @Override
  // Overkill toString override
  public String toString() {
    if(nElements == 0) {
      return "(NEW) QuadTree";
    } else {
      String result = String.format("QuadTree (%d) %d/%d\nElements:\n", depth, nElements, maxElements);

      for(int i = 0; i < nElements; i++) {
        result += String.format("  + %s\n", elements[i]);
      }

      if(nodes[0] != null) {
        result += "+ Children nodes:";
        
        for(int i = 0; i < 4; i++) {
          result += String.format("\n  + Child node %d:\n", i + 1);
          
          String[] childLines = nodes[i].toString().split("\n");

          for(String line : childLines) {
              result += "      " + line + "\n";
          }

          result += "  ----";
        }
      }
      
      return result;
    }
  }
}
