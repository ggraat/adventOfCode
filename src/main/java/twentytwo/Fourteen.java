package twentytwo;

import common.Assignment;

import java.util.Arrays;
import java.util.List;

public class Fourteen implements Assignment<String> {

  private Canvas canvas;

  public static void main(String[] args) {
    Fourteen fourteen = new Fourteen();
    fourteen.readInput();
    fourteen.answer();
  }

  @Override
  public void readInput() {
    canvas = new Canvas(1000, 1000);
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
  }

  @Override
  public String partOne() {
    canvas.determineDimensions();
    canvas.dropSand();
    canvas.printCanvas();
    return String.valueOf(canvas.getRestingSand());
  }

  @Override
  public String partTwo() {
    readInput();
    canvas.addFloor();
    canvas.dropSandOnTheFloor();
    canvas.printCanvas();
    return String.valueOf(canvas.getRestingSand());
  }

  private static class Canvas {
    Point[][] points;
    private int minX;
    private int maxX;
    private int maxY;

    public Canvas(int maxX, int maxY) {
      this.maxX = maxX;
      this.maxY = maxY;
      initCanvas();
    }

    public void dropSand() {
      Sand sand = new Sand(500, 0);
      boolean intoTheAbyss = false;
      while (!intoTheAbyss) {
        if (sand.y > maxY) {
          intoTheAbyss = true;
        }
        if (points[sand.x][sand.y + 1] instanceof Sand || points[sand.x][sand.y + 1] instanceof Rock) {
          if (points[sand.x - 1][sand.y + 1] instanceof Sand || points[sand.x - 1][sand.y + 1] instanceof Rock) {
            if (points[sand.x + 1][sand.y + 1] instanceof Sand || points[sand.x + 1][sand.y + 1] instanceof Rock) {
              points[sand.x][sand.y] = sand;
              sand = new Sand(500, 0);
            } else {
              sand.x++;
              sand.y++;
            }
          } else {
            sand.x--;
            sand.y++;
          }
        } else {
          sand.y++;
        }
      }
    }

    public void dropSandOnTheFloor() {
      Sand sand = new Sand(500, 0);
      while (true) {
        if (points[sand.x][sand.y + 1] instanceof Sand || points[sand.x][sand.y + 1] instanceof Rock) {
          if (points[sand.x - 1][sand.y + 1] instanceof Sand || points[sand.x - 1][sand.y + 1] instanceof Rock) {
            if (points[sand.x + 1][sand.y + 1] instanceof Sand || points[sand.x + 1][sand.y + 1] instanceof Rock) {
              points[sand.x][sand.y] = sand;
              if (points[500][0] instanceof Sand) {
                return;
              }
              sand = new Sand(500, 0);
            } else {
              sand.x++;
              sand.y++;
            }
          } else {
            sand.x--;
            sand.y++;
          }
        } else {
          sand.y++;
        }
      }
    }

    public long getRestingSand() {
      return Arrays.stream(points).mapToLong(row -> Arrays.stream(row).filter(p -> p instanceof Sand).count()).sum();
    }

    private void initCanvas() {
      points = new Point[maxX][maxY];
      for (int x = 0; x < maxX; x++) {
        for (int y = 0; y < maxY; y++) {
          points[x][y] = new Point(x, y);
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

    public void determineDimensions() {
      minX =
          Arrays.stream(points).mapToInt(row -> Arrays.stream(row).filter(p -> p instanceof Rock || p instanceof Sand)
              .findFirst().map(p -> p.x).orElse(points.length)).min().orElse(0);
      maxX =
          Arrays.stream(points).mapToInt(row -> Arrays.stream(row).filter(p -> p instanceof Rock || p instanceof Sand)
              .mapToInt(p -> p.x).max().orElse(0)).max().orElse(points.length);
      maxY =
          Arrays.stream(points).mapToInt(row -> Arrays.stream(row).filter(p -> p instanceof Rock || p instanceof Sand)
              .mapToInt(p -> p.y).max().orElse(0)).max().orElse(points[0].length);
    }

    public void printCanvas() {
      for (int y = 0; y < Integer.min(maxY + 2, points[0].length); y++) {
        for (int x = Integer.max(minX - 2, 0); x < Integer.min(maxX + 2, points.length); x++) {
          System.out.print(points[x][y]);
        }
        System.out.println();
      }
      System.out.println();
    }

    public void addFloor() {
      maxY =
        Arrays.stream(points).mapToInt(row -> Arrays.stream(row).filter(p -> p instanceof Rock || p instanceof Sand)
          .mapToInt(p -> p.y).max().orElse(0)).max().orElse(points[0].length) + 2;
      for (int x = 0; x < points.length; x++) {
        points[x][maxY] = new Rock(x, maxY);
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
