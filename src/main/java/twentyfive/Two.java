package twentyfive;

import common.Assignment;

import java.util.ArrayList;
import java.util.List;

public class Two implements Assignment<Long> {

  private static List<Range> ranges = new ArrayList<>();

  public static void main(String[] args) {
    Two two = new Two();
    two.readInput();
    two.answer();
  }

  @Override
  public void readInput() {
    String line = readInputLines().get(0);
    String[] stringRanges = line.split(",");
    for (String stringRange : stringRanges) {
      String[] split = stringRange.split("-");
      Range range = new Range(Long.parseLong(split[0]), Long.parseLong(split[1]));
      ranges.add(range);
    }
  }

  @Override
  public Long partOne() {
    return ranges.stream().mapToLong(Range::findInvalidIds).sum();
  }

  @Override
  public Long partTwo() {
    return ranges.stream().mapToLong(Range::findInvalidIdsPart2).sum();
  }

  private static class Range {
    private final long min;
    private final long max;

    public Range(long min, long max) {
      this.min = min;
      this.max = max;
    }

    public long findInvalidIds() {
      List<Long> invalidIds = new ArrayList<>();
      for (long id = min; id <= max; id++) {
        String idString = String.valueOf(id);
        if (idString.length() % 2 == 0) {
          if (idString.substring(0, idString.length() / 2).equals(idString.substring(idString.length() / 2))) {
            invalidIds.add(id);
          }
        }
      }
      return invalidIds.stream().mapToLong(i -> i).sum();
    }

    public long findInvalidIdsPart2() {
      List<Long> invalidIds = new ArrayList<>();
      for (long id = min; id <= max; id++) {
        String idString = String.valueOf(id);
        int pairSize = 1;
        boolean found = false;
        while (!found && pairSize <= idString.length() / 2) {
          found = checkPair(idString, pairSize);
          pairSize++;
        }
        if (found) {
          invalidIds.add(id);
        }
      }
      return invalidIds.stream().mapToLong(i -> i).sum();
    }

    private boolean checkPair(String idString, int pairSize) {
      if (idString.length() % pairSize != 0) {
        return false;
      }
      int numOfPairs = idString.length() / pairSize;
      for (int index = 0; index < numOfPairs - 1; index++) {
        if (!idString.substring(index * pairSize, (index + 1) * pairSize).equals(idString.substring((index + 1) * pairSize, (index + 2) * pairSize))) {
          return false;
        }
      }
      return true;
    }
  }
}
