package distribution.infra.api.dto

import distribution.domain.{ReleaseId, ReleaseState, ReleaseTitle}
import io.circe.Encoder

object Release:

  given releaseIdEncoder: Encoder[ReleaseId] =
    Encoder[Long].contramap { case ReleaseId(id) => id }

  given releaseTitleEncoder: Encoder[ReleaseTitle] =
    Encoder[String].contramap { case ReleaseTitle(id) => id }

//  given releaseStateEncoder: Encoder[ReleaseState] =
//    Encoder[String].contramap {
//      case ReleaseState.Created => "Created"
//      case ReleaseState.Distributed => "Distributed"
//      case ReleaseState.Withdrawn => "Withdrawn"
//      case ReleaseState.Proposed(date) => s"Proposed $date"
//      case ReleaseState.Approved(date) => s"Approved $date"
//    }

//  given releaseStateDecoder: Decoder[UpdateStatus] =
//    Decoder[String].emap {
//      case "Withdrawn" => UpdateStatus.Withdrawn.asRight
//      case s"Proposed $date" => extractDate(date).map(UpdateStatus.Proposed.apply)
//      case s"Approved $date" => extractDate(date).map(UpdateStatus.Approved.apply)
//      case other =>
//        s"Expected one of 'Withdrawn', 'Proposed YYYY-MM-DD' or 'Approved YYY-MM-DD' but found '$other'.".asLeft
//    }
//
//  private def extractDate(date: String): Either[String, LocalDate] =
//    Try(LocalDate.parse(date)).toEither.leftMap(_.getMessage)
