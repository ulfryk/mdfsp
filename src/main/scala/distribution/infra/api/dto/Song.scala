package distribution.infra.api.dto

import cats.syntax.all.*
import distribution.domain.{SongId, SongTitle}
import io.circe.{Decoder, Encoder}

object Song:

  given songIdEncoder: Encoder[SongId] =
    Encoder[Long].contramap { case SongId(id) => id }

  given songTitleEncoder: Encoder[SongTitle] =
    Encoder[String].contramap { case SongTitle(t) => t }

  given songIdDecoder: Decoder[SongId] =
    Decoder[Long].emap(SongId(_).asRight)

  given songTitleDecoder: Decoder[SongTitle] =
    Decoder[String].map(SongTitle.apply)
