package common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public abstract class AssignmentTest<T> {

  private final Assignment<T> assignment;
  private final T expectedPartOne;
  private final T expectedPartTwo;

  public AssignmentTest(Assignment<T> assignment, T expectedPartOne, T expectedPartTwo) {
    this.assignment = assignment;
    this.expectedPartOne = expectedPartOne;
    this.expectedPartTwo = expectedPartTwo;
  }

  @Test
  void testPartOne() {
    assignment.readInput();
    assertEquals(expectedPartOne, assignment.partOne());
  }

  @Test
  public void testPartTwo() {
    assignment.readInput();
    assertEquals(expectedPartTwo, assignment.partTwo());
  }
}
