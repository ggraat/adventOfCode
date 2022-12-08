package common;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public interface Assignment<T> {

  void readInput();

  T partOne();

  T partTwo();

  default List<String> readInputLines() {
    URL resource = getClass().getResource(getClass().getSimpleName());
    try {
      return Files.readAllLines(Path.of(resource.toURI()));
    } catch (IOException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  default void answer() {
    System.out.println("Answer to part 1: " + partOne());
    System.out.println("Answer to part 2: " + partTwo());
  }
}
