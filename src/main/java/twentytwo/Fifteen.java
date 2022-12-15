package twentytwo;

import common.Assignment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Fifteen implements Assignment<String> {

  private Field field;
  private final int yPosition;
  private final int max;

  public Fifteen() {
    yPosition = 2_000_000;
    max = 4_000_000;
  }

  Fifteen(int yPosition, int max) {
    this.yPosition = yPosition;
    this.max = max;
  }

  public static void main(String[] args) {
    Fifteen fifteen = new Fifteen();
    fifteen.readInput();
    fifteen.answer();
  }

  @Override
  public void readInput() {
    List<String> lines = readInputLines();
    field = new Field();
    for (String line : lines) {
      int sensorX = Integer.parseInt(line.substring(line.indexOf("x=") + 2, line.indexOf(",")));
      int sensorY = Integer.parseInt(line.substring(line.indexOf("y=") + 2, line.indexOf(":")));
      int beaconX = Integer.parseInt(line.substring(line.lastIndexOf("x=") + 2, line.lastIndexOf(",")));
      int beaconY = Integer.parseInt(line.substring(line.lastIndexOf("y=") + 2));
      Beacon beacon = new Beacon(beaconX, beaconY);
      Sensor sensor = new Sensor(sensorX, sensorY, beacon);
      field.beacons.add(beacon);
      field.sensors.add(sensor);
    }
  }

  @Override
  public String partOne() {
    Set<Integer> intersect = new HashSet<>();
    for (Sensor sensor : field.sensors) {
      intersect.addAll(sensor.intersectionWithLine(yPosition));
    }
    removeBeacons(intersect, yPosition);
    return String.valueOf(intersect.size());
  }

  private void removeBeacons(Set<Integer> intersect, int yValue) {
    field.beacons.stream().filter(b -> b.y == yValue).mapToInt(b -> b.x).forEach(intersect::remove);
  }

  @Override
  public String partTwo() {
    long total = 0;
    for (int y = 0; y <= max; y++) {
      Ranges ranges = new Ranges(new Range(0, max));
      for (Sensor sensor : field.sensors) {
        Range intersection = sensor.intersectWithLine(y, max);
        if (intersection != null) {
          ranges.subtract(intersection);
          if (ranges.isEmpty()) {
            break;
          }
        }
      }
      if (ranges.containsUniqueElement()) {
        total = (ranges.getResult() * 4_000_000L) + y;
        break;
      }
      System.out.println(y);
    }
    return String.valueOf(total);
  }

  private void fillSet(Set<Integer> intersect) {
    IntStream.range(0, max + 1).forEach(intersect::add);
  }

  private static class Field {
    List<Beacon> beacons = new ArrayList<>();
    List<Sensor> sensors = new ArrayList<>();
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
      return x + "," + y;
    }
  }

  private static class Beacon extends Point {

    public Beacon(int x, int y) {
      super(x, y);
    }
  }

  private static class Sensor extends Point {
    Beacon nearest;
    int distance;

    public Sensor(int x, int y, Beacon nearest) {
      super(x, y);
      this.nearest = nearest;
      distance = Math.abs(nearest.x - x) + Math.abs(nearest.y - y);
    }

    public Set<Integer> intersectionWithLine(int yPosition) {
      Set<Integer> intersect = new HashSet<>();
      int deltaY = Math.abs(yPosition - y);
      if (deltaY <= distance) {
        IntStream.range(x - (distance - deltaY), x + (distance - deltaY) + 1).forEach(intersect::add);
      }
      return intersect;
    }

    public Range intersectWithLine(int yPosition, int max) {
      int deltaY = Math.abs(yPosition - y);
      if (deltaY <= distance) {
        return new Range(Math.max(0, x - (distance - deltaY)), Math.min(x + (distance - deltaY), max));
      }
      return null;
    }
  }

  private static class Ranges {
    List<Range> ranges = new ArrayList<>();

    public Ranges(Range range) {
      ranges.add(range);
    }

    public void subtract(Range r) {
      ranges = ranges.stream().map(range -> range.subtract(r)).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public boolean isEmpty() {
      return ranges.isEmpty();
    }

    public boolean containsUniqueElement() {
      return ranges.size() == 1 && ranges.get(0).start == ranges.get(0).end;
    }

    public int getResult() {
      return ranges.get(0).start;
    }
  }
  private static class Range {
    int start;
    int end;

    public Range(int start, int end) {
      this.start = start;
      this.end = end;
    }

    public List<Range> subtract(Range r) {
      if (r.end < start || r.start > end) {
        return List.of(this);
      }
      if (r.start <= start && r.end >= end) {
        return Collections.emptyList();
      }
      if (r.start <= start) {
        return List.of(new Range(r.end + 1, end));
      }
      if (r.end < end) {
        return List.of(new Range(start, r.start - 1), new Range(r.end + 1, end));
      }
      return List.of(new Range(start, r.start - 1));
    }
  }
}
