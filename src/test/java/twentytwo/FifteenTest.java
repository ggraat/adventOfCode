package twentytwo;

import common.AssignmentTest;

public class FifteenTest extends AssignmentTest<String> {

  public FifteenTest() {
    super(new Fifteen(10, 20), String.valueOf(26), String.valueOf(56000011));
  }
}
