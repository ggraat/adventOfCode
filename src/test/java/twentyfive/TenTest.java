package twentyfive;

import common.AssignmentTest;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TenTest extends AssignmentTest<Long> {

  public TenTest() {
    super(new Ten(), 7L, 33L);
  }

  @Test
  void testPattern() {
    Ten.Machine machine = new Ten.Machine("####.#", List.of(new Ten.ButtonGroup(List.of(0, 1, 2)),
            new Ten.ButtonGroup(List.of(0, 3)), new Ten.ButtonGroup(List.of(3, 4)),
            new Ten.ButtonGroup(List.of(0, 2, 3, 4)), new Ten.ButtonGroup(List.of(0, 1, 2, 4, 5)),
            new Ten.ButtonGroup(List.of(0, 2, 3)), new Ten.ButtonGroup(List.of(1, 3, 4, 5))));
    machine.getPatterns();
  }
}
