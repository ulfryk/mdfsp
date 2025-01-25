package distribution.domain

import cats.syntax.all.*

opaque type SongId = Long
object SongId:
  def apply(id: Long): SongId = id
  def unapply(id: SongId): Option[Long] = id.some
  def toLong(id: SongId): Long = id

opaque type SongTitle = String
object SongTitle:
  def apply(title: String): SongTitle = title
  def unapply(title: SongTitle): Option[String] = title.some
  def asString(title: SongTitle): String = title

// opaque type FileId = Long

case class Song(
  id: SongId,
  releaseId: ReleaseId,
  title: SongTitle,
  // fileId: FileId
)
