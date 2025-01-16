package distribution.domain

import organisation.domain.ArtistId

trait StreamRepository[F[_]]:
  def getLatest(artistId: ArtistId): F[Option[Stream]]
  def getMonetizedCountInRange(artistId: ArtistId, after: Option[StreamSequenceId], to: StreamSequenceId): F[Int]
  def save(command: StreamCommand): F[Stream]
