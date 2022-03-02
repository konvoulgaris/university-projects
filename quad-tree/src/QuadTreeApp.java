import java.util.Set;

import geometry.*;
import utils.*;

public class QuadTreeApp {
  public static void main(String[] args) {
    BoundingBox bb = new BoundingBox(0, 0, 1000, 1000);
    
    QuadTree qt = new QuadTree(bb, 5);
    
    qt.insertMany(UniformGenerator.generatePoints(bb, 500000));
    qt.insertMany(UniformGenerator.generateRectangles(bb, 500000));

    System.out.println("Generated geometries");

    Timer timer = new Timer();
    timer.start();
    Set<Geometry> range = qt.rangeQuery(bb.getCentre(), 100);
    timer.stop();

    System.out.printf("Range query with %d results took %.3f ms\n", range.size(), timer.getMilliseconds());

    timer.start();
    Set<Geometry> nearest = qt.kNearestQuery(bb.getCentre(), 100);
    timer.stop();

    System.out.printf("KNearest query with %d results took %.3f ms\n", nearest.size(), timer.getMilliseconds());
  }
}
