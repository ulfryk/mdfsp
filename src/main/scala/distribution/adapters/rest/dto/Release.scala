package distribution.adapters.rest.dto

import common.http4s.MalformedId
import distribution.domain.{ReleaseId, ReleaseState, ReleaseTitle}
import io.circe.{Decoder, Encoder}

object Release:
  
  given releaseIdEncoder: Encoder[ReleaseId] =
    Encoder[Long].contramap { case ReleaseId(id) => id }
    
  given releaseTitleEncoder: Encoder[ReleaseTitle] =
    Encoder[String].contramap { case ReleaseTitle(id) => id }

  given releaseStateEncoder: Encoder[ReleaseState] =
    Encoder[String].contramap {
      case ReleaseState.Created => "Created"
      case ReleaseState.Distributed => "Distributed"
      case ReleaseState.Withdrawn => "Withdrawn"
      case ReleaseState.Proposed(date) => s"Proposed $date"
      case ReleaseState.Approved(date) => s"Approved $date"
    }
