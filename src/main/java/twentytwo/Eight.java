package twentytwo;

import common.Assignment;

import java.util.Arrays;

public class Eight implements Assignment<Integer> {

  private Forest forest;

  public static void main(String[] args) {
    Eight eight = new Eight();
    eight.readInput();
    eight.answer();
  }

  @Override
  public void readInput() {
    Integer[][] trees = readInputLines().stream()
        .map(line -> Arrays.stream(line.split("")).map(Integer::parseInt).toArray(Integer[]::new))
        .toArray(Integer[][]::new);
    forest = new Forest(trees);
  }

  @Override
  public Integer partOne() {
    return forest.countVisibleTrees();
  }

  @Override
  public Integer partTwo() {
    return forest.computeScenicScore();
  }

  private static class Forest {
    Integer[][] trees;

    public Forest(Integer[][] trees) {
      this.trees = trees;
    }

    public int countVisibleTrees() {
      int count = 0;
      for (int row = 0; row < trees.length; row++) {
        for (int col = 0; col < trees[0].length; col++) {
          if (isTreeVisible(row, col, trees[row][col])) {
            count++;
          }
        }
      }
      return count;
    }

    public int computeScenicScore() {
      int highscore = 0;
      for (int row = 0; row < trees.length; row++) {
        for (int col = 0; col < trees[0].length; col++) {
          int scenicScore = computeScore(row, col, trees[row][col]);
          if (scenicScore > highscore) {
            highscore = scenicScore;
          }
        }
      }
      return highscore;
    }

    private int computeScore(int row, int col, int height) {
      int scoreLeft = computeScoreLeft(row, col, height);
      if (scoreLeft == 0) {
        return 0;
      }
      int scoreRight = computeScoreRight(row, col, height);
      if (scoreRight == 0) {
        return 0;
      }
      int scoreTop = computeScoreTop(row, col, height);
      if (scoreTop == 0) {
        return 0;
      }
      int scoreBottom = computeScoreBottom(row, col, height);
      if (scoreBottom == 0) {
        return 0;
      }
      return scoreLeft * scoreRight * scoreTop * scoreBottom;
    }

    private int computeScoreLeft(int row, int col, int height) {
      if (col == 0) {
        return 0;
      }
      int distance = 0;
      for (int i = col - 1; i >= 0; i--) {
        if (trees[row][i] < height) {
          distance++;
        } else {
          return ++distance;
        }
      }
      return distance;
    }

    private int computeScoreRight(int row, int col, int height) {
      if (col == trees[0].length - 1) {
        return 0;
      }
      int distance = 0;
      for (int i = col + 1; i < trees[0].length; i++) {
        if (trees[row][i] < height) {
          distance++;
        } else {
          return ++distance;
        }
      }
      return distance;
    }

    private int computeScoreTop(int row, int col, int height) {
      if (row == 0) {
        return 0;
      }
      int distance = 0;
      for (int i = row - 1; i >= 0; i--) {
        if (trees[i][col] < height) {
          distance++;
        } else {
          return ++distance;
        }
      }
      return distance;
    }

    private int computeScoreBottom(int row, int col, int height) {
      if (row == trees.length - 1) {
        return 0;
      }
      int distance = 0;
      for (int i = row + 1; i < trees.length; i++) {
        if (trees[i][col] < height) {
          distance++;
        } else {
          return ++distance;
        }
      }
      return distance;
    }

    public boolean isTreeVisible(int row, int col, int height) {
      return isVisibleLeft(row, col, height) || isVisibleRight(row, col, height) || isVisibleTop(row, col, height)
          || isVisibleBottom(row, col, height);
    }

    private boolean isVisibleLeft(int row, int col, int height) {
      for (int i = 0; i < col; i++) {
        if (trees[row][i] >= height) {
          return false;
        }
      }
      return true;
    }

    private boolean isVisibleRight(int row, int col, int height) {
      for (int i = trees[row].length - 1; i > col; i--) {
        if (trees[row][i] >= height) {
          return false;
        }
      }
      return true;
    }

    private boolean isVisibleTop(int row, int col, int height) {
      for (int i = 0; i < row; i++) {
        if (trees[i][col] >= height) {
          return false;
        }
      }
      return true;
    }

    private boolean isVisibleBottom(int row, int col, int height) {
      for (int i = trees[col].length - 1; i > row; i--) {
        if (trees[i][col] >= height) {
          return false;
        }
      }
      return true;
    }

  }
}
