package distribution.domain

import java.time.Instant
import scala.concurrent.duration.FiniteDuration

opaque type StreamId = Long
object StreamId:
  def apply(id: Long): StreamId = id
  def toLong(id: StreamId): Long = id
  
opaque type StreamSequenceId = Long
object StreamSequenceId:
  def apply(id: Long): StreamSequenceId = id
  def toLong(id: StreamSequenceId): Long = id

// TODO: So our ubiquitous language clashes with common abstractions…
case class Stream(
  id: StreamId,
  sequenceId: StreamSequenceId,
  songId: SongId,
  duration: FiniteDuration, // or should we use our own type on top of Int?
  startedAt: Instant,
)
