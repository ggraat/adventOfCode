package twentytwo;

import common.Assignment;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Five implements Assignment<String> {

  private List<String> instructions;
  private int numberOfStacks;
  private List<String> configuration;

  public static void main(String[] args) {
    Five five = new Five();
    five.readInput();
    five.answer();
  }

  @Override
  public void readInput() {
    List<String> lines = readInputLines();
    numberOfStacks = 0;
    configuration = null;
    instructions = null;
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      if (line.startsWith(" 1")) {
        numberOfStacks = Integer.parseInt(line.substring(line.length() - 1));
        configuration = lines.subList(0, i);
        instructions = lines.subList(i + 2, lines.size());
        break;
      }
    }
  }

  private Stacks initStacks() {
    Stacks stacks = new Stacks(numberOfStacks);
    for (String line : configuration) {
      int stacksOnLine = (line.length() + 1) / 4;
      for (int i = 0; i < stacksOnLine; i++) {
        String crate = line.substring((4 * i) + 1, (4 * i) + 2);
        if (!crate.isBlank()) {
          stacks.addToBeginning(i, crate);
        }
      }
    }
    return stacks;
  }

  @Override
  public String partOne() {
    Stacks stacks = initStacks();
    for (String instruction : instructions) {
      String[] split = instruction.split(" ");
      stacks.move(Integer.parseInt(split[1]), Integer.parseInt(split[3]), Integer.parseInt(split[5]));
    }
    return stacks.getTop();
  }

  @Override
  public String partTwo() {
    Stacks stacks = initStacks();
    for (String instruction : instructions) {
      String[] split = instruction.split(" ");
      stacks.move9001(Integer.parseInt(split[1]), Integer.parseInt(split[3]), Integer.parseInt(split[5]));
    }
    return stacks.getTop();
  }

  private static class Stacks {
    Deque<String>[] stacks;

    public Stacks(int numberOfStacks) {
      List<Deque<String>> stackList = new ArrayList<>();
      for (int i = 0; i < numberOfStacks; i++) {
        Deque<String> stack = new ArrayDeque<>();
        stackList.add(stack);
      }
      stacks = stackList.toArray(new Deque[0]);
    }

    public void addToBeginning(int stack, String crate) {
      stacks[stack].addLast(crate);
    }

    public void move(int number, int from, int to) {
      for (int i = 0; i < number; i++) {
        stacks[to - 1].push(stacks[from - 1].pop());
      }
    }

    public void move9001(int number, int from, int to) {
      Deque<String> temp = new ArrayDeque<>();
      for (int i = 0; i < number; i++) {
        temp.push(stacks[from - 1].pop());
      }
      while (!temp.isEmpty()) {
        stacks[to - 1].push((temp.pop()));
      }
    }

    public String getTop() {
      String result = "";
      for (Deque<String> stack : stacks) {
        result += stack.peek();
      }
      return result;
    }
  }
}
