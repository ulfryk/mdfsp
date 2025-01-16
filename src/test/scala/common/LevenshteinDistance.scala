package common

class LevenshteinDistance extends munit.FunSuite:

  test("should return 0 for equal strings"):
    val a = "Lorem ipsum, dolor sit amet."

    assertEquals(lev(a, a), 0)

  test("should return 3 for kitten → sitting"):
    assertEquals(lev("kitten", "sitting"), 3)

  test("should return 1 uninformed → uniformed"):
    assertEquals(lev("uninformed", "uniformed"), 1)

  test("should return length of non empty string if one is empty"):
    assertEquals(lev("12345678", ""), 8)
