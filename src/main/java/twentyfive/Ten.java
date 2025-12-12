package twentyfive;

import common.Assignment;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
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

    public ButtonGroup copy() {
      ButtonGroup buttonGroup = new ButtonGroup(buttons);
      buttonGroup.maxUse = maxUse;
      return buttonGroup;
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
    int[] state;
    private final List<ButtonGroup> buttonGroups;
    long presses;

    public Joltage(int[] state, List<ButtonGroup> buttonGroups) {
      this.state = state;
      this.buttonGroups = buttonGroups;
    }

    public void applyButtons(ButtonGroup buttonGroup) {
      buttonGroup.buttons().forEach(button -> state[button]--);
      presses++;
    }

    public boolean reachEndState() {
      return Arrays.stream(state).sum() == 0;
    }

    public Joltage copy() {
      Joltage joltage = new Joltage(Arrays.copyOf(state, state.length), buttonGroups);
      joltage.presses = presses;
      return joltage;
    }

    public int minPressedNeeded() {
      return Arrays.stream(state).max().orElse(Integer.MAX_VALUE);
    }

    @Override
    public String toString() {
      String s = "[";
      for (int i : state) {
        s += i;
      }
      s += "]";
      return s;
    }

    public boolean impossible() {
      return Arrays.stream(state).anyMatch(i -> i < 0);
    }

    public boolean unreachable(List<ButtonGroup> buttonGroups) {
      for (int index = 0; index < state.length; index++) {
        int todo = state[index];
        if (todo > 0) {
          int finalIndex = index;
          if (buttonGroups.stream().noneMatch(bg -> bg.contains(finalIndex))) {
            return true;
          }
        }
      }
      return false;
    }

    public List<Integer> exhausted() {
      List<Integer> exhausted = new ArrayList<>();
      for (int index = 0; index < state.length; index++) {
        if (state[index] == 0) {
          exhausted.add(index);
        }
      }
      return exhausted;
    }

    private int getMinIndex() {
      int min = 1000;
      int minIndex = 0;
      for (int index = 0; index < state.length; index++) {
        if (state[index] != 0 && state[index] < min) {
          min = state[index];
          minIndex = index;
        }
      }
      return minIndex;
    }

    public List<ButtonGroup> getButtonGroupsToProcess(List<ButtonGroup> buttonGroups) {
      List<ButtonGroup> toProcess = new ArrayList<>();
      int minIndex = getMinIndex();
      List<Integer> exhausted = exhausted();
      for (ButtonGroup bg : buttonGroups) {
        if (bg.contains(minIndex) && !bg.contains(exhausted)) {
          toProcess.add(bg);
        }
      }
      return toProcess;
    }
  }

  private class JoltageMachine {
    private final List<ButtonGroup> buttonGroups;
    Joltage joltage;

    public JoltageMachine(List<ButtonGroup> buttonGroups, int[] joltage) {
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
      this.joltage = new Joltage(joltage, buttonGroups);
    }

    public long getShortestPath() {
      Deque<Node> queue = new ArrayDeque<>();
      queue.add(new Node(joltage, buttonGroups));
      while (!queue.isEmpty()) {
        Node node = queue.poll();
        List<ButtonGroup> toProcess = node.joltage.getButtonGroupsToProcess(node.buttonGroups);
        for (ButtonGroup bg : toProcess) {
          Joltage joltageCopy = node.joltage.copy();
          joltageCopy.applyButtons(bg);
          if (joltageCopy.reachEndState()) {
            return node.depth + 1;
          } else {
            Node child = new Node(joltageCopy, node.buttonGroups);
            child.depth = node.depth + 1;
            queue.add(child);
          }
        }
      }
      return 0;
//      return step(buttonGroups, joltage, buttonGroups.stream().mapToInt(bg -> bg.maxUse).sum());
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
      if (joltage.presses + joltage.minPressedNeeded() >= min) {
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
            if (!group.contains(exhausted)) {
              remaining.add(group.copy());
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

  class Node {
    Joltage joltage;
    List<ButtonGroup> buttonGroups;
    int depth;

    public Node(Joltage joltage, List<ButtonGroup> buttonGroups) {
      this.joltage = joltage;
      this.buttonGroups = buttonGroups;
    }
  }
}
