package twentyfive;

import common.Assignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Five implements Assignment<Long> {

  List<Long> ids = new ArrayList<>();
  List<Range> ranges = new ArrayList<>();

  static void main(String[] args) {
    Five puzzle = new Five();
    puzzle.readInput();
    puzzle.answer();
  }

  @Override
  public void readInput() {
    List<String> lines = readInputLines();
    boolean addIds = false;
    for (String line : lines) {
      if (line.isBlank()) {
        addIds = true;
      } else if (addIds) {
        ids.add(Long.parseLong(line));
      } else {
        String[] split = line.split("-");
        ranges.add(new Range(Long.parseLong(split[0]), Long.parseLong(split[1])));
      }
    }
    Collections.sort(ranges);
  }

  @Override
  public Long partOne() {
    long total = 0;
    for (Long id : ids) {
      for (Range range : ranges) {
        if (range.isInRange(id)) {
          total++;
          break;
        }
      }
    }
    return total;
  }

  @Override
  public Long partTwo() {
    FreshRanges freshRanges = new FreshRanges();
    for (Range range : ranges) {
      freshRanges.addRange(range);
    }
    return freshRanges.size();
  }

  private static class FreshRanges {
    private Set<Range> freshRange = new HashSet<>();

    public void addRange(Range range) {
      boolean merged = false;
      Set<Range> mergedRanges = new HashSet<>();
      for (Range fresh : freshRange) {
        if (fresh.overlaps(range)) {
          mergedRanges.add(fresh.merge(range));
          merged = true;
        } else {
          mergedRanges.add(fresh);
        }
      }
      if (!merged) {
        mergedRanges.add(range);
      }
      freshRange = mergedRanges;
    }

    public Long size() {
      long total = 0;
      for (Range range : freshRange) {
        total += (range.max - range.min) + 1;
      }
      return total;
    }
  }

  private record Range(long min, long max) implements Comparable<Range> {
    public boolean isInRange(long value) {
      return value >= min && value <= max;
    }

    public boolean overlaps(Range range) {
      return contained(range) || leftOverlap(range) || rightOverlap(range);
    }

    private boolean contained(Range range) {
      return range.min >= min && range.max <= max;
    }

    private boolean leftOverlap(Range range) {
      return range.min <= min && range.max >= min;
    }

    private boolean rightOverlap(Range range) {
      return range.min <= max && range.max >= max;
    }

    @Override
    public int compareTo(Range other) {
      return Long.compare(min, other.min);
    }

    public Range merge(Range range) {
      return new Range(Math.min(min, range.min), Math.max(max, range.max));
    }
  }
}
