package twentytwo;

import common.Assignment;

import java.util.ArrayList;
import java.util.List;

public class Four implements Assignment<Long> {

  private List<Pair> pairs;

  public static void main(String[] args) {
    Four four = new Four();
    four.readInput();
    four.answer();
  }

  @Override
  public void readInput() {
    List<String> lines = readInputLines();
    pairs = new ArrayList<>();
    for (String line : lines) {
      String[] split = line.split(",");
      String[] leftSplit = split[0].split("-");
      Range left = new Range(Integer.parseInt(leftSplit[0]), Integer.parseInt(leftSplit[1]));
      String[] rightSplit = split[1].split("-");
      Range right = new Range(Integer.parseInt(rightSplit[0]), Integer.parseInt(rightSplit[1]));
      Pair pair = new Pair(left, right);
      pairs.add(pair);
    }
  }

  @Override
  public Long partOne() {
    return pairs.stream().filter(Pair::completelyOverlap).count();
  }

  @Override
  public Long partTwo() {
    return pairs.stream().filter(Pair::overlaps).count();
  }

  private static class Pair {
    Range left;
    Range right;

    public Pair(Range left, Range right) {
      this.left = left;
      this.right = right;
    }

    public boolean completelyOverlap() {
      if (left.min < right.min) {
        return left.max >= right.max;
      } else if (left.min > right.min) {
        return right.max >= left.max;
      }
      return true;
    }

    public boolean overlaps() {
      if (left.max < right.min) {
        return false;
      }
      if (right.max < left.min) {
        return false;
      }
      return true;
    }
  }

  private static class Range {
    int min;
    int max;

    public Range(int min, int max) {
      this.min = min;
      this.max = max;
    }
  }
}
