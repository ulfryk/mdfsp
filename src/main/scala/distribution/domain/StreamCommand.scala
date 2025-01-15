package distribution.domain

import java.time.Instant
import scala.concurrent.duration.FiniteDuration

sealed trait StreamCommand

case class AddStream(songId: SongId, duration: FiniteDuration, startedAt: Instant) extends StreamCommand