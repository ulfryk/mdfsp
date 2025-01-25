package distribution.adapters.rest.dto

import cats.syntax.all.*
import io.circe.{Decoder, Encoder}
import organisation.domain.{RecordLabelId, RecordLabelName}

object RecordLabel:

  given recordLabelIdEncoder: Encoder[RecordLabelId] =
    Encoder[Long].contramap { case RecordLabelId(id) => id }

  given recordLabelNameEncoder: Encoder[RecordLabelName] =
    Encoder[String].contramap { case RecordLabelName(t) => t }

  given recordLabelIdDecoder: Decoder[RecordLabelId] =
    Decoder[Long].emap(RecordLabelId(_).asRight)

  given recordLabelNameDecoder: Decoder[RecordLabelName] =
    Decoder[String].map(RecordLabelName.apply)
