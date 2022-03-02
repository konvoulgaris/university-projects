package utils;

import java.util.Random;
import java.util.ArrayList;

import geometry.*;

public final class UniformGenerator {
  private UniformGenerator() { }

  // Generates n uniformly distributed and generated Geometries within the given BoundingBox
  public static ArrayList<Geometry> generateGeometries(BoundingBox bb, int n) {
    return generateGeometries(bb, n, 0.5);
  }
  
  // Generates n uniformly distributed but not uniformly generated Geometries within the given BoundingBox
  public static ArrayList<Geometry> generateGeometries(BoundingBox bb, int n, double ratio) {
    ArrayList<Geometry> generated = new ArrayList<Geometry>();
    
    generated.addAll(generatePoints(bb, (int) (n * ratio)));
    generated.addAll(generateRectangles(bb, (int) (n * (1 - ratio))));
    
    return generated;
  }

  // Generates n uniformly distributed Points within the given BoundingBox
  public static ArrayList<Geometry> generatePoints(BoundingBox bb, int n) {
    ArrayList<Geometry> generated = new ArrayList<Geometry>();

    for(int i = 0; i < n; i++) {
      Random random = new Random();

      double x = random.nextDouble();
      x = limit(x, bb.min.x, bb.max.x);

      double y = random.nextDouble();
      y = limit(y, bb.min.y, bb.max.y);

      generated.add(new Point(x, y));
    }

    return generated;
  }

  // Generates n uniformly distributed Rectangles within the given BoundingBox
  public static ArrayList<Geometry> generateRectangles(BoundingBox bb, int n) {
    ArrayList<Geometry> generated = new ArrayList<Geometry>();
    
    // Min generated length
    double xMinLength = 0.5;
    double yMinLength = 0.5;

    // Max generated length
    double xMaxLength = bb.getCentre().x;
    double yMaxLength = bb.getCentre().y;
    
    for(int i = 0; i < n; i++) {
      Random random = new Random();
    
      double xMin = random.nextDouble();
      double yMin = random.nextDouble();

      xMin = limit(xMin, bb.min.x, bb.max.x);
      yMin = limit(yMin, bb.min.y, bb.max.y);

      double xMax = random.nextDouble();
      double yMax = random.nextDouble();

      xMax = limit(xMax, xMinLength, xMaxLength);
      yMax = limit(yMax, yMinLength, yMaxLength);
      
      Point min = new Point(xMin, yMin);
      Point max = new Point(xMax, yMax);

      generated.add(new Rectangle(min, max));
    }

    return generated;
  }

  private static double limit(double x, double lower, double upper) {
    return lower + (x * (upper - lower));
  }
}
