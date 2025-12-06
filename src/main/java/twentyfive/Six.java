package twentyfive;

import common.Assignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Six implements Assignment<Long> {

  static void main() {
    Six puzzle = new Six();
    puzzle.answer();
  }

  @Override
  public void readInput() {
  }

  @Override
  public Long partOne() {
    List<Problem> problems = new ArrayList<>();
    List<String> lines = readInputLines();
    String[] split = lines.removeLast().split("\\s+");
    for (String s : split) {
      if ("+".equals(s)) {
        problems.add(new SumProblem());
      } else {
        problems.add(new MultiplyProblem());
      }
    }
    for (String line : lines) {
      Long[] numbers = Arrays.stream(line.trim().split("\\s+")).map(Long::parseLong).toArray(Long[]::new);
      for (int index = 0; index < numbers.length; index++) {
        Long number = numbers[index];
        problems.get(index).addNumber(number);
      }
    }
    return problems.stream().mapToLong(Problem::calculate).sum();
  }

  @Override
  public Long partTwo() {
    List<Problem> problems = new ArrayList<>();
    List<String> lines = readInputLines();
    String[] split = lines.removeLast().split("\\s+");
    for (String s : split) {
      if ("+".equals(s)) {
        problems.add(new SumProblem());
      } else {
        problems.add(new MultiplyProblem());
      }
    }
    String[][] tokens = lines.stream().map(line -> Arrays.stream(line.split("")).toArray(String[]::new)).toArray(String[][]::new);
    Problem current = problems.removeLast();
    long total = 0;
    for (int col = tokens[0].length - 1; col >= 0; col--) {
      String numberValue = "";
      for (int row = 0; row < tokens.length; row++) {
        if (!tokens[row][col].isBlank()) {
          numberValue += tokens[row][col];
        }
      }
      if (!numberValue.isBlank()) {
        current.addNumber(Long.parseLong(numberValue));
      } else {
        total += current.calculate();
        current = problems.removeLast();
      }
    }
    // calculate last problem
    total += current.calculate();
    return total;
  }

  private static abstract class Problem {
    protected final List<Long> numbers = new ArrayList<>();

    public void addNumber(long number) {
      numbers.add(number);
    }

    public abstract long calculate();
  }

  public class MultiplyProblem extends Problem {
    @Override
    public long calculate() {
      return numbers.stream().reduce(1L, (aLong, aLong2) -> aLong * aLong2);
    }
  }

  public class SumProblem extends Problem {
    public long calculate() {
      return numbers.stream().mapToLong(n -> n).sum();
    }
  }
}
