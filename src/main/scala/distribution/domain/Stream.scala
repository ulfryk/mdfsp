package distribution.domain

import cats.syntax.all.*
import java.time.Instant
import scala.concurrent.duration.FiniteDuration

opaque type StreamId = Long

object StreamId:
  def apply(id: Long): StreamId = id
  def unapply(id: StreamId): Option[Long] = id.some
  def toLong(id: StreamId): Long = id

opaque type StreamSequenceId = Long

object StreamSequenceId:
  def apply(id: Long): StreamSequenceId = id
  def unapply(id: StreamSequenceId): Option[Long] = id.some
  def toLong(id: StreamSequenceId): Long = id
  given Ordering[StreamSequenceId] = Ordering.by[StreamSequenceId, Long](toLong)


// TODO: So our ubiquitous language clashes with common abstractions…
case class Stream(
  id: StreamId,
  sequenceId: StreamSequenceId,
  songId: SongId,
  duration: FiniteDuration, // or should we use our own type on top of Int?
  startedAt: Instant,
)
