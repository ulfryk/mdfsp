package distribution.adapters.rest.dto

import _root_.io.circe.*
import _root_.io.circe.generic.auto.*
import cats.effect.IO
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

import java.time.LocalDate

enum UpdateStatus:
  case Proposed(date: LocalDate)
  case Approved(date: LocalDate)
  case Withdrawn

enum UpdateReleaseRequest:
  case UpdateReleaseState(state: UpdateStatus) extends UpdateReleaseRequest

/**
 * Default enum coding with circe is quite ugly:
 * `{ "UpdateReleaseState": { "state": { "Withdrawn": {} } } }`
 * `{ "UpdateReleaseState": { "state": { "Proposed": { "date": "2025-01-27" } } } }`
 * `{ "UpdateReleaseState": { "state": { "Approved": { "date": "2025-01-27" } } } }`
 */
object UpdateReleaseRequest:
  given entityDecoderUpdateReleaseRequest: EntityDecoder[IO, UpdateReleaseRequest] = jsonOf[IO, UpdateReleaseRequest]

//  jsonOf[IO, Json].transform { x =>
//      x.flatMap { json =>
//        val ss = json.asObject.flatMap(_("state")).flatMap(_.asString) match
//          case Some(st) => st match
//            case "Withdrawn" => UpdateStatus.Withdrawn.asRight
//            case s"Proposed $date" => extractDate(date).map(UpdateStatus.Proposed.apply)
//            case s"Approved $date" => extractDate(date).map(UpdateStatus.Approved.apply)
//            case other =>
//              s"Expected one of 'Withdrawn', 'Proposed YYYY-MM-DD' or 'Approved YYY-MM-DD' but found '$other'.".asLeft
//          case _ => "KaBoom!!".asLeft
//
//        ss.bimap(MalformedMessageBodyFailure(_), UpdateReleaseState.apply)
//      }
//    }
//
//  private def extractDate(date: String): Either[String, LocalDate] =
//    Try(LocalDate.parse(date)).toEither.leftMap(_.getMessage)
