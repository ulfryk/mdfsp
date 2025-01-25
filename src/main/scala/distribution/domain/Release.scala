package distribution.domain

import cats.syntax.all.*
import organisation.domain.{ArtistId, RecordLabelId}

import java.time.LocalDate

opaque type ReleaseId = Long
object ReleaseId:
  def apply(id: Long): ReleaseId = id
  def unapply(id: ReleaseId): Option[Long] = id.some
  
opaque type ReleaseTitle = String
object ReleaseTitle:
  def apply(title: String): ReleaseTitle = title
  def unapply(title: ReleaseTitle): Option[String] = title.some

enum ReleaseState:
  case Created
  case Proposed(date: LocalDate)
  case Approved(date: LocalDate)
  case Distributed
  case Withdrawn

case class Release(
  id: ReleaseId,
  artistId: ArtistId,
  recordLabelId: RecordLabelId,
  title: ReleaseTitle,
  state: ReleaseState,
  songs: List[Song],
)
