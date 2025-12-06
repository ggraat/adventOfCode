package twentyfive;

import common.Assignment;

public class Four implements Assignment<Long> {

  private Map map;

  static void main(String[] args) {
    Four puzzle = new Four();
    puzzle.readInput();
    puzzle.answer();
  }

  @Override
  public void readInput() {
    char[][] grid = readInputLines().stream().map(String::toCharArray).toArray(char[][]::new);
    map = new Map(grid);
  }

  @Override
  public Long partOne() {
    return map.findAccessible();
  }

  @Override
  public Long partTwo() {
    char[][] grid = readInputLines().stream().map(String::toCharArray).toArray(char[][]::new);
    map = new Map(grid);
    long total = 0L;
    long accessible = 1L;
    while (accessible > 0) {
      accessible = map.findAccessible();
      total += accessible;
      map.removeAccessible();
    }
    return total;
  }

  private static class Map {
    private final char[][] grid;
    private final int width;
    private final int height;
    private static final int[][] directions = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};

    public Map(char[][] grid) {
      this.grid = grid;
      width = grid[0].length;
      height = grid.length;
    }

    public long findAccessible() {
      long accessible = 0;
      for (int row = 0; row < height; row++) {
        for (int col = 0; col < width; col++) {
          if (grid[row][col] == '@') {
            if (getAdjacent(row, col) < 4) {
              accessible++;
              grid[row][col] = 'x';
            }
          }
        }
      }
      return accessible;
    }

    private int getAdjacent(int row, int col) {
      int adjacent = 0;
      for (int[] direction : directions) {
        int nCol = col + direction[0];
        int nRow = row + direction[1];
        if (nRow >= 0 && nRow < height && nCol >= 0 && nCol < width) {
          if (grid[nRow][nCol] == '@' || grid[nRow][nCol] == 'x') {
            adjacent++;
          }
        }
      }
      return adjacent;
    }

    public void removeAccessible() {
      for (int row = 0; row < height; row++) {
        for (int col = 0; col < width; col++) {
          if (grid[row][col] == 'x') {
            grid[row][col] = '.';
          }
        }
      }
    }
  }
}
