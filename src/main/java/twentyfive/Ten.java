package twentyfive;

import common.Assignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ten implements Assignment<Long> {

  private List<Machine> machines = new ArrayList<>();

  static void main() {
    Ten puzzle = new Ten();
    puzzle.readInput();
    puzzle.answer();
  }

  @Override
  public void readInput() {
    List<String> lines = readInputLines();
    for (String line : lines) {
      String indicatorString = line.substring(1, line.indexOf("]"));
      List<ButtonGroup> buttonGroups = new ArrayList<>();
      Pattern p = Pattern.compile("\\((.+?)\\)");
      Matcher m = p.matcher(line);
      while (m.find()) {
        List<Integer> buttons = Arrays.stream(m.group(1).split(",")).map(Integer::parseInt).toList();
        buttonGroups.add(new ButtonGroup(buttons));
      }
      Machine machine = new Machine(indicatorString, buttonGroups);
      machines.add(machine);
    }
  }

  @Override
  public Long partOne() {
    return machines.stream().mapToLong(Machine::getShortestPath).sum();
  }

  @Override
  public Long partTwo() {
    return 0L;
  }

  class Indicator {
    boolean[] current;
    boolean[] endState;
    long presses;

    public Indicator(boolean[] endState) {
      this.endState = endState;
      current = new boolean[endState.length];
    }

    public void toggleButtons(ButtonGroup buttonGroup) {
      buttonGroup.buttons().forEach(button -> current[button] = !current[button]);
      presses++;
    }

    public boolean reachEndState() {
      return Arrays.equals(current, endState);
    }

    public Indicator copy() {
      Indicator indicator = new Indicator(endState);
      indicator.current = Arrays.copyOf(current, current.length);
      indicator.presses = presses;
      return indicator;
    }

    @Override
    public String toString() {
      String s = "[";
      for (boolean c : current) {
        s += c ? "#" : ".";
      }
      s += "]";
      return s;
    }
  }

  record ButtonGroup(List<Integer> buttons) {

    @Override
    public String toString() {
      return "(" + Arrays.toString(buttons.toArray()) + ")";
    }
  }

  class Machine {
    private final List<ButtonGroup> buttonGroups;
    Indicator indicator;

    public Machine(String indicatorString, List<ButtonGroup> buttonGroups) {
      boolean[] endState = new boolean[indicatorString.length()];
      for (int i = 0; i < indicatorString.length(); i++) {
        if (indicatorString.charAt(i) == '#') {
          endState[i] = true;
        }
      }
      this.indicator = new Indicator(endState);
      this.buttonGroups = buttonGroups;
    }

    public long getShortestPath() {
      return step(buttonGroups, indicator, Integer.MAX_VALUE);
    }

    public long step(List<ButtonGroup> remainingButtonGroups, Indicator indicator, long min) {
      if (remainingButtonGroups.isEmpty() || indicator.presses >= min) {
        return Integer.MAX_VALUE;
      }
      for (ButtonGroup next : remainingButtonGroups) {
        Indicator indicatorCopy = indicator.copy();
        indicatorCopy.toggleButtons(next);
        if (indicatorCopy.reachEndState()) {
          return indicatorCopy.presses;
        } else {
          List<ButtonGroup> remaining = new ArrayList<>(remainingButtonGroups);
          remaining.remove(next);
          long steps = step(remaining, indicatorCopy, min);
          if (steps < min) {
            min = steps;
          }

        }
      }
      return min;
    }
  }

}
