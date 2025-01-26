package distribution.infra.api.dto

import distribution.domain.{Release, ReleaseId, ReleaseState, ReleaseTitle, Song}
import organisation.domain.{ArtistId, RecordLabelId}

case class ReleaseResponse(
  id: ReleaseId,
  artistId: ArtistId,
  recordLabelId: RecordLabelId,
  title: ReleaseTitle,
  state: ReleaseState,
  songs: List[SongResponse],
)

object ReleaseResponse:
  def apply(model: Release): ReleaseResponse = new ReleaseResponse(
    model.id, model.artistId, model.recordLabelId, model.title, model.state, model.songs.map(SongResponse.apply)
  )
