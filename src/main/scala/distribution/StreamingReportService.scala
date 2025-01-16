package distribution

import cats.MonadThrow
import distribution.domain.{SongReport, StreamRepository}
import organisation.domain.ArtistId

class StreamingReportService[F[_] : MonadThrow](
  streamRepository: StreamRepository[F],
):

  def getReport(artistId: ArtistId): F[List[SongReport]] =
    // Not much happening here. But there may be some query validation in future… maybe.
    streamRepository.getSongsWithStreams(artistId)
    
