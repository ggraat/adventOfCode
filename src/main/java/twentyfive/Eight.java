package twentyfive;

import common.Assignment;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class Eight implements Assignment<Long> {

  private final Integer rounds;
  private List<Point> points;

  public Eight(Integer rounds) {
    this.rounds = rounds;
  }

  static void main() {
    Eight puzzle = new Eight(1000);
    puzzle.readInput();
    puzzle.answer();
  }

  @Override
  public void readInput() {
    List<String> lines = readInputLines();
    points = lines.stream().map(line -> line.split(","))
            .map(s -> new Point(Long.parseLong(s[0]), Long.parseLong(s[1]), Long.parseLong(s[2]))).toList();
  }

  @Override
  public Long partOne() {
    Circuits circuits = new Circuits(points);
    circuits.connect(rounds);
    return circuits.size();
  }

  @Override
  public Long partTwo() {
    Circuits circuits = new Circuits(points);
    return circuits.connectUntilLast();
  }

  record Point(long x, long y, long z) {

  }

  record Distance(Point p1, Point p2, double distance) implements Comparable<Distance> {

    @Override
    public int compareTo(Distance o) {
      return Double.compare(distance, o.distance);
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof Distance distance1)) return false;
      return distance == distance1.distance && Objects.equals(p1, distance1.p1) && Objects.equals(p2, distance1.p2);
    }

    @Override
    public int hashCode() {
      return Objects.hash(p1, p2, distance);
    }
  }

  class Circuit {

    private Set<Point> points = new HashSet<>();
    private double distance;

    public int overlap(Distance distance) {
      int overlap = 0;
      if (points.contains(distance.p1())) {
        overlap++;
      }
      if (points.contains(distance.p2())) {
        overlap++;
      }
      return overlap;
    }

    public void addDistance(Distance distance) {
      points.add(distance.p1());
      points.add(distance.p2());
      this.distance += distance.distance();
    }

    public long size() {
      return points.size();
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof Circuit circuit)) return false;
      return Double.compare(distance, circuit.distance) == 0 && Objects.equals(points, circuit.points);
    }

    @Override
    public int hashCode() {
      return Objects.hash(points, distance);
    }

    public void merge(Circuit circuit) {
      points.addAll(circuit.points);
      this.distance += circuit.distance;
    }
  }

  class Circuits {

    private final int pointsSize;
    private SortedSet<Distance> distances = new TreeSet<>();
    private Set<Circuit> circuits = new HashSet<>();

    public Circuits(List<Point> points) {
      calcuteDistances(points);
      this.pointsSize = points.size();
    }

    public void connect(int times) {
      for (int i = 0; i < times; i++) {
        connect();
      }
    }

    public long connectUntilLast() {
      Distance lastDistance = null;
      while (!(circuits.size() == 1 && circuits.stream().mapToLong(Circuit::size).sum() == pointsSize)) {
        lastDistance = connect();
      }
      return lastDistance.p1.x() * lastDistance.p2.x();
    }

    private Distance connect() {
      Distance distance = distances.removeFirst();
      Set<Circuit> newCircuits = new HashSet<>();
      Circuit overlapping = null;
      for (Circuit circuit : circuits) {
        int overlap = circuit.overlap(distance);
        if (overlap > 0) {
          if (overlapping == null) {
            overlapping = circuit;
            overlapping.addDistance(distance);
          } else {
            overlapping.merge(circuit);
          }
        } else {
          newCircuits.add(circuit);
        }
      }
      if (overlapping == null) {
        Circuit c = new Circuit();
        c.addDistance(distance);
        newCircuits.add(c);
      } else {
        newCircuits.add(overlapping);
      }
      circuits = newCircuits;
      return distance;
    }

    private void calcuteDistances(List<Point> points) {
      for (int i = 0; i < points.size() - 1; i++) {
        for (int j = i + 1; j < points.size(); j++) {
          Point p1 = points.get(i);
          Point p2 = points.get(j);
          double distance = calculateDistance(p1, p2);
          distances.add(new Distance(p1, p2, distance));
        }
      }
    }

    private double calculateDistance(Point p1, Point p2) {
      return Math.sqrt(Math.pow(Math.abs(p2.x() - p1.x()), 2) + Math.pow(Math.abs(p2.y() - p1.y()), 2) + Math.pow(Math.abs(p2.z() - p1.z()), 2));
    }

    public long size() {
      return circuits.stream().sorted((c1, c2) -> Long.compare(c2.size(), c1.size())).limit(3).mapToLong(Circuit::size).reduce(1L, (c1, c2) -> c1 * c2);
    }
  }
}
