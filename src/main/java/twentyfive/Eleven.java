package twentyfive;

import common.Assignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Eleven implements Assignment<Long> {

  private Map<String, Node> nodes;

  static void main() {
    Eleven puzzle = new Eleven();
    puzzle.readInput();
    puzzle.answer();
  }

  @Override
  public void readInput() {
    List<String> lines = readInputLines();
    nodes = new HashMap<>();
    for (String line : lines) {
      String[] split = line.split(" ");
      String label = split[0].substring(0, split[0].length() - 1);
      Node node = nodes.computeIfAbsent(label, Node::new);
      for (int i = 1; i < split.length; i++) {
        String childLabel = split[i];
        Node childNode = nodes.computeIfAbsent(childLabel, Node::new);
        node.addChild(childNode);
      }
    }
  }

  @Override
  public Long partOne() {
    Node start = nodes.get("you");
    return start.getPaths();
  }

  @Override
  public Long partTwo() {
    NodeVisitor nodeVisitor = new NodeVisitor();
    long pathsStoF = nodeVisitor.getPathsBetween(nodes.get("svr"), nodes.get("fft"));
    long pathsFtoD = nodeVisitor.getPathsBetween(nodes.get("fft"), nodes.get("dac"));
    long pathsDtoO = nodeVisitor.getPathsBetween(nodes.get("dac"), nodes.get("out"));
    long pathsStoD = nodeVisitor.getPathsBetween(nodes.get("svr"), nodes.get("dac"));
    long pathsDtoF = nodeVisitor.getPathsBetween(nodes.get("dac"), nodes.get("fft")); // <- = 0 so dac always comes first
    long pathsFtoO = nodeVisitor.getPathsBetween(nodes.get("fft"), nodes.get("out"));
    return pathsStoF * pathsFtoD * pathsDtoO;
  }

  static class NodeVisitor {
    private final Map<Node, Long> memo = new HashMap<>();
    private Node endNode;

    public NodeVisitor() {
    }

    private long dfs(Node node) {
      if (memo.containsKey(node)) {
        return memo.get(node);
      }

      if (node == endNode) {
        memo.put(node, 1L);
        return 1L;
      }

      long total = 0;
      for (Node next : node.children) {
        total += dfs(next);
      }

      memo.put(node, total);
      return total;
    }

    public long getPathsBetween(Node startNode, Node endNode) {
      this.endNode = endNode;
      this.memo.clear();
      return dfs(startNode);
    }
  }

  static class Node {
    String label;
    List<Node> children = new ArrayList<>();
    long paths;

    public Node(String label) {
      this.label = label;
    }

    public void addChild(Node child) {
      children.add(child);
    }

    public long getPaths() {
      if (children.isEmpty()) {
        paths = 1;
      } else {
        paths = children.stream().mapToLong(Node::getPaths).sum();
      }
      return paths;
    }

    @Override
    public String toString() {
      return label;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof Node node)) return false;
      return Objects.equals(label, node.label);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(label);
    }
  }
}
