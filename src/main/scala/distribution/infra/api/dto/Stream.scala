package distribution.infra.api.dto

import cats.syntax.all.*
import distribution.domain.{StreamId, StreamSequenceId}
import io.circe.{Decoder, Encoder}

object Stream:

  given streamIdEncoder: Encoder[StreamId] =
    Encoder[Long].contramap { case StreamId(id) => id }

  given streamIdDecoder: Decoder[StreamId] =
    Decoder[Long].emap(StreamId(_).asRight)

  given streamSequenceIdEncoder: Encoder[StreamSequenceId] =
    Encoder[Long].contramap { case StreamSequenceId(id) => id }

  given streamSequenceIdDecoder: Decoder[StreamSequenceId] =
    Decoder[Long].emap(StreamSequenceId(_).asRight)