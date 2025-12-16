package twentyfive;

import common.Assignment;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    long sum = 0L;
    for (Machine machine : machines) {
      sum += machine.getPatterns().stream().mapToLong(List::size).min().getAsLong();
    }
    return sum;
  }

  @Override
  public Long partTwo() {
    long sum = 0L;
    for (int i = 0; i < joltages.size(); i++) {
      JoltageMachine joltage = joltages.get(i);
      long shortestPath = joltage.getFewestPresses(joltage.joltage.state);
      System.out.println(Arrays.toString(joltage.joltage.state) + " " + shortestPath);
      sum += shortestPath;
    }
    return sum;
  }

  static class Indicator {
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
  }

  static class Machine {
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

    public List<List<ButtonGroup>> getPatterns() {
      Deque<Work> stack = new ArrayDeque<>();
      stack.push(new Work(buttonGroups, indicator, new ArrayList<>()));
      List<List<ButtonGroup>> groups = new ArrayList<>();
      while (!stack.isEmpty()) {
        Work work = stack.pop();
        if (work.buttonGroups.isEmpty()) {
          continue;
        }
        for (int i = 0; i < work.buttonGroups().size(); i++) {
          ButtonGroup next = work.buttonGroups.get(i);
          Indicator indicatorCopy = work.indicator.copy();
          indicatorCopy.toggleButtons(next);
          List<ButtonGroup> used = new ArrayList<>(work.usedButtons);
          used.add(next);
          if (indicatorCopy.reachEndState()) {
            groups.add(used);
          }
          List<ButtonGroup> remaining = new ArrayList<>(work.buttonGroups.subList(i + 1, work.buttonGroups.size()));
          stack.push(new Work(remaining, indicatorCopy, used));
        }
      }
      return groups;
    }
  }

  record Work(List<ButtonGroup> buttonGroups, Indicator indicator, List<ButtonGroup> usedButtons) {
  }

  class Joltage {
    int[] state;

    public Joltage(int[] state) {
      this.state = state;
    }
  }

  private class JoltageMachine {
    private final List<ButtonGroup> buttonGroups;
    Joltage joltage;
    Map<Integer, Long> memory = new HashMap<>();

    public JoltageMachine(List<ButtonGroup> buttonGroups, int[] joltage) {
      this.buttonGroups = buttonGroups;
      this.joltage = new Joltage(joltage);
    }
    
    public long getFewestPresses(int[] joltage) {
      if (memory.containsKey(Arrays.hashCode(joltage))) {
        return memory.get(Arrays.hashCode(joltage));
      }
      int divided = 0;
      while (canDivide(joltage)) {
        joltage = divide(joltage);
        divided++;
      }
      if (divided > 0) {
        return 2 * divided * getFewestPresses(joltage);
      }
      String indicatorString = getIndicatorString(joltage);
      Machine machine = new Machine(indicatorString, buttonGroups);
      List<List<ButtonGroup>> patterns = machine.getPatterns();
      long min = Integer.MAX_VALUE;
      for (List<ButtonGroup> buttons : patterns) {
        int[] joltageApplied = applyPattern(joltage, buttons);
        if (notPossible(joltageApplied)) {
          continue;
        }
        if (isFinished(joltageApplied)) {
          int result = buttons.size();
          if (result < min) {
            min = result;
          }
          continue;
        }
        divided = 0;
        while (canDivide(joltageApplied)) {
          joltageApplied = divide(joltageApplied);
          divided++;
        }
        assert !isFinished(joltageApplied);
        assert !canDivide(joltageApplied);
        assert !notPossible(joltageApplied);
        int factor = (divided > 0) ? 2 * divided : 1;
        long presses = factor * getFewestPresses(joltageApplied) + buttons.size();
        if (presses < min) {
          min = presses;
        }
      }
      memory.put(Arrays.hashCode(joltage), min);
      return min;
    }

    private boolean canDivide(int[] joltage) {
      return Arrays.stream(joltage).allMatch(i -> i % 2 == 0);
    }

    private int[] divide(int[] joltage) {
      int[] divided = new int[joltage.length];
      for (int index = 0; index < joltage.length; index++) {
        divided[index] = joltage[index] / 2;
      }
      return divided;
    }

    private boolean notPossible(int[] joltage) {
      return Arrays.stream(joltage).anyMatch(i -> i < 0);
    }

    private boolean isFinished(int[] joltage) {
      return Arrays.stream(joltage).sum() == 0;
    }

    private int[] applyPattern(int[] joltage, List<ButtonGroup> pattern) {
      int[] applied = Arrays.copyOf(joltage, joltage.length);
      for (ButtonGroup buttonGroup : pattern) {
        buttonGroup.buttons.forEach(button -> applied[button]--);
      }
      return applied;
    }

    private String getIndicatorString(int[] joltage) {
      String s = "";
      for (int j : joltage) {
        if (j % 2 == 0) {
          s += ".";
        } else {
          s += "#";
        }
      }
      return s;
    }
  }
}
