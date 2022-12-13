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
    List<Node> nodes = new ArrayList<>();
    for (Pair pair : pairs) {
      if (pair.rightOrder()) {
        nodes.add(pair.left);
        nodes.add(pair.right);
      } else {
        nodes.add(pair.right);
        nodes.add(pair.left);
      }
    }
    Node dividerPacket = new ListNode();
    ListNode listNode = new ListNode();
    listNode.addSubNode(new ValueNode(2));
    dividerPacket.addSubNode(listNode);
    Node dividerPacket2 = new ListNode();
    ListNode listNode2 = new ListNode();
    listNode2.addSubNode(new ValueNode(6));
    dividerPacket2.addSubNode(listNode2);
    nodes.add(dividerPacket);
    nodes.add(dividerPacket2);
    List<Node> sorted = getSortedNodes(nodes);
    return String.valueOf((sorted.indexOf(dividerPacket) + 1) * (sorted.indexOf(dividerPacket2) + 1));
  }

  private List<Node> getSortedNodes(List<Node> nodes) {
    List<Node> sorted = new ArrayList<>();
    while (!nodes.isEmpty()) {
      Node lowest = getLowestNode(nodes);
      sorted.add(lowest);
      nodes.remove(lowest);
    }
    return sorted;
  }

  private Node getLowestNode(List<Node> nodes) {
    Node first = nodes.get(0);
    for (int i = 1; i < nodes.size(); i++) {
      if (!new Pair(first, nodes.get(i)).rightOrder()) {
        first = nodes.get(i);
      }
    }
    return first;
  }

  private static class NodeParser {
    private static final char START = '[';
    private static final char END = ']';
    private static final char COMMA = ',';

    public Node parse(String line) {
      Deque<Node> nodes = new ArrayDeque<>();
      char[] charArray = line.toCharArray();
      for (int i = 0; i < charArray.length; i++) {
        char c = charArray[i];
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
            if (charArray[i + 1] == 48) { // include 10 :p
              value = 10;
              i++;
            }
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
      List<Node> leftSubs = left.subs;
      List<Node> rightSubs = right.subs;
      for (int i = 0; i < leftSubs.size(); i++) {
        if (i >= rightSubs.size()) {
          correct = false;
          return;
        }
        Node leftSub = leftSubs.get(i);
        Node rightSub = rightSubs.get(i);
        compareNode(leftSub, rightSub);
        if (correct != null) {
          return;
        }
      }
      if (leftSubs.size() < rightSubs.size()) {
        correct = true;
      }
    }

    private void compareValue(ValueNode left, ValueNode right) {
      if (left.value < right.value) {
        correct = true;
      } else if (left.value > right.value) {
        correct = false;
      }
      // left == right -> undetermined
    }
  }

  private static class Node {
    List<Node> subs = new ArrayList<>();

    public void addSubNode(Node sub) {
      subs.add(sub);
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
