package distribution.infra

import cats.MonadThrow
import distribution.domain.{AddStream, ProcessingFailure, Stream, StreamCommand, StreamId, StreamRepository, StreamSequenceId}
import distribution.infra.FakeStreamRepository.streams

import scala.collection.mutable

private class FakeStreamRepository[F[_] : MonadThrow] extends StreamRepository[F]:
  override def save(command: StreamCommand): F[Stream] =
    command match
      case AddStream(songId, duration, startedAt) =>
        FakeReleaseRepository.allSongs.find(_.id == songId) match
          case Some(song) =>
            val nextId = streams.map(s => StreamId.toLong(s.id)).max
            val nextSeqId = streams.map(s => StreamSequenceId.toLong(s.sequenceId)).max
            val newStream = Stream(StreamId(nextId), StreamSequenceId(nextSeqId), song.id, duration, startedAt)
            streams.addOne(newStream)
            MonadThrow[F].pure(newStream)
         
          case None => MonadThrow[F].raiseError(ProcessingFailure())

object FakeStreamRepository:
  def apply[F[_] : MonadThrow]: StreamRepository[F] = new FakeStreamRepository[F]()

  val streams: mutable.ListBuffer[Stream] = mutable.ListBuffer()
