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
      System.out.println(i + ": " + Arrays.toString(joltage.joltage.state) + " " + shortestPath);
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
    String indicatorString;

    public Machine(String indicatorString, List<ButtonGroup> buttonGroups) {
      this.indicatorString = indicatorString;
      this.buttonGroups = buttonGroups;
    }

    private Indicator createIndicator(String indicatorString) {
      boolean[] endState = new boolean[indicatorString.length()];
      for (int i = 0; i < indicatorString.length(); i++) {
        if (indicatorString.charAt(i) == '#') {
          endState[i] = true;
        }
      }
      return new Indicator(endState);
    }

    public List<List<ButtonGroup>> getPatterns() {
      return getPatterns(indicatorString);
    }

    public List<List<ButtonGroup>> getPatterns(String indicatorString) {
      Indicator indicator = createIndicator(indicatorString);
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

  record State(int[] joltage) {
    @Override
    public boolean equals(Object o) {
      return o instanceof State s && Arrays.equals(joltage, s.joltage);
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(joltage);
    }
  }

  class Joltage {
    int[] state;

    public Joltage(int[] state) {
      this.state = state;
    }
  }

  private class JoltageMachine {
    Joltage joltage;
    Map<State, Long> memory = new HashMap<>();
    private Machine machine;
    Map<String, List<List<ButtonGroup>>> patternCache = new HashMap<>();

    public JoltageMachine(List<ButtonGroup> buttonGroups, int[] joltage) {
      this.joltage = new Joltage(joltage);
      machine = new Machine("", buttonGroups);
    }

    public long getFewestPresses(int[] joltage) {
      if (memory.containsKey(new State(joltage))) {
        return memory.get(new State(joltage));
      }
      long min = 100_000L;
      if (canDivide(joltage)) {
        int[] dividedJoltage = divide(joltage);
        long presses = 2 * getFewestPresses(dividedJoltage);
        if (presses < min) {
          min = presses;
        }
      }
      String indicatorString = getIndicatorString(joltage);
      List<List<ButtonGroup>> patterns = patternCache.computeIfAbsent(indicatorString, pattern -> machine.getPatterns(pattern));
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
        int divided = 1;
        int[] joltageAppliedCopy = joltageApplied;
        while (canDivide(joltageAppliedCopy)) {
          long presses = 2L * divided * getFewestPresses(joltageAppliedCopy) + buttons.size();
          if (presses < min) {
            min = presses;
          }
          joltageAppliedCopy = divide(joltageAppliedCopy);
          divided++;
        }
        long presses = getFewestPresses(joltageApplied) + buttons.size();
        if (presses < min) {
          min = presses;
        }
      }
      memory.put(new State(joltage), min);
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
