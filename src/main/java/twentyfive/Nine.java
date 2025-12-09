package twentyfive;

import common.Assignment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Nine implements Assignment<Long> {

  private Grid grid;

  static void main() {
    Nine puzzle = new Nine();
    puzzle.readInput();
    puzzle.answer();
  }

  @Override
  public void readInput() {
    List<String> lines = readInputLines();
    List<Point> points = lines.stream().map(line -> line.split(",")).map(s -> new Point(Long.parseLong(s[0]), Long.parseLong(s[1]))).collect(Collectors.toList());
    grid = new Grid(points);
    grid.calculateSquares();
  }

  @Override
  public Long partOne() {
    return grid.getMaxSquare();
  }

  @Override
  public Long partTwo() {
    grid.calculateBoundaries();
    return grid.getMaxSquareWithinBoundary();
  }

  record Point(long x, long y) {
  }

  record Square(long square, Point p1, Point p2) {
  }

  interface Range {
    public boolean cross(Range other);
  }

  class HorizontalRange implements Range {

    private final long y;
    private final long minX;
    private final long maxX;

    public HorizontalRange(long y, long x1, long x2) {
      this.y = y;
      this.minX = Math.min(x1, x2);
      this.maxX = Math.max(x1, x2);
    }

    @Override
    public boolean cross(Range other) {
      if (!(other instanceof VerticalRange verticalRange)) {
        return false;
      }
      return minX <= verticalRange.x && verticalRange.x <= maxX && verticalRange.minY < y && y < verticalRange.maxY;
    }

    @Override
    public String toString() {
      return "H: " + y + "[" + minX + ", " + maxX + "]";
    }
  }

  class VerticalRange implements Range {
    private final long x;
    private final long minY;
    private final long maxY;

    VerticalRange(long x, long y1, long y2) {
      this.x = x;
      this.minY = Math.min(y1, y2);
      this.maxY = Math.max(y1, y2);
    }

    @Override
    public boolean cross(Range other) {
      if (!(other instanceof HorizontalRange horizontalRange)) {
        return false;
      }
      return minY <= horizontalRange.y && horizontalRange.y <= maxY && horizontalRange.minX < x && x < horizontalRange.maxX;
    }

    @Override
    public String toString() {
      return "V: " + x + "[" + minY + ", " + maxY + "]";
    }
  }

  class Boundary {

    private Set<Range> ranges;

    public Boundary(Set<Range> ranges) {
      this.ranges = ranges;
    }

    public boolean withinBoundary(Square s) {
      Range h1 = new HorizontalRange(s.p1.y, s.p1.x, s.p2.x);
      Range h2 = new HorizontalRange(s.p2.y, s.p1.x, s.p2.x);
      Range v1 = new VerticalRange(s.p1.x, s.p1.y, s.p2.y);
      Range v2 = new VerticalRange(s.p2.x, s.p1.y, s.p2.y);
      int crossed = 0;
      for (Range r : ranges) {
        if (r.cross(h1)) {
          crossed++;
        }
        if (r.cross(h2)) {
          crossed++;
        }
        if (r.cross(v1)) {
          crossed++;
        }
        if (r.cross(v2)) {
          crossed++;
        }
        if (crossed > 1) {
          return false;
        }
      }
      return true;
    }
  }

  class Grid {
    private final List<Point> points;
    private List<Square> squares = new ArrayList<>();
    private Boundary boundary;

    public Grid(List<Point> points) {
      this.points = points;
    }

    public void calculateSquares() {
      for (int i = 0; i < points.size() - 1; i++) {
        for (int j = i + 1; j < points.size(); j++) {
          Point p1 = points.get(i);
          Point p2 = points.get(j);
          long square = calculateSquare(p1, p2);
          squares.add(new Square(square, p1, p2));
        }
      }
    }

    public void calculateBoundaries() {
      Point start = points.stream().sorted(Comparator.comparing(Point::y).thenComparing(Point::x)).limit(1).findFirst().orElseThrow();
      Set<Range> ranges = new HashSet<>();
      Point current = start;
      boolean isHorizontal = false;
      Point next;
      do {
        Point finalCurrent = current;
        if (isHorizontal) {
          next = points.stream().filter(p -> p.y() == finalCurrent.y() && p.x() != finalCurrent.x()).findFirst().orElseThrow();
          ranges.add(new HorizontalRange(current.y, current.x, next.x));
          isHorizontal = false;
        } else {
          next = points.stream().filter(p -> p.x() == finalCurrent.x() && p.y() != finalCurrent.y()).findFirst().orElseThrow();
          ranges.add(new VerticalRange(current.x, current.y, next.y));
          isHorizontal = true;
        }
        points.remove(next);
        current = next;
      } while (next != start);

      boundary = new Boundary(ranges);
    }

    private long calculateSquare(Point p1, Point p2) {
      return (Math.abs(p1.x() - p2.x()) + 1) * (Math.abs(p1.y() - p2.y()) + 1);
    }

    public long getMaxSquare() {
      return squares.stream().min((s1, s2) -> Long.compare(s2.square(), s1.square())).orElseThrow().square();
    }

    public long getMaxSquareWithinBoundary() {
      List<Square> sorted = squares.stream().sorted((s1, s2) -> Long.compare(s2.square(), s1.square())).toList();
      Square maxSquare = null;
      for (Square s : sorted) {
        if (boundary.withinBoundary(s)) {
          maxSquare = s;
          break;
        }
      }
      return maxSquare.square();
    }
  }
}
