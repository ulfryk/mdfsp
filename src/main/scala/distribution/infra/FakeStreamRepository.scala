package distribution.infra

import cats.MonadThrow
import cats.data.NonEmptyList
import cats.syntax.all.*
import distribution.domain.StreamSequenceId.given_Ordering_StreamSequenceId.mkOrderingOps
import distribution.domain.{AddStream, ProcessingFailure, Song, SongId, SongReport, Stream, StreamCommand, StreamId, StreamRepository, StreamSequenceId}
import distribution.infra.FakeStreamRepository.streams
import organisation.domain.ArtistId

import scala.collection.mutable
import scala.concurrent.duration.{FiniteDuration, SECONDS}

private class FakeStreamRepository[F[_] : MonadThrow] extends StreamRepository[F]:

  def getLatest(artistId: ArtistId): F[Option[Stream]] =
    val artistSongIds = getArtistSongIs(artistId)
    // would be so much easier and cleaner with SQL…
    NonEmptyList.fromList(streams.toList).map(_.toList
        .filter(stream => artistSongIds.contains(stream.songId))
        .maxBy(s => StreamId.toLong(s.id)))
      .pure()

  def getMonetizedCountInRange(artistId: ArtistId, after: Option[StreamSequenceId], to: StreamSequenceId): F[Int] =
    val artistSongIds = getArtistSongIs(artistId)
    streams.count(stream =>
      stream.sequenceId > after.getOrElse(StreamSequenceId(0))
        && stream.sequenceId <= to
        && artistSongIds.contains(stream.songId)
        && isMonetized(stream)).pure

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

  def getSongsWithStreams(artistId: ArtistId): F[List[SongReport]] =
    val groupedCounts = streams.toList.groupMapReduce(_.songId)(stream =>
        if isMonetized(stream) then (1, 1) else (1, 0)
      ) {
        case ((t1, m1), (t2, m2)) => (t1 + t2, m1 + m2)
      }.withDefault(_ => (0, 0))
    getArtistSongs(artistId).map { song =>
      val counts = groupedCounts(song.id)
      SongReport(
        FakeReleaseRepository.releases(song.releaseId).title,
        song.title,
        counts._1,
        counts._2
      )
    }.pure

  /**
   * This is quite an important feature.
   * Should it happen on creation of stream or on payment request?
   * Can the `30s` value change over time?
   * How to sync change of this value with existing data?
   */
  private def isMonetized(stream: Stream): Boolean =
    stream.duration >= FiniteDuration(30, SECONDS)

  private def getArtistSongIs(artistId: ArtistId): Set[SongId] =
    getArtistSongs(artistId).map(_.id).toSet

  private def getArtistSongs(artistId: ArtistId): List[Song] =
    FakeReleaseRepository.releases.values.filter(_.artistId == artistId).flatMap(_.songs).toList

object FakeStreamRepository:
  def apply[F[_] : MonadThrow]: StreamRepository[F] = new FakeStreamRepository[F]()

  val streams: mutable.ListBuffer[Stream] = mutable.ListBuffer()
