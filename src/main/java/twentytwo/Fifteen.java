package twentytwo;

import common.Assignment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
      Set<Integer> intersect = new HashSet<>();
      fillSet(intersect);
      for (Sensor sensor : field.sensors) {
        intersect.removeAll(sensor.intersectionWithLine(y));
      }
      if (intersect.size() == 1) {
        total = ((long) intersect.iterator().next() * 4_000_000) + y;
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

  }
}
