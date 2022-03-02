package geometry;

public class Rectangle implements Geometry {
  public Point min;
  public Point max;

  public Rectangle(double xMin, double yMin, double xMax, double yMax) {
    this.min = new Point(xMin, yMin);
    this.max = new Point(xMax, yMax);
  }
  
  public Rectangle(Point min, Point max) {
    this.min = min;
    this.max = max;
  }

  public Rectangle(Rectangle other) {
    this.min = other.min;
    this.max = other.max;
  }

  @Override
  // Calculates the distance between the Rectangle and the given Point p
  public double calculateDistance(Point p) {
    double xClosest = Math.max(min.x - p.x, p.x - max.x);
    double yClosest = Math.max(min.y - p.y, p.y - max.y);

    return Math.sqrt(Math.pow(xClosest, 2) + Math.pow(yClosest, 2));
  }

  @Override
  public boolean equals(Object other) {
    if(other instanceof Rectangle) {
      Rectangle target = (Rectangle) other;

      boolean minMatch = this.min.equals(target.min);
      boolean maxMatch = this.max.equals(target.max);

      return minMatch && maxMatch;
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return String.format("Rectangle => [Min => (%.3f, %.3f), Max => (%.3f, %.3f)]", min.x, min.y, max.x, max.y);
  }
}

