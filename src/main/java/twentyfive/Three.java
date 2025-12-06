package twentyfive;

import common.Assignment;

import java.util.List;

public class Three implements Assignment<Long> {

  private List<Bank> banks;

  static void main(String[] args) {
    Three puzzle = new Three();
    puzzle.readInput();
    puzzle.answer();
  }

  @Override
  public void readInput() {
    banks = readInputLines().stream().map(Bank::new).toList();
  }

  @Override
  public Long partOne() {
    return banks.stream().mapToLong(bank -> bank.getMaxJoltage(2)).sum();
  }

  @Override
  public Long partTwo() {
    return banks.stream().mapToLong(bank -> bank.getMaxJoltage(12)).sum();
  }

  private static class Bank {
    private final String digits;

    private Bank(String digits) {
      this.digits = digits;
    }

    public long getMaxJoltage(int batteries) {
      String joltage = "";
      int index = 0;
      for (int i = 0; i < batteries; i++) {
        String toConsider = digits.substring(index, digits.length() - (batteries - i - 1));
        char max = (char) toConsider.chars().max().getAsInt();
        index = digits.indexOf(max, index) + 1;
        joltage += max;
      }
      return Long.parseLong(joltage);
    }
  }
}
