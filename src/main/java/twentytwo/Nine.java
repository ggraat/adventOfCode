package twentytwo;

import common.Assignment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    Grid grid = new Grid();
    instructions.forEach(instruction -> grid.move(instruction.direction, instruction.steps));
    return grid.visited.size();
  }

  @Override
  public Integer partTwo() {
    return null;
  }

  private record Instruction(Grid.Direction direction, int steps) {
  }

  private static class Grid {
    Set<Position> visited = new HashSet<>();
    Position head;
    Position tail;

    enum Direction {
      U, R, D, L
    }

    public Grid() {
      Position start = new Position(0, 0);
      head = start;
      tail = start;
      visited.add(start);
    }

    public void move(Direction direction, int steps) {
      switch (direction) {
        case U -> {
          for (int i = 0; i < steps; i++) {
            head = new Position(head.x, head.y + 1);
            moveTail();
          }
        }
        case R -> {
          for (int i = 0; i < steps; i++) {
            head = new Position(head.x + 1, head.y);
            moveTail();
          }
        }
        case D -> {
          for (int i = 0; i < steps; i++) {
            head = new Position(head.x, head.y - 1);
            moveTail();
          }
        }
        case L -> {
          for (int i = 0; i < steps; i++) {
            head = new Position(head.x - 1, head.y);
            moveTail();
          }
        }
      }
    }

    public void moveTail() {
      int deltaX = Math.abs(head.x - tail.x);
      int deltaY = Math.abs(head.y - tail.y);
      int totalDelta = deltaX + deltaY;
      switch (totalDelta) {
        case 3:
          moveDiagonally(deltaX, deltaY);
          break;
        case 2:
          if (deltaX == 2) {
            moveHorizontally();
          } else if (deltaY == 2) {
            moveVertically();
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

    private void moveDiagonally(int deltaX, int deltaY) {
      int x;
      int y;
      if (deltaX == 1) {
        x = head.x;
        y = (head.y + tail.y) / 2;
      } else {
        x = (head.x + tail.x) / 2;
        y = head.y;
      }
      tail = new Position(x, y);
      visited.add(tail);
    }

    private void moveHorizontally() {
      tail = new Position((head.x + tail.x) / 2, tail.y);
      visited.add(tail);
    }

    private void moveVertically() {
      tail = new Position(tail.x, (head.y + tail.y) / 2);
      visited.add(tail);
    }
  }

  private record Position(int x, int y) {
    @Override
    public String toString() {
      return x + "," + y;
    }
  }
}
