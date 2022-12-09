package twentytwo;

import common.Assignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Nine implements Assignment<Integer> {

  private static final List<Instruction> instructions = new ArrayList<>();

  public static void main(String[] args) {
    Nine nine = new Nine();
    nine.readInput();
    nine.answer();
  }

  @Override
  public void readInput() {
    readInputLines().forEach(line -> {
      String[] split = line.split(" ");
      Instruction instruction = new Instruction(Grid.Direction.valueOf(split[0]), Integer.parseInt(split[1]));
      instructions.add(instruction);
    });
  }

  @Override
  public Integer partOne() {
    Grid grid = new Grid(2);
    instructions.forEach(instruction -> grid.move(instruction.direction, instruction.steps));
    return grid.visited.size();
  }

  @Override
  public Integer partTwo() {
    Grid grid = new Grid(10);
    instructions.forEach(instruction -> grid.move(instruction.direction, instruction.steps));
    return grid.visited.size();
  }

  private record Instruction(Grid.Direction direction, int steps) {
  }

  private static class Grid {
    Set<Position> visited = new HashSet<>();
    Position[] knots;

    enum Direction {
      U, R, D, L
    }

    public Grid(int numberOfKnots) {
      Position start = new Position(0, 0);
      knots = new Position[numberOfKnots];
      Arrays.fill(knots, start);
      visited.add(start);
    }

    public void move(Direction direction, int steps) {
      switch (direction) {
        case U -> {
          for (int i = 0; i < steps; i++) {
            knots[0] = new Position(knots[0].x, knots[0].y + 1);
            moveTail();
          }
        }
        case R -> {
          for (int i = 0; i < steps; i++) {
            knots[0] = new Position(knots[0].x + 1, knots[0].y);
            moveTail();
          }
        }
        case D -> {
          for (int i = 0; i < steps; i++) {
            knots[0] = new Position(knots[0].x, knots[0].y - 1);
            moveTail();
          }
        }
        case L -> {
          for (int i = 0; i < steps; i++) {
            knots[0] = new Position(knots[0].x - 1, knots[0].y);
            moveTail();
          }
        }
      }
    }

    public void moveTail() {
      for (int index = 0; index < knots.length - 1; index++) {
        moveNextKnot(index, index + 1);
      }
      visited.add(knots[knots.length - 1]);
    }

    private void moveNextKnot(int headIndex, int tailIndex) {
      int deltaX = Math.abs(knots[headIndex].x - knots[tailIndex].x);
      int deltaY = Math.abs(knots[headIndex].y - knots[tailIndex].y);
      int totalDelta = deltaX + deltaY;
      switch (totalDelta) {
        case 4: // if head was already diagonally adjacent to tail and then is moved diagonally again, delta becomes 4
        case 3:
          moveDiagonally(headIndex, tailIndex, deltaX, deltaY);
          break;
        case 2:
          if (deltaX == 2) {
            moveHorizontally(headIndex, tailIndex);
          } else if (deltaY == 2) {
            moveVertically(headIndex, tailIndex);
          }
          // diagonally adjacent
          break;
        case 1: // adjacent
        case 0: // overlap
          break;
        default:
          throw new RuntimeException("Head is too far from tail: " + totalDelta);
      }
    }

    private void moveDiagonally(int headIndex, int tailIndex, int deltaX, int deltaY) {
      if (deltaX + deltaY == 4) {
        knots[tailIndex] =
            new Position((knots[headIndex].x + knots[tailIndex].x) / 2, (knots[headIndex].y + knots[tailIndex].y) / 2);
      } else if (deltaX == 1) {
        knots[tailIndex] = new Position(knots[headIndex].x, (knots[headIndex].y + knots[tailIndex].y) / 2);
      } else {
        knots[tailIndex] = new Position((knots[headIndex].x + knots[tailIndex].x) / 2, knots[headIndex].y);
      }
    }

    private void moveHorizontally(int headIndex, int tailIndex) {
      knots[tailIndex] = new Position((knots[headIndex].x + knots[tailIndex].x) / 2, knots[tailIndex].y);
    }

    private void moveVertically(int headIndex, int tailIndex) {
      knots[tailIndex] = new Position(knots[tailIndex].x, (knots[headIndex].y + knots[tailIndex].y) / 2);
    }
  }

  private static class Position {

    int x;
    int y;

    public Position(int x, int y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public String toString() {
      return x + "," + y;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Position position = (Position) o;
      return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
      return Objects.hash(x, y);
    }
  }
}
