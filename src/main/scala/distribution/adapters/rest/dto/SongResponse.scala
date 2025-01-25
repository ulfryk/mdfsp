package distribution.adapters.rest.dto

import cats.syntax.all.*
import distribution.domain.{ReleaseId, Song, SongId, SongTitle}

final case class SongResponse(
  id: SongId,
  releaseId: ReleaseId,
  title: SongTitle,
)

object SongResponse:
  def apply(model: Song): SongResponse =
    new SongResponse(model.id, model.releaseId, model.title)