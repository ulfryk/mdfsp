package distribution.infra.api.dto

import distribution.domain.{SongId, Stream, StreamId, StreamSequenceId}

import java.time.Instant

case class StreamResponse(
  id: StreamId,
  sequenceId: StreamSequenceId,
  songId: SongId,
  duration: Long,
  startedAt: Instant,
)

object StreamResponse:
  def apply(model: Stream): StreamResponse =
    StreamResponse(model.id, model.sequenceId, model.songId, model.duration.toSeconds, model.startedAt)
