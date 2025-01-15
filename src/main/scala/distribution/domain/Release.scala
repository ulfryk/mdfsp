package distribution.domain

import organisation.domain.{ArtistId, RecordLabelId}

import java.time.LocalDate

opaque type ReleaseId = Long
object ReleaseId:
  def apply(id: Long): ReleaseId = id
  
opaque type ReleaseTitle = String
object ReleaseTitle:
  def apply(title: String): ReleaseTitle = title

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
  state: ReleaseState,
  songs: List[Song],
)
