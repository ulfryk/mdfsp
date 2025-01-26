package distribution.infra.api

import _root_.io.circe.*
import _root_.io.circe.generic.auto.*
import _root_.io.circe.syntax.*
import cats.MonadThrow
import cats.effect.*
import cats.implicits.*
import common.helpers.raiseErrorOnInvalid
import common.http4s.ApiId
import distribution.domain.*
import distribution.infra.api.dto.RecordLabel.given
import distribution.infra.api.dto.Release.given
import distribution.infra.api.dto.Song.given
import distribution.infra.api.dto.*
import org.http4s.*
import org.http4s.Header.*
import org.http4s.circe.*
import org.http4s.dsl.io.*
import org.http4s.headers.`Content-Type`
import organisation.domain.{ArtistId, RecordLabelId}
import organisation.infra.api.Artist.given

case class InvalidApiId(override val message: String) extends MessageFailure:
  override def cause: Option[Throwable] = None
  override def toHttpResponse[F[_]](httpVersion: HttpVersion): Response[F] = Response(status = BadRequest)

val `application/json` = MediaType("application", "json")

extension (req: Request[IO])
  def withContentTypeX(expected: MediaType)(fn: => IO[Response[IO]]): IO[Response[IO]] =
    req.headers.get[`Content-Type`] match
      case Some(ct) => ct.mediaType match
        case mediaType if mediaType == expected => fn
        case other => IO.raiseError(MediaTypeMismatch(other, Set(expected)))
      case None => IO.raiseError(MediaTypeMissing(Set(expected)))

def distributionRoutes(service: ReleaseService[IO])(using MonadThrow[IO]) = HttpRoutes.of[IO] {

  case req @ POST -> Root / "releases" / ApiId(releaseId) / "songs" => req.withContentTypeX(`application/json`) {
    for
      idVal <- releaseId.raiseErrorOnInvalid { es =>
        InvalidApiId("releaseId: " + es.foldLeft("")(_ ++ _.message))
      }
      input <- req.as[CreateSongRequest]
      artistId = ArtistId(321) // should come from auth token most probably
      command = AddSong(artistId, ReleaseId(idVal), input.title)
      created <- service.addSong(command)
      // That `.find(…).get.id` part below is questionable… Just for the sake of MVP
      output = SongResponse(created.songs.find(_.title == input.title).get.id, created.id, input.title)
      resp <- Ok(output.asJson)
    yield resp
  }

  case GET -> Root / "released-songs" :? SearchQueryParam.Matcher(searchQuery) =>
    for
      search <- searchQuery match
        case Some(query) => query.raiseErrorOnInvalid { es =>
          InvalidApiId("releaseId: " + es.foldLeft("")(_ ++ _.message))
        }.map(_.some)
        case None => None.pure()
      found <- service.listReleasedSongs(search.map(SearchQueryParam.toString))
      output = found.map { song => SongResponse(song.id, ReleaseId(1), song.title) }
      resp <- Ok(output.asJson)
    yield resp

  case GET -> Root / "releases" / ApiId(releaseId) =>
    for {
      idVal <- releaseId.raiseErrorOnInvalid { es =>
        InvalidApiId("releaseId: " + es.foldLeft("")(_ ++ _.message))
      }
      releaseOpt <- service.find(ReleaseId(idVal))
      resp <- releaseOpt match
        case None => NotFound(s"Release '$idVal' not found.")
        case Some(release) => Ok(ReleaseResponse(release).asJson)
    } yield resp

  case req @ PATCH -> Root / "releases" / ApiId(releaseId) => req.withContentTypeX(`application/json`) {
    for {
      idVal <- releaseId.raiseErrorOnInvalid { es =>
        InvalidApiId("releaseId: " + es.foldLeft("")(_ ++ _.message))
      }
      input <- req.as[UpdateReleaseRequest]
      artistId = ArtistId(321) // should come from auth token most probably
      recordLabelId = RecordLabelId(123) // should come from auth token most probably
      releaseId = ReleaseId(idVal)
      release <- input match
        case UpdateReleaseRequest.UpdateReleaseState(state) => state match
          case UpdateStatus.Withdrawn =>
            service.withdrawRelease(WithdrawRelease(artistId, releaseId))
          case UpdateStatus.Approved(date) =>
            service.approveReleaseDate(ApproveReleaseDate(recordLabelId, releaseId, date))
          case UpdateStatus.Proposed(date) =>
            service.setReleaseDate(SetReleaseDate(artistId, releaseId, date))
      resp <- Ok(ReleaseResponse(release).asJson)
    } yield resp
  }

  // #===============#
  // # TEMP TECH API #
  // #===============#
  case POST -> Root / "releases" / ApiId(releaseId) / "force-distributed" =>
    for {
      idVal <- releaseId.raiseErrorOnInvalid { es =>
        InvalidApiId("releaseId: " + es.foldLeft("")(_ ++ _.message))
      }
      release <- service.distributeRelease(DistributeRelease(ArtistId(321), ReleaseId(idVal)))
      resp <- Ok(ReleaseResponse(release).asJson)
    } yield resp

}
