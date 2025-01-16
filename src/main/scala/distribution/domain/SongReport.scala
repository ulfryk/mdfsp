package distribution.domain

// Not exactly a domain model in terms of DDD
final case class SongReport(
  release: ReleaseTitle,
  title: SongTitle,
  // should we wrap it in value objects (opaque type)? I'm voting _Yay_, but that may be a little overkill in this case.
  totalStreams: Int,
  monetizedStreams: Int
)
