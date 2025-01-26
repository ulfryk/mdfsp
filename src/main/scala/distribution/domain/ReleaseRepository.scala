package distribution.domain

import fs2.Stream as FStream
import organisation.domain.ArtistId

trait ReleaseRepository[F[_]]:
  def find(id: ReleaseId): F[Option[Release]]
  def get(id: ReleaseId): F[Release]
  def save(command: ReleaseCommand): F[Release]
  def listReleasedSongs(query: Option[String]): F[List[Song]]
  def getAllSongsWithReleaseTitle(artistId: ArtistId): FStream[F, (ReleaseTitle, Song)]
