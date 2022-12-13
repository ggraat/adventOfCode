package twentytwo;

import common.Assignment;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Thirteen implements Assignment<String> {

  private final List<Pair> pairs = new ArrayList<>();

  public static void main(String[] args) {
    Thirteen assignment = new Thirteen();
    assignment.readInput();
    assignment.answer();
  }

  @Override
  public void readInput() {
    List<String> lines = readInputLines();
    NodeParser parser = new NodeParser();
    for (int i = 0; i < lines.size(); i += 3) {
      Node left = parser.parse(lines.get(i));
      Node right = parser.parse(lines.get(i + 1));
      pairs.add(new Pair(left, right));
    }
  }

  @Override
  public String partOne() {
    int total = 0;
    for (int i = 0; i < pairs.size(); i++) {
      Pair pair = pairs.get(i);
      if (pair.rightOrder()) {
        total += i + 1;
      }
    }
    return String.valueOf(total);
  }

  @Override
  public String partTwo() {
    return null;
  }

  private static class NodeParser {
    private static final char START = '[';
    private static final char END = ']';
    private static final char COMMA = ',';

    public Node parse(String line) {
      Deque<Node> nodes = new ArrayDeque<>();
      for (char c : line.toCharArray()) {
        switch (c) {
          case START -> {
            nodes.push(new ListNode());
          }
          case END -> {
            if (nodes.size() != 1) {
              Node finished = nodes.pop();
              nodes.peek().addSubNode(finished);
            }
          }
          case COMMA -> {
          }
          default -> {
            int value = Integer.parseInt(String.valueOf(c));
            nodes.peek().addSubNode(new ValueNode(value));
          }
        }
      }
      if (nodes.size() != 1) {
        throw new RuntimeException("should be just one node");
      }
      return nodes.pop();
    }
  }

  private static class Pair {
    Node left;
    Node right;

    Boolean correct;

    public Pair(Node left, Node right) {
      this.left = left;
      this.right = right;
    }

    public boolean rightOrder() {
      return inRightOrder(left, right);
    }

    private boolean inRightOrder(Node left, Node right) {
      compareNode(left, right);
      return correct;
    }

    private void compareNode(Node left, Node right) {
      if (correct != null) {
        return;
      }
      if (left instanceof ValueNode lnode) {
        if (right instanceof ValueNode rnode) {
          compareValue(lnode, rnode);
        } else {
          ListNode wrappedNode = new ListNode();
          wrappedNode.addSubNode(lnode);
          compareList(wrappedNode, (ListNode) right);
        }
      } else {
        if (right instanceof ListNode rnode) {
          compareList((ListNode) left, rnode);
        } else {
          ListNode wrappedNode = new ListNode();
          wrappedNode.addSubNode(right);
          compareList((ListNode) left, wrappedNode);
        }
      }
    }

    private void compareList(ListNode left, ListNode right) {
      List<Node> subs = left.subs;
      for (int i = 0; i < subs.size(); i++) {
        Node leftSub = subs.get(i);
        if (i >= right.subs.size()) {
          correct = false;
          return;
        }
        Node rightSub = right.subs.get(i);
        compareNode(leftSub, rightSub);
        if (correct != null) {
          return;
        }
      }
      if (subs.size() < right.subs.size()) {
        correct = true;
      }
    }

    private void compareValue(ValueNode left, ValueNode right) {
      if (left.value < right.value) {
        correct = true;
        return;
      }
      if (right.value < left.value) {
        correct = false;
      }
      // left == right -> undetermined
    }
  }

  private static class Node {
    Node parent;
    List<Node> subs = new ArrayList<>();

    public void addSubNode(Node sub) {
      subs.add(sub);
      sub.parent = this;
    }
  }

  private static class ListNode extends Node {
    @Override
    public String toString() {
      return subs.toString();
    }
  }

  private static class ValueNode extends Node {
    int value;

    public ValueNode(int value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }
  }
}
