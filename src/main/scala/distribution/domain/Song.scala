package distribution.domain

opaque type SongId = Long
opaque type SongTitle = String
// opaque type FileId = Long

case class Song(
  id: SongId,
  releaseId: ReleaseId,
  title: SongTitle,
  // fileId: FileId
)
