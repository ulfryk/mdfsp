package common

// Mathematical definition re-written in scala.
// Terribly inefficient -> worst case O(2^n). But could do for MVP prototype.
// Also, not tail recursive. Super huge strings can easily overflow the stack.
// If we didn't need optimisation, we could go with CPS and trampolining.
def lev(a: String, b: String): Int =
  (a, b) match
    case ("", "") => 0
    case ("", x) => x.length
    case (x, "") => x.length
    case _ if a.head == b.head => lev(a.tail, b.tail)
    case _ => List(
      lev(a.tail, b),
      lev(a, b.tail),
      lev(a.tail, b.tail)
    ).min + 1

// To optimize it, we can use distances of the prefixes of the strings and build final distance.
// It's O(n + 1 + m + 1 + (n + 1) * (m + 1)) where n = a.length and m = b.length.
// So it can be expressed as O(m * n).
// Not perfect, but much, much better!
// There is still potential for improvement in terms of space complexity…
def levEfficient(a: String, b: String): Int =
  (a, b) match
    case ("", "") => 0
    case ("", x) => x.length
    case (x, "") => x.length
    case _ => levEfficientCore(a, b)

def levEfficientCore(a: String, b: String): Int =
  // matrix of distances between prefixes
  val distMatrix = Array.fill[Int](a.length + 1, b.length + 1)(0)
  // displaced by one to take into consideration empty prefixes at 0
  val aIndices = a.indices.map(_ + 1)
  val bIndices = b.indices.map(_ + 1)

  // distances between any prefix and empty string is the length of prefix
  for i <- aIndices do
    distMatrix(i)(0) = i
  for j <- bIndices do
    distMatrix(0)(j) = j

  // now fill the rest based on provided edges
  for {
    i <-aIndices
    j <- bIndices
  } do {
    val substCost = if a.charAt(i - 1) == b.charAt(j - 1) then 0 else 1
    distMatrix(i)(j) = List(
      distMatrix(i - 1)(j - 1) + substCost,
      distMatrix(i - 1)(j) + 1, // deleting
      distMatrix(i)(j - 1) + 1, // inserting
    ).min
  }

  distMatrix(a.length)(b.length)
