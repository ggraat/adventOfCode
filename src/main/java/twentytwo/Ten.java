package twentytwo;

import common.Assignment;

import java.util.ArrayList;
import java.util.List;

public class Ten implements Assignment<String> {

  public static void main(String[] args) {
    Ten ten = new Ten();
    ten.readInput();
    ten.answer();
  }

  private final List<Integer> timeline = new ArrayList<>();
  private int x = 1;

  @Override
  public void readInput() {
    timeline.add(x);
    for (String line : readInputLines()) {
      String[] split = line.split(" ");
      if (split.length == 2) {
        timeline.add(x);
        timeline.add(x);
        x += Integer.parseInt(split[1]);
      } else {
        timeline.add(x);
      }
    }
  }

  @Override
  public String partOne() {
    int total = 0;
    for (int cycle = 0; cycle < 6; cycle++) {
      int index = 20 + (40 * cycle);
      total += index * timeline.get(index);
    }
    return String.valueOf(total);
  }

  @Override
  public String partTwo() {
    String result = "";
    for (int cycle = 1; cycle < timeline.size(); cycle++) {
      int sprite = timeline.get(cycle);
      int pixel = (cycle - 1) % 40;
      if (sprite - 1 <= pixel && pixel <= sprite + 1) {
        result += "#";
      } else {
        result += ".";
      }
      if (cycle % 40 == 0 && cycle != timeline.size() - 1) {
        result += "\n";
      }
    }
    return result;
  }
}
