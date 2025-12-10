package twentyfive;

import common.Assignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ten implements Assignment<Long> {

  private List<Machine> machines = new ArrayList<>();
  private List<JoltageMachine> joltages = new ArrayList<>();

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
      String joltageString = line.substring(line.indexOf('{') + 1, line.length() - 1);
      int[] joltage = Arrays.stream(joltageString.split(",")).mapToInt(Integer::parseInt).toArray();
      JoltageMachine joltageMachine = new JoltageMachine(buttonGroups, joltage);
      joltages.add(joltageMachine);
    }
  }

  @Override
  public Long partOne() {
    return machines.stream().mapToLong(Machine::getShortestPath).sum();
  }

  @Override
  public Long partTwo() {
    long sum = 0L;
    for (int i = 0; i < joltages.size(); i++) {
      JoltageMachine joltage = joltages.get(i);
      long shortestPath = joltage.getShortestPath();
      sum += shortestPath;
    }
    return sum;
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

  static final class ButtonGroup {
    private final List<Integer> buttons;
    private int maxUse = Integer.MAX_VALUE;
    private int used;
    private boolean disabled;

    ButtonGroup(List<Integer> buttons) {
      this.buttons = buttons;
    }

    @Override
    public String toString() {
      return "(" + Arrays.toString(buttons.toArray()) + ")";
    }

    public List<Integer> buttons() {
      return buttons;
    }

    public boolean contains(int joltage) {
      return buttons.contains(joltage);
    }

    public boolean contains(List<Integer> exhausted) {
      return exhausted.stream().anyMatch(this::contains);
    }

    public boolean isDisabled() {
      return used == maxUse;
    }

    public ButtonGroup copy() {
      ButtonGroup buttonGroup = new ButtonGroup(buttons);
      buttonGroup.maxUse = maxUse;
      buttonGroup.used = used;
      return buttonGroup;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof ButtonGroup that)) return false;
      return maxUse == that.maxUse && used == that.used && disabled == that.disabled && Objects.equals(buttons, that.buttons);
    }

    @Override
    public int hashCode() {
      return Objects.hash(buttons, maxUse, used, disabled);
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

  class Joltage {
    int[] current;
    int[] endState;
    long presses;

    public Joltage(int[] endState) {
      this.endState = endState;
      current = new int[endState.length];
    }

    public void applyButtons(ButtonGroup buttonGroup) {
      buttonGroup.buttons().forEach(button -> current[button] += 1);
      buttonGroup.used++;
      presses++;
    }

    public boolean reachEndState() {
      return Arrays.equals(current, endState);
    }

    public Joltage copy() {
      Joltage joltage = new Joltage(endState);
      joltage.current = Arrays.copyOf(current, current.length);
      joltage.presses = presses;
      return joltage;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof Joltage joltage)) return false;
      return presses == joltage.presses && Objects.deepEquals(current, joltage.current) && Objects.deepEquals(endState, joltage.endState);
    }

    @Override
    public int hashCode() {
      return Objects.hash(Arrays.hashCode(current), Arrays.hashCode(endState), presses);
    }

    @Override
    public String toString() {
      String s = "[";
      for (int i : current) {
        s += i;
      }
      s += "]";
      return s;
    }

    public boolean impossible() {
      for (int index = 0; index < current.length; index++) {
        if (current[index] > endState[index]) {
          return true;
        }
      }
      return false;
    }

    public boolean unreachable(List<ButtonGroup> buttonGroups) {
      for (int index = 0; index < endState.length; index++) {
        int todo = endState[index] - current[index];
        if (todo > 0) {
          int finalIndex = index;
          int available = buttonGroups.stream().filter(bg -> bg.contains(finalIndex)).mapToInt(bg -> bg.maxUse - bg.used).sum();
          if (available < todo) {
            return true;
          }
        }
      }
      return false;
    }

    public List<Integer> exhausted() {
      List<Integer> exhausted = new ArrayList<>();
      for (int index = 0; index < endState.length; index++) {
        if (current[index] == endState[index]) {
          exhausted.add(index);
        }
      }
      return exhausted;
    }
  }

  private class JoltageMachine {
    private final List<ButtonGroup> buttonGroups;
    Joltage joltage;

    public JoltageMachine(List<ButtonGroup> buttonGroups, int[] joltage) {
      this.joltage = new Joltage(joltage);
      for (int index = 0; index < joltage.length; index++) {
        int j = joltage[index];
        int fIndex = index;
        buttonGroups.forEach(group -> {
          if (group.contains(fIndex)) {
            group.maxUse = Math.min(group.maxUse, j);
          }
        });
      }
      this.buttonGroups = buttonGroups.stream().sorted(Comparator.comparing(bg -> bg.maxUse)).toList();
    }

    public long getShortestPath() {
      return step(buttonGroups, joltage, buttonGroups.stream().mapToInt(bg -> bg.maxUse).sum());
    }

    public long step(List<ButtonGroup> buttonGroups, Joltage joltage, long min) {
      if (buttonGroups.isEmpty()) {
        return Integer.MAX_VALUE;
      }
      if (joltage.impossible()) {
        return Integer.MAX_VALUE;
      }
      if (joltage.unreachable(buttonGroups)) {
        return Integer.MAX_VALUE;
      }
      if (joltage.presses >= min) {
        return Integer.MAX_VALUE;
      }
      for (ButtonGroup next : buttonGroups) {
        Joltage joltageCopy = joltage.copy();
        joltageCopy.applyButtons(next);
        if (joltageCopy.reachEndState()) {
          return joltageCopy.presses;
        } else {
          List<ButtonGroup> remaining = new ArrayList<>();
          List<Integer> exhausted = joltageCopy.exhausted();
          buttonGroups.forEach(group -> {
            if (!group.isDisabled()) {
              if (exhausted.isEmpty()) {
                remaining.add(group.copy());
              } else if (!group.contains(exhausted)) {
                remaining.add(group.copy());
              }
            }
          });
          long steps = step(remaining, joltageCopy, min);
          if (steps < min) {
            min = steps;
          }

        }
      }
      return min;
    }
  }
}
