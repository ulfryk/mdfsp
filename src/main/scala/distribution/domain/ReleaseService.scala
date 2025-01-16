package distribution.domain

import cats.MonadThrow
import cats.syntax.all.*
import distribution.domain.ReleaseState.*
import organisation.domain.{ArtistId, RecordLabelId}

import java.time.LocalDate

class ReleaseService[F[_] : MonadThrow](private val repository: ReleaseRepository[F]):

  def addSong(command: AddSong): F[Release] =
    // 1. Don't allow if artist doesn't own release.
    //    This opens a huge separate topic about multitenancy and security.
    // 2. Check any validation requirements (title length?)
    for {
      release <- repository.get(command.releaseId)
      _ <- validateArtist(release, command.artistId)
      _ <- validateState(release, state => state == Created || state.isInstanceOf[Proposed])
      updated <- repository.save(command)
    } yield updated

  def setReleaseDate(command: SetReleaseDate): F[Release] =
    for {
      release <- repository.get(command.releaseId)
      _ <- validateArtist(release, command.artistId)
      _ <- validateState(release, state => state == Created || state.isInstanceOf[Proposed])
      _ <- // I don't like it this way. For real life project I would prepare some abstraction over all validations.
        if command.date.isAfter(LocalDate.now()) then ().pure
        else MonadThrow[F].raiseError(ProcessingFailure())
      updated <- repository.save(command)
    } yield updated

  def approveReleaseDate(command: ApproveReleaseDate): F[Release] =
    for {
      release <- repository.get(command.releaseId)
      _ <- validateLabel(release, command.recordLabelId)
      _ <- validateState(release, _.isInstanceOf[Proposed])
      _ <- validateDate(release, _ == command.date)
      updated <- repository.save(command)
    } yield updated

  def withdrawRelease(command: WithdrawRelease): F[Release] =
    for {
      release <- repository.get(command.releaseId)
      _ <- validateArtist(release, command.artistId)
      _ <- validateState(release, _ == Distributed)
      updated <- repository.save(command)
    } yield updated

  def distributeRelease(command: DistributeRelease): F[Release] =
    for {
      release <- repository.get(command.releaseId)
      _ <- validateArtist(release, command.artistId)
      _ <- validateState(release, _.isInstanceOf[Approved])
      updated <- repository.save(command)
    } yield updated

  private def validateDate(release: Release, predicate: LocalDate => Boolean): F[Release] =
    val theDate = release.state match
      case Created | Distributed | Withdrawn => MonadThrow[F].raiseError(ProcessingFailure())
      case Proposed(date) => date.pure
      case Approved(date) => date.pure
    theDate.flatMap { date =>
      if predicate(date) then release.pure
      else MonadThrow[F].raiseError(ProcessingFailure())
    }

  private def validateState(release: Release, predicate: ReleaseState => Boolean): F[Release] =
    if predicate(release.state) then release.pure
    else MonadThrow[F].raiseError(ProcessingFailure())

  private def validateArtist(release: Release, artistId: ArtistId): F[Release] =
    if release.artistId == artistId then release.pure
    else MonadThrow[F].raiseError(ProcessingFailure())

  private def validateLabel(release: Release, labelId: RecordLabelId): F[Release] =
    if release.recordLabelId == labelId then release.pure
    else MonadThrow[F].raiseError(ProcessingFailure())
