package distribution.domain

class StreamService[F[_]](
  private val repository: StreamRepository[F],
  private val releaseRepository: ReleaseRepository[F],
):

  def add(command: AddStream): F[Stream] =
    // 1. can we anyhow check for duplicates?
    // 2. confirm that song exists
    //    releaseRepository.getSong(command.songId)
    // 3. sequence id should be derived on DB level IMO
    repository.save(command)
