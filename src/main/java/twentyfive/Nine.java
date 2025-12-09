package twentyfive;

import common.Assignment;

import java.util.ArrayList;
import java.util.List;

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
    List<Point> points = lines.stream().map(line -> line.split(",")).map(s -> new Point(Long.parseLong(s[0]), Long.parseLong(s[1]))).toList();
    grid = new Grid(points);
  }

  @Override
  public Long partOne() {
    grid.calculateDistances();
    return grid.getMaxSquare();
  }

  @Override
  public Long partTwo() {
    return 0L;
  }

  record Point(long x, long y) {
  }

  record Square(long square, Point p1, Point p2) {
  }

  class Grid {
    private final List<Point> points;
    private List<Square> squares = new ArrayList<>();

    public Grid(List<Point> points) {
      this.points = points;
    }

    public void calculateDistances() {
      for (int i = 0; i < points.size() - 1; i++) {
        for (int j = i + 1; j < points.size(); j++) {
          Point p1 = points.get(i);
          Point p2 = points.get(j);
          long square = calculateSquare(p1, p2);
          squares.add(new Square(square, p1, p2));
        }
      }
    }

    private long calculateSquare(Point p1, Point p2) {
      return (Math.abs(p1.x() - p2.x()) + 1) * (Math.abs(p1.y() - p2.y()) + 1);
    }

    public long getMaxSquare() {
      return squares.stream().sorted((s1, s2) -> Long.compare(s2.square(), s1.square())).findFirst().get().square();
    }
  }
}
