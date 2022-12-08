package twentytwo;

import common.Assignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class One implements Assignment<Integer> {

  private final List<Elf> elves = new ArrayList<>();

  public static void main(String[] args) {
    One one = new One();
    one.readInput();
    one.answer();
  }

  @Override
  public void readInput() {
    Elf elf = new Elf();
    List<String> lines = readInputLines();
    for (String line : lines) {
      if (!line.isBlank()) {
        elf.addFood(Integer.parseInt(line));
      } else {
        elves.add(elf);
        elf = new Elf();
      }
    }
    elves.add(elf);
  }

  @Override
  public Integer partOne() {
    return elves.stream().mapToInt(Elf::getTotalCalories).max()
        .orElseThrow(() -> new RuntimeException("something wrong"));
  }

  @Override
  public Integer partTwo() {
    return elves.stream().map(Elf::getTotalCalories).sorted(Collections.reverseOrder()).mapToInt(Integer::intValue)
        .limit(3).sum();
  }

  private static class Elf {
    List<Integer> food = new ArrayList<>();

    public int getTotalCalories() {
      return food.stream().mapToInt(Integer::intValue).sum();
    }

    public void addFood(Integer calories) {
      food.add(calories);
    }

    @Override
    public String toString() {
      return "total = " + getTotalCalories();
    }
  }
}
