package twentytwo;

import common.Assignment;

import java.util.ArrayList;
import java.util.List;

public class Seven implements Assignment<Long> {

  private Filesystem filesystem;

  public static void main(String[] args) {
    Seven seven = new Seven();
    seven.readInput();
    seven.answer();
  }

  @Override
  public void readInput() {
    filesystem = new Filesystem();
    List<String> lines = readInputLines();
    for (String line : lines) {
      String[] split = line.split(" ");
      switch (split[0]) {
        case "$" -> {
          switch (split[1]) {
            case "cd" -> {
              switch (split[2]) {
                case "/" -> filesystem.goToRoot();
                case ".." -> filesystem.moveUp();
                default -> filesystem.changeDir(split[2]);
              }
            }
            case "ls" -> {
              // nothing
            }
          }
        }
        case "dir" -> filesystem.addDir(split[1]);
        default -> filesystem.addFile(split[1], Long.parseLong(split[0]));
      }
    }
  }

  @Override
  public Long partOne() {
    return filesystem.getAllDirectories().stream().mapToLong(Directory::calculateSize).filter(value -> value <= 100_000)
        .sum();
  }

  @Override
  public Long partTwo() {
    long totalSpace = 70000000;
    long required = 30000000;
    long inUse = filesystem.root.calculateSize();
    long unused = totalSpace - inUse;
    long toDelete = required - unused;
    return filesystem.getAllDirectories().stream().map(Directory::calculateSize).sorted()
        .filter(size -> size >= toDelete).findFirst().orElseThrow(() -> new RuntimeException("not found!"));
  }

  private record File(String name, long size) {

    @Override
    public String toString() {
      return name;
    }
  }

  private static class Filesystem {
    Directory root = new Directory("/");
    Directory current = root;

    public void addDir(String name) {
      Directory dir = new Directory(name);
      dir.up = current;
      current.subdirs.add(dir);
    }

    public void addFile(String name, long size) {
      current.files.add(new File(name, size));
    }

    public void changeDir(String name) {
      current = current.subdirs.stream().filter(dir -> name.equals(dir.name)).findFirst()
          .orElseThrow(() -> new RuntimeException("dir not found: " + name));
    }

    public void moveUp() {
      current = current.up;
    }

    public void goToRoot() {
      current = root;
    }

    public List<Directory> getDirectories(List<Directory> dirs, Directory start) {
      dirs.add(start);
      start.subdirs.forEach(dir -> getDirectories(dirs, dir));
      return dirs;
    }

    public List<Directory> getAllDirectories() {
      return getDirectories(new ArrayList<>(), root);
    }
  }

  private static class Directory {
    String name;
    List<File> files = new ArrayList<>();
    List<Directory> subdirs = new ArrayList<>();
    Directory up;

    public Directory(String name) {
      this.name = name;
    }

    public long calculateSize() {
      return files.stream().mapToLong(File::size).sum() + subdirs.stream().mapToLong(Directory::calculateSize).sum();
    }

    @Override
    public String toString() {
      return name;
    }
  }
}
