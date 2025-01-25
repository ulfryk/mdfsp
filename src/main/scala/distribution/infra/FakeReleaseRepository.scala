package distribution.infra

import cats.Monad
import cats.syntax.all.*
import common.levEfficient
import distribution.domain.*
import distribution.domain.ReleaseState.*
import distribution.infra.FakeReleaseRepository.{allSongs, releases}
import organisation.domain.{ArtistId, RecordLabelId}

import java.time.LocalDate
import scala.collection.mutable

private class FakeReleaseRepository[F[_] : Monad] extends ReleaseRepository[F]:
  def find(id: ReleaseId): F[Option[Release]] = releases.get(id).pure
  def get(id: ReleaseId): F[Release] = releases(id).pure

  def save(command: ReleaseCommand): F[Release] =
    command match
      case AddSong(_, releaseId, title) => addSong(releaseId, title)
      case ApproveReleaseDate(_, releaseId, date) => approveReleaseDate(releaseId, date)
      case SetReleaseDate(_, releaseId, date) => setReleaseDate(releaseId, date)
      case WithdrawRelease(_, releaseId) => withdraw(releaseId)
      case DistributeRelease(_, releaseId) => distribute(releaseId)

  def listReleasedSongs(query: String): F[List[Song]] =
    val search = normalize(query)
    releases.values
      .filter(_.state == Distributed)
      .flatMap(_.songs).toList
      .map(song => (levEfficient(search, normalizeTitle(song.title)), song))
      .sortBy(_._1).map(_._2).pure

  private def normalize(text: String): String = text.replaceAll("[^a-zA-Z0-9]", "").toLowerCase
  private def normalizeTitle(title: SongTitle): String = normalize(SongTitle.asString(title))

  private def addSong(id: ReleaseId, title: SongTitle): F[Release] =
    updateRelease(id, appendSong(_, title))

  private def approveReleaseDate(id: ReleaseId, date: LocalDate): F[Release] =
    updateRelease(id, updateState(_, Approved(date)))

  private def setReleaseDate(id: ReleaseId, date: LocalDate): F[Release] =
    updateRelease(id, updateState(_, Proposed(date)))

  private def withdraw(id: ReleaseId): F[Release] =
    updateRelease(id, updateState(_, Withdrawn))

  private def distribute(id: ReleaseId): F[Release] =
    // In case we use a transactional outbox - create event here too.
    updateRelease(id, updateState(_, Distributed))

  private def appendSong(release: Release, title: SongTitle): Release =
    val newId = if allSongs.isEmpty then 1L else allSongs.map(song => SongId.toLong(song.id)).max + 1
    release.copy(
      songs = release.songs :+ Song(SongId(newId), release.id, title)
    )

  private def updateState(release: Release, state: ReleaseState): Release =
    release.copy(state = state)

  private def updateRelease(id: ReleaseId, fn: Release => Release): F[Release] =
    get(id).map(fn).flatTap { release =>
      releases(id) = release
      ().pure() // So cats don't provide `.tap`, but it seems wise.
    }

object FakeReleaseRepository:
  def apply[F[_] : Monad]: ReleaseRepository[F] = new FakeReleaseRepository[F]()

  val releases = mutable.HashMap(ReleaseId(1) -> Release(
    id = ReleaseId(1),
    artistId = ArtistId(321),
    recordLabelId = RecordLabelId(123),
    title = ReleaseTitle("Nice Album"),
    state = Created,
    songs = List.empty,
  ))

  def allSongs: List[Song] = releases.values.flatMap(_.songs).toList
