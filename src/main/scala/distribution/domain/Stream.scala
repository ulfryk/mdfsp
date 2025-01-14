package distribution.domain

import java.time.Instant
import scala.concurrent.duration.FiniteDuration

opaque type StreamId = Long

// TODO: So our ubiquitous language clashes with common abstractions…
case class Stream(
  id: StreamId,
  songId: SongId,
  duration: FiniteDuration, // or should we use our own type on top of Int?
  startedAt: Instant,
)
