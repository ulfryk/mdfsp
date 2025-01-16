package common

// Mathematical definition re-written in scala. Terribly inefficient -> O(2^n).
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
