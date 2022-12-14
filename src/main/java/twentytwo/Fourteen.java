package twentytwo;

import common.Assignment;

import java.util.List;

public class Fourteen implements Assignment<String> {

  public static void main(String[] args) {
    Fourteen fourteen = new Fourteen();
    fourteen.readInput();
    fourteen.answer();
  }

  @Override
  public void readInput() {
    Canvas canvas = new Canvas(550, 200);
    List<String> lines = readInputLines();
    for (String line : lines) {
      String[] splitPoints = line.split(" -> ");
      for (int i = 0; i < splitPoints.length - 1; i++) {
        String[] firstPoint = splitPoints[i].split(",");
        String[] secondPoint = splitPoints[i + 1].split(",");
        canvas.drawLine(Integer.parseInt(firstPoint[0]), Integer.parseInt(firstPoint[1]),
            Integer.parseInt(secondPoint[0]), Integer.parseInt(secondPoint[1]));
      }
    }
    canvas.printCanvas();
  }

  @Override
  public String partOne() {
    return null;
  }

  @Override
  public String partTwo() {
    return null;
  }

  private static class Canvas {
    Point[][] points;

    public Canvas(int maxX, int maxY) {
      initCanvas(maxX, maxY);
    }

    private void initCanvas(int maxX, int maxY) {
      points = new Point[maxX][maxY];
      for (int x = 0; x < maxX; x++) {
        for (int y = 0; y < maxY; y++) {
          if (x == 500 && y == 0) {
            points[x][y] = new Sand(x, y);
          } else {
            points[x][y] = new Point(x, y);
          }
        }
      }
    }

    public void drawLine(int startX, int startY, int endX, int endY) {
      if (startX == endX) {
        int yStart;
        int yEnd;
        if (endY > startY) {
          yStart = startY;
          yEnd = endY;
        } else {
          yStart = endY;
          yEnd = startY;
        }
        for (int y = yStart; y <= yEnd; y++) {
          points[startX][y] = new Rock(startX, y);
        }
      } else {
        int xStart;
        int xEnd;
        if (endX > startX) {
          xStart = startX;
          xEnd = endX;
        } else {
          xStart = endX;
          xEnd = startX;
        }
        for (int x = xStart; x <= xEnd; x++) {
          points[x][startY] = new Rock(x, startY);
        }
      }
    }

    public void printCanvas() {
      // for (int x = 0; x < points.length; x++) {
      // for (int y = 0; y < points[0].length; y++) {
      for (int y = 0; y < 20; y++) {
        for (int x = 490; x < 510; x++) {
          System.out.print(points[x][y]);
        }
        System.out.println();
      }
    }
  }

  private static class Point {
    int x;
    int y;

    public Point(int x, int y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public String toString() {
      return ".";
    }
  }

  private static class Rock extends Point {

    public Rock(int x, int y) {
      super(x, y);
    }

    @Override
    public String toString() {
      return "#";
    }
  }

  private static class Sand extends Point {

    public Sand(int x, int y) {
      super(x, y);
    }

    @Override
    public String toString() {
      return "o";
    }
  }
}
