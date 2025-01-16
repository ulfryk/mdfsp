package distribution.domain

opaque type SongId = Long
object SongId:
  def apply(id: Long): SongId = id
  def toLong(id: SongId): Long = id

opaque type SongTitle = String
object SongTitle:
  def apply(title: String): SongTitle = title
  def asString(title: SongTitle): String = title

// opaque type FileId = Long

case class Song(
  id: SongId,
  releaseId: ReleaseId,
  title: SongTitle,
  // fileId: FileId
)
