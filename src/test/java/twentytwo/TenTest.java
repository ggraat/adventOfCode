package twentytwo;

import common.AssignmentTest;

public class TenTest extends AssignmentTest<String> {

  private static final String part2 =
      "##..##..##..##..##..##..##..##..##..##..\n" + "###...###...###...###...###...###...###.\n"
          + "####....####....####....####....####....\n" + "#####.....#####.....#####.....#####.....\n"
          + "######......######......######......####\n" + "#######.......#######.......#######.....";

  public TenTest() {
    super(new Ten(), String.valueOf(13140), part2);
  }
}
