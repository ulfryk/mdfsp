package distribution.domain

import organisation.domain.{ArtistId, RecordLabelId}

import java.time.LocalDate

opaque type ReleaseId = Long
opaque type ReleaseTitle = String

enum ReleaseState:
  case Created
  case Proposed(date: LocalDate)
  case Approved(date: LocalDate)
  case Withdrawn

case class Release(
  id: ReleaseId,
  artistId: ArtistId,
  recordLabelId: RecordLabelId,
  title: ReleaseTitle,
  state: ReleaseState
)
