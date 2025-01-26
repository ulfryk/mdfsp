package distribution

import cats.MonadThrow
import cats.effect.Concurrent
import distribution.domain.{ReleaseRepository, SongReport, StreamRepository}
import fs2.Stream as FStream
import organisation.domain.ArtistId

class StreamingReportService[F[_] : MonadThrow](
  releaseRepository: ReleaseRepository[F],
  streamRepository: StreamRepository[F],
):

  def getReport(artistId: ArtistId): F[List[SongReport]] =
    // Not much happening here. But there may be some query validation in future… maybe.
    streamRepository.getSongsWithStreams(artistId)

  def getReport2(artistId: ArtistId)(using Concurrent[F]): FStream[F, SongReport] =
    releaseRepository.getAllSongsWithReleaseTitle(artistId)
      .map { case inp @ (_, song) =>
        streamRepository.getAll(song.id)
          .scan(SongReport(inp))(_.appendStream(_))
          .lastOr(SongReport(inp))
      }
      .parJoinUnbounded
