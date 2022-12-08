package twentytwo;

import common.Assignment;

import java.util.HashSet;
import java.util.Set;

public class Six implements Assignment<Integer> {

  private String line;

  public static void main(String[] args) {
    Six six = new Six();
    six.readInput();
    six.answer();
  }

  private static int getMarker(String line, int length) {
    char[] chars = line.toCharArray();
    for (int i = 0; i < chars.length - 3; i++) {
      Set<Character> marker = new HashSet<>();
      for (int j = 0; j < length; j++) {
        marker.add(chars[i + j]);
      }
      if (marker.size() == length) {
        return i + length;
      }
    }
    return -1;
  }

  @Override
  public void readInput() {
    line = readInputLines().get(0);
  }

  @Override
  public Integer partOne() {
    return getMarker(line, 4);
  }

  @Override
  public Integer partTwo() {
    return getMarker(line, 14);
  }
}
