package twentyfive;

import common.Assignment;

public class Seven implements Assignment<Long> {

  private Map map;

  static void main(String[] args) {
    Seven puzzle = new Seven();
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
    map.beam();
    return map.getSplitted();
  }

  @Override
  public Long partTwo() {
    return 0L;
  }

  private static class Map {
    private final char[][] grid;
    private final int width;
    private final int height;
    private long splitted;
    private static final char START = 'S';
    private static final char SPLIT = '^';
    private static final char BEAM = '|';

    public Map(char[][] grid) {
      this.grid = grid;
      width = grid[0].length;
      height = grid.length;
    }

    public void beam() {
      for (int row = 1; row < height; row++) {
        for (int col = 0; col < width; col++) {
          if (beamFromAbove(row, col)) {
            if (grid[row][col] == SPLIT) {
              split(row, col);
            } else if (grid[row][col] != BEAM) {
              grid[row][col] = BEAM;
            }
          }
        }
      }
    }

    private boolean beamFromAbove(int row, int col) {
      return grid[row - 1][col] == START || grid[row - 1][col] == BEAM;
    }

    private void split(int row, int col) {
      grid[row][col - 1] = BEAM;
      grid[row][col + 1] = BEAM;
      splitted++;
    }

    public long getSplitted() {
      return splitted;
    }
  }
}
