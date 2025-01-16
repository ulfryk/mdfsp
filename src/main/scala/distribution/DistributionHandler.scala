package distribution

import cats.MonadThrow
import cats.syntax.all.*
import distribution.domain.{ReleaseDistributed, ReleaseEvent}

class DistributionHandler[F[_] : MonadThrow]:
  def handle(event: ReleaseEvent): F[Unit] =
    event match
      case ReleaseDistributed =>
        // 1. get all songs from release
        // 2. upload them to streaming platforms (those configured for the artist?)
        // 3. most probably, this handler can forward separate commands for separate integrations
        ().pure
