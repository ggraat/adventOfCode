package twentytwo;

import common.Assignment;

import java.util.ArrayList;
import java.util.List;

public class Two implements Assignment<Integer> {

  public enum Hand {
    ROCK(1), PAPER(2), SCISSORS(3);

    private final int value;

    Hand(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }
  }

  private List<String> lines;

  public static void main(String[] args) {
    Two two = new Two();
    two.readInput();
    two.answer();
  }

  @Override
  public void readInput() {
    lines = readInputLines();
  }

  @Override
  public Integer partOne() {
    Player one = new Player();
    Player two = new Player();
    Game game = new Game(one, two, lines.size());
    for (String line : lines) {
      String[] split = line.split(" ");
      switch (split[0]) {
        case "A" -> {
          one.addHand(Hand.ROCK);
        }
        case "B" -> {
          one.addHand(Hand.PAPER);
        }
        case "C" -> {
          one.addHand(Hand.SCISSORS);
        }
      }
      switch (split[1]) {
        case "X" -> two.addHand(Hand.ROCK);
        case "Y" -> two.addHand(Hand.PAPER);
        case "Z" -> two.addHand(Hand.SCISSORS);
      }

    }
    game.play();
    return two.totalScore;
  }

  @Override
  public Integer partTwo() {
    Player one = new Player();
    Player two = new Player();
    Game game = new Game(one, two, lines.size());
    for (String line : lines) {
      String[] split = line.split(" ");
      switch (split[0]) {
        case "A" -> {
          one.addHand(Hand.ROCK);
          switch (split[1]) {
            case "X" -> two.addHand(Hand.SCISSORS);
            case "Y" -> two.addHand(Hand.ROCK);
            case "Z" -> two.addHand(Hand.PAPER);
          }
        }
        case "B" -> {
          one.addHand(Hand.PAPER);
          switch (split[1]) {
            case "X" -> two.addHand(Hand.ROCK);
            case "Y" -> two.addHand(Hand.PAPER);
            case "Z" -> two.addHand(Hand.SCISSORS);
          }
        }
        case "C" -> {
          one.addHand(Hand.SCISSORS);
          switch (split[1]) {
            case "X" -> two.addHand(Hand.PAPER);
            case "Y" -> two.addHand(Hand.SCISSORS);
            case "Z" -> two.addHand(Hand.ROCK);
          }
        }
      }
    }
    game.play();
    return two.totalScore;
  }

  private static class Game {
    Player player1;
    Player player2;
    int rounds;

    public Game(Player player1, Player player2, int rounds) {
      this.player1 = player1;
      this.player2 = player2;
      this.rounds = rounds;
    }

    public void play() {
      for (int round = 0; round < rounds; round++) {
        Hand one = player1.draw(round);
        Hand two = player2.draw(round);
        player1.addScore(one.getValue());
        player2.addScore(two.getValue());
        if (one.equals(two)) {
          player1.addScore(3);
          player2.addScore(3);
        } else if (Hand.ROCK.equals(one)) {
          if (Hand.PAPER.equals(two)) {
            player2.addScore(6);
          } else {
            player1.addScore(6);
          }
        } else if (Hand.PAPER.equals(one)) {
          if (Hand.ROCK.equals(two)) {
            player1.addScore(6);
          } else {
            player2.addScore(6);
          }
        } else if (Hand.SCISSORS.equals(one)) {
          if (Hand.ROCK.equals(two)) {
            player2.addScore(6);
          } else {
            player1.addScore(6);
          }
        }
      }
    }
  }
  private static class Player {
    private final List<Hand> strategy = new ArrayList<>();
    private int totalScore;

    public void addHand(Hand hand) {
      strategy.add(hand);
    }

    public Hand draw(int round) {
      return strategy.get(round);
    }

    public void addScore(int score) {
      totalScore += score;
    }
  }
}
