package distribution.domain

class ReleaseService[F[_]](private val repository: ReleaseRepository[F]):

  def addSong(command: AddSong): F[Release] =
    // 1. Don't allow if artist doesn't own release.
    //    This opens a huge separate topic about multitenancy and security.
    // 2. Check any validation requirements (title length?)
    repository.save(command)

  def setReleaseDate(command: SetReleaseDate): F[Release] =
    // 1. Don't allow if artist doesn't own release.
    // 2. Don't allow if release is Approved or Withdrawn
    // 3. Date cannot be in the past.
    //    It shouldn't be the same day either.
    repository.save(command)

  def approveReleaseDate(command: ApproveReleaseDate): F[Release] =
    // 1. Don't allow if the release is not under the record label.
    // 2. Don't allow if the release is Created, Approved or Withdrawn.
    // 3. Don't allow if the approved date doesn't match the proposed date.
    repository.save(command)
    
  def withdrawRelease(command: WithdrawRelease): F[Release] =
    // 1. Don't allow if artist doesn't own release.
    // 2. Don't allow if release is Created, Proposed or Withdrawn.
    repository.save(command)
