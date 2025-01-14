package distribution.domain

import organisation.domain.{ArtistId, RecordLabelId}

import java.time.LocalDate

opaque type ReleaseId = Long
opaque type ReleaseTitle = String

enum ReleaseState:
  case Created
  case Proposed
  case Approved
  case Withdrawn

case class Release(
  id: ReleaseId,
  artistId: ArtistId,
  recordLabelId: RecordLabelId,
  title: ReleaseTitle,
  releaseDate: Option[LocalDate], // FIXME: Smell
  state: ReleaseState
)
