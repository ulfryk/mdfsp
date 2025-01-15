package distribution.domain

import java.time.Instant
import scala.concurrent.duration.FiniteDuration

opaque type StreamId = Long
opaque type StreamSequenceId = Long

// TODO: So our ubiquitous language clashes with common abstractions…
case class Stream(
  id: StreamId,
  sequenceId: StreamSequenceId,
  songId: SongId,
  duration: FiniteDuration, // or should we use our own type on top of Int?
  startedAt: Instant,
)
