package common

class LevenshteinDistance extends munit.FunSuite:

  test("should return 0 for equal strings"):
    val a = "Lorem ipsum, dolor sit amet."

    assertEquals(lev(a, a), 0)
    assertEquals(levEfficient(a, a), 0)

  test("should return 3 for kitten → sitting"):
    assertEquals(lev("kitten", "sitting"), 3)
    assertEquals(levEfficient("kitten", "sitting"), 3)

  test("should return 1 uninformed → uniformed"):
    assertEquals(lev("uninformed", "uniformed"), 1)
    assertEquals(levEfficient("uninformed", "uniformed"), 1)

  test("should return 5 teacher → lecturer"):
    assertEquals(lev("teacher", "lecturer"), 5)
    assertEquals(levEfficient("teacher", "lecturer"), 5)

  test("should return length of non empty string if one is empty"):
    assertEquals(lev("12345678", ""), 8)
    assertEquals(levEfficient("12345678", ""), 8)
