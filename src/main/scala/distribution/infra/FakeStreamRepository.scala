package distribution.infra

import cats.MonadThrow
import cats.data.NonEmptyList
import cats.syntax.all.*
import distribution.domain.StreamSequenceId.given_Ordering_StreamSequenceId.mkOrderingOps
import distribution.domain.{AddStream, ProcessingFailure, SongId, Stream, StreamCommand, StreamId, StreamRepository, StreamSequenceId}
import distribution.infra.FakeStreamRepository.streams
import organisation.domain.ArtistId

import scala.collection.mutable
import scala.concurrent.duration.{FiniteDuration, SECONDS}

private class FakeStreamRepository[F[_] : MonadThrow] extends StreamRepository[F]:

  def getLatest(artistId: ArtistId): F[Option[Stream]] =
    val artistSongIds = getArtistSongs(artistId)
    // would be so much easier and cleaner with SQL…
    NonEmptyList.fromList(streams.toList).map(_.toList
        .filter(stream => artistSongIds.contains(stream.songId))
        .maxBy(s => StreamId.toLong(s.id)))
      .pure()

  def getMonetizedCountInRange(artistId: ArtistId, after: Option[StreamSequenceId], to: StreamSequenceId): F[Int] =
    val artistSongIds = getArtistSongs(artistId)
    streams.count(stream =>
      stream.sequenceId > after.getOrElse(StreamSequenceId(0))
        && stream.sequenceId <= to
        && artistSongIds.contains(stream.songId)
        && stream.duration >= FiniteDuration(30, SECONDS)).pure

  def save(command: StreamCommand): F[Stream] =
    command match
      case AddStream(songId, duration, startedAt) =>
        FakeReleaseRepository.allSongs.find(_.id == songId) match
          case Some(song) =>
            val nextId = if streams.isEmpty then 1L else streams.map(s => StreamId.toLong(s.id)).max + 1
            val nextSeqId =
              if streams.isEmpty then 1L
              else streams.map(s => StreamSequenceId.toLong(s.sequenceId)).max + 1
            val newStream = Stream(StreamId(nextId), StreamSequenceId(nextSeqId), song.id, duration, startedAt)
            streams.addOne(newStream)
            MonadThrow[F].pure(newStream)

          case None => MonadThrow[F].raiseError(ProcessingFailure())

  private def getArtistSongs(artistId: ArtistId): Set[SongId] =
    FakeReleaseRepository.releases.values
      .filter(_.artistId == artistId).flatMap(_.songs).map(_.id).toSet

object FakeStreamRepository:
  def apply[F[_] : MonadThrow]: StreamRepository[F] = new FakeStreamRepository[F]()

  val streams: mutable.ListBuffer[Stream] = mutable.ListBuffer()
