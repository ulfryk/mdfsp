package distribution.infra.api.dto

import _root_.io.circe.*
import _root_.io.circe.generic.auto.*
import cats.effect.IO
import distribution.domain.{AddStream, SongId}
import distribution.infra.api.dto.Song.given
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

import java.time.Instant
import scala.concurrent.duration.{FiniteDuration, SECONDS}

case class CreateStreamRequest(songId: SongId, duration: Long):
  def toCommand: AddStream =
    AddStream(
      songId = songId,
      duration = FiniteDuration(duration, SECONDS),
      startedAt = Instant.now().minusSeconds(60))

object CreateStreamRequest:
  given entityDecoderCreateStreamRequest: EntityDecoder[IO, CreateStreamRequest] = jsonOf[IO, CreateStreamRequest]
