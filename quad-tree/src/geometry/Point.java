package geometry;

public class Point implements Geometry {
  public double x;
  public double y;

  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public Point(Point other) {
    this.x = other.x;
    this.y = other.y;
  }

  @Override
  // Calculates the distance between the Point and another given Point p
  public double calculateDistance(Point p) {
    return Math.sqrt(Math.pow(x - p.x, 2) + Math.pow(y - p.y, 2));
  }

  @Override
  public boolean equals(Object other) {
    if(other instanceof Point) {
      Point target = (Point) other;

      boolean xMatch = this.x == target.x;
      boolean yMatch = this.y == target.y;

      return xMatch && yMatch;
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return String.format("Point => (%.3f, %.3f)", x, y);
  }
}
