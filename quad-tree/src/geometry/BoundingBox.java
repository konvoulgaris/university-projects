package geometry;

public class BoundingBox extends Rectangle {
  public BoundingBox(double xMin, double yMin, double xMax, double yMax) {
    super(xMin, yMin, xMax, yMax);
  }
  
  public BoundingBox(Point min, Point max) {
    super(min, max);
  }

  public BoundingBox(Rectangle other) {
    super(other);
  }

  public BoundingBox(BoundingBox other) {
    super(other);
  }

  // Checks if the given Geometry g is "bounded" (i.e. within) by the BoundingBox
  public boolean bounds(Geometry g) {
    if(g instanceof Point) {
      Point target = (Point) g;
      
      boolean xBounds = min.x <= target.x && max.x >= target.x;
      boolean yBounds = min.y <= target.y && max.y >= target.y;
      
      return xBounds && yBounds;
    }
    else {
      Rectangle target = (Rectangle) g;

      boolean minBounds = bounds(target.min);
      boolean xMaxBounds = bounds(new Point(target.max.x, target.min.y));
      boolean yMaxBounds = bounds(new Point(target.min.x, target.max.y));
      boolean maxBounds = bounds(target.max);

      return minBounds || xMaxBounds || yMaxBounds || maxBounds;
    }
  }

  // Checks if the BoundingBox is within the radius of the given circle
  public boolean inRadiusOf(Point centre, double radius) {
    return bounds(centre) || calculateDistance(centre) <= radius;
  }

  // Returns the centre Point of the BoundingBox.
  public Point getCentre() {
    double xCenter = min.x + ((max.x - min.x) / 2);
    double yCenter = min.y + ((max.y - min.y) / 2);

    return new Point(xCenter, yCenter);
  }

  @Override
  public String toString() {
    return String.format("BoundingBox => [Min => (%.3f, %.3f), Max => (%.3f, %.3f)]", min.x, min.y, max.x, max.y);
  }
}
