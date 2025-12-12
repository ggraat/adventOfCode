package twentyfive;

import common.Assignment;

import java.util.Arrays;
import java.util.List;

public class Twelve implements Assignment<Long> {

  static void main() {
    Twelve twelve = new Twelve();
    twelve.readInput();
    twelve.answer();
  }
  
  @Override
  public void readInput() {
    List<String> lines = readInputLines();
    int count = 0;
    for (String line : lines) {
      long width = Long.parseLong(line.substring(0, 2));
      long height = Long.parseLong(line.substring(3, 5));
      long numPieces = Arrays.stream(line.substring(7).split(" ")).mapToLong(Long::parseLong).sum();
      if (numPieces * 7 < width * height) {
        count++;
        System.out.println(line);
      }
    }
    System.out.println(count);
  }

  @Override
  public Long partOne() {
    return 0L;
  }

  @Override
  public Long partTwo() {
    return 0L;
  }
}
