package distribution.domain

import fs2.Stream as FStream
import organisation.domain.ArtistId

trait StreamRepository[F[_]]:
  def getLatest(artistId: ArtistId): F[Option[Stream]]
  def getMonetizedCountInRange(artistId: ArtistId, after: Option[StreamSequenceId], to: StreamSequenceId): F[Int]
  def save(command: StreamCommand): F[Stream]
  def getSongsWithStreams(artistId: ArtistId): F[List[SongReport]]
  def getAll(songId: SongId): FStream[F, Stream]
