package twentytwo;

import common.Assignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Three implements Assignment<Integer> {

  private List<String> lines;

  public static void main(String[] args) {
    Three three = new Three();
    three.readInput();
    three.answer();
  }

  public static int getPriority(Character character) {
    if (character < 97) {
      // uppercase 65 - 90
      return character - 38;
    }
    // lowercase 97 - 122
    return character - 96;
  }

  @Override
  public void readInput() {
    lines = readInputLines();
  }

  @Override
  public Integer partOne() {
    List<Rucksack> rucksacks = new ArrayList<>();
    for (String line : lines) {
      String leftLine = line.substring(0, line.length() / 2);
      String rightLine = line.substring(line.length() / 2);
      Set<Character> left = Arrays.stream(leftLine.split("")).map(s -> s.charAt(0)).collect(Collectors.toSet());
      Set<Character> right = Arrays.stream(rightLine.split("")).map(s -> s.charAt(0)).collect(Collectors.toSet());
      Rucksack rucksack = new Rucksack(left, right);
      rucksacks.add(rucksack);
    }
    int priority = 0;
    for (Rucksack rucksack : rucksacks) {
      Set<Character> common = rucksack.common();
      for (Character character : common) {
        priority += getPriority(character);
      }
    }
    return priority;
  }

  @Override
  public Integer partTwo() {
    List<Group> groups = new ArrayList<>();
    for (int i = 0; i < lines.size() - 2; i += 3) {
      Set<Character> one = Arrays.stream(lines.get(i).split("")).map(s -> s.charAt(0)).collect(Collectors.toSet());
      Set<Character> two = Arrays.stream(lines.get(i + 1).split("")).map(s -> s.charAt(0)).collect(Collectors.toSet());
      Set<Character> three =
          Arrays.stream(lines.get(i + 2).split("")).map(s -> s.charAt(0)).collect(Collectors.toSet());
      Group group = new Group(one, two, three);
      groups.add(group);
    }
    int priority = 0;
    for (Group group : groups) {
      Set<Character> common = group.common();
      for (Character character : common) {
        priority += getPriority(character);
      }
    }
    return priority;
  }

  private static class Group {
    Set<Character> one;
    Set<Character> two;
    Set<Character> three;

    public Group(Set<Character> one, Set<Character> two, Set<Character> three) {
      this.one = one;
      this.two = two;
      this.three = three;
    }

    public Set<Character> common() {
      Set<Character> common = new HashSet<>(one);
      common.retainAll(two);
      common.retainAll(three);
      return common;
    }
  }
  private static class Rucksack {
    Set<Character> left;
    Set<Character> right;

    public Rucksack(Set<Character> left, Set<Character> right) {
      this.left = left;
      this.right = right;
    }

    public Set<Character> common() {
      Set<Character> common = new HashSet<>(left);
      common.retainAll(right);
      return common;
    }
  }
}
