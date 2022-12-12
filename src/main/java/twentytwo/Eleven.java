package twentytwo;

import common.Assignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.LongPredicate;
import java.util.function.LongUnaryOperator;

public class Eleven implements Assignment<String> {

  private Game game;

  public static void main(String[] args) {
    Eleven eleven = new Eleven();
    eleven.readInput();
    eleven.answer();
  }

  @Override
  public void readInput() {
    List<Monkey> monkeys = new ArrayList<>();
    List<String> lines = readInputLines();
    for (int i = 0; i < lines.size(); i += 7) {
      List<Long> items =
          Arrays.stream(lines.get(i + 1).substring("  Starting items: ".length()).split(", ")).map(Long::new).toList();
      String operationString = lines.get(i + 2).substring("  Operation: new = old ".length());
      LongUnaryOperator operation = parseOperation(operationString);
      long divisor = Long.parseLong(lines.get(i + 3).substring("  Test: divisible by ".length()));
      LongPredicate test = value -> value % divisor == 0;
      int positiveIndex = Integer.parseInt(lines.get(i + 4).substring("    If true: throw to monkey ".length()));
      int negativeIndex = Integer.parseInt(lines.get(i + 5).substring("    If false: throw to monkey ".length()));
      Monkey monkey = new Monkey(new ArrayList<>(items), operation, test, positiveIndex, negativeIndex);
      monkeys.add(monkey);
    }
    game = new Game(monkeys);
  }

  private LongUnaryOperator parseOperation(String operationString) {
    String[] split = operationString.split(" ");
    if ("old".equals(split[1])) {
      switch (split[0]) {
        case "*" -> {
          return value -> value * value;
        }
        case "+" -> {
          return value -> value + value;
        }
      }
    }
    long level = Long.parseLong(split[1]);
    switch (split[0]) {
      case "*" -> {
        return value -> value * level;
      }
      case "+" -> {
        return value -> value + level;
      }
    }
    throw new IllegalArgumentException("Unknown operation " + split[0]);
  }

  @Override
  public String partOne() {
    for (int i = 0; i < 20; i++) {
      game.round();
    }
    return String.valueOf(game.getMonkeyBusiness());
  }

  @Override
  public String partTwo() {
    return null;
  }

  private static class Game {
    List<Monkey> monkeys;
    int round = 1;

    public Game(List<Monkey> monkeys) {
      this.monkeys = monkeys;
    }

    public void round() {
      for (Monkey monkey : monkeys) {
        Map<Integer, List<Long>> result = monkey.play();
        result.forEach((key, value) -> monkeys.get(key).items.addAll(value));
      }
      printResult();
      round++;
    }

    private void printResult() {
      System.out.println("Result after round " + round);
      for (int i = 0; i < monkeys.size(); i++) {
        Monkey monkey = monkeys.get(i);
        System.out.println("Monkey " + i + ": " + monkey.items);
      }
    }

    public long getMonkeyBusiness() {
      return monkeys.stream().map(monkey -> monkey.inspected).sorted(Collections.reverseOrder()).limit(2).reduce(1,
          (val1, val2) -> val1 * val2);
    }
  }

  private static class Monkey {

    public Monkey(List<Long> items, LongUnaryOperator operation, LongPredicate test, int positiveIndex,
        int negativeIndex) {
      this.items = items;
      this.operation = operation;
      this.test = test;
      this.positiveIndex = positiveIndex;
      this.negativeIndex = negativeIndex;
    }

    List<Long> items;

    LongUnaryOperator operation;

    LongPredicate test;

    int inspected;
    private final int positiveIndex;
    private final int negativeIndex;

    public Map<Integer, List<Long>> play() {
      List<Long> positiveList = new ArrayList<>();
      List<Long> negativeList = new ArrayList<>();
      while (!items.isEmpty()) {
        long item = items.remove(0);
        long level = operation.applyAsLong(item) / 3;
        inspected++;
        if (test.test(level)) {
          positiveList.add(level);
        } else {
          negativeList.add(level);
        }
      }
      return Map.of(positiveIndex, positiveList, negativeIndex, negativeList);
    }
  }
}
