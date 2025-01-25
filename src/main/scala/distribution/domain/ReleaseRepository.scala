package distribution.domain

trait ReleaseRepository[F[_]]:
  def find(id: ReleaseId): F[Option[Release]]
  def get(id: ReleaseId): F[Release]
  def save(command: ReleaseCommand): F[Release]
  def listReleasedSongs(query: String): F[List[Song]]
