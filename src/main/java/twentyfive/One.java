package twentyfive;

import common.Assignment;

import java.util.List;

public class One implements Assignment<Integer> {

  private Dial dial;

  public static void main(String[] args) {
    One one = new One();
    one.readInput();
    one.answer();
  }

  @Override
  public void readInput() {
    dial = new Dial(50);
    List<String> lines = readInputLines();
    for (String line : lines) {
      int steps = Integer.parseInt(line.substring(1));
      if (line.charAt(0) == 'L') {
        dial.left(steps);
      } else {
        dial.right(steps);
      }
    }
  }

  @Override
  public Integer partOne() {
    return dial.password;
  }

  @Override
  public Integer partTwo() {
    return dial.password + dial.rotate;
  }

  private static class Dial {
    private int current;
    private int password = 0;
    private int rotate = 0;

    public Dial(int current) {
      this.current = current;
    }

    public void left(int steps) {
      rotate += steps / 100;
      if ((steps % 100) > current) {
        if (current != 0) {
          rotate++;
        }
        current+= (100 - (steps % 100)) % 100;
      } else {
        current -= steps % 100;
      }
      if (current == 0) {
        password++;
      }
    }

    public void right(int steps) {
      rotate += steps / 100;
      if ((steps % 100) + current > 100) {
        rotate++;
      }
      current = (current + steps) % 100;
      if (current == 0) {
        password++;
      }
    }
  }
}
