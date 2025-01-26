package distribution.infra.api.dto

import _root_.io.circe.*
import _root_.io.circe.generic.auto.*
import cats.effect.IO
import cats.syntax.all.*
import Song.given
import distribution.domain.SongTitle
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

final case class CreateSongRequest(title: SongTitle)

object CreateSongRequest:
  given entityDecoderCreateSongRequest: EntityDecoder[IO, CreateSongRequest] = jsonOf[IO, CreateSongRequest]
