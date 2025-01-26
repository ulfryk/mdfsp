package organisation.infra.api

import cats.syntax.all.*
import io.circe.{Decoder, Encoder}
import organisation.domain.ArtistId

object Artist:

  given artistIdEncoder: Encoder[ArtistId] =
    Encoder[Long].contramap { case ArtistId(id) => id }

  given artistIdDecoder: Decoder[ArtistId] =
    Decoder[Long].emap(ArtistId(_).asRight)
  
