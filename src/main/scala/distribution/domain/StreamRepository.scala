package distribution.domain

trait StreamRepository[F[_]]:
  def save(command: StreamCommand): F[Stream]
