package distribution.infra

import cats.Monad
import cats.syntax.all.*
import distribution.domain.*
import distribution.domain.ReleaseState.{Approved, Created, Proposed, Withdrawn}
import distribution.infra.FakeReleaseRepository.{allSongs, releases}
import organisation.domain.{ArtistId, RecordLabelId}

import java.time.LocalDate
import scala.collection.mutable

private class FakeReleaseRepository[F[_] : Monad] extends ReleaseRepository[F]:
  def get(id: ReleaseId): F[Release] =
    releases(id).pure()

  def save(command: ReleaseCommand): F[Release] =
    command match
      case AddSong(_, releaseId, title) => addSong(releaseId, title)
      case ApproveReleaseDate(_, releaseId, date) => approveReleaseDate(releaseId, date)
      case SetReleaseDate(_, releaseId, date) => setReleaseDate(releaseId, date)
      case WithdrawRelease(_, releaseId) => withdraw(releaseId)

  private def addSong(id: ReleaseId, title: SongTitle): F[Release] =
    updateRelease(id, appendSong(_, title))

  private def approveReleaseDate(id: ReleaseId, date: LocalDate): F[Release] =
    updateRelease(id, updateState(_, Approved(date)))

  private def setReleaseDate(id: ReleaseId, date: LocalDate): F[Release] =
    updateRelease(id, updateState(_, Proposed(date)))

  private def withdraw(id: ReleaseId): F[Release] =
    updateRelease(id, updateState(_, Withdrawn))

  private def appendSong(release: Release, title: SongTitle): Release =
    val newId = if allSongs.isEmpty then 1L else allSongs.map(song => SongId.toLong(song.id)).max
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
  def apply[F[_] : Monad](): ReleaseRepository[F] = new FakeReleaseRepository[F]()

  val releases = mutable.HashMap(ReleaseId(1) -> Release(
    id = ReleaseId(1),
    artistId = ArtistId(321),
    recordLabelId = RecordLabelId(123),
    title = ReleaseTitle("Nice Album"),
    state = Created,
    songs = List.empty,
  ))

  def allSongs: List[Song] = releases.values.flatMap(_.songs).toList
