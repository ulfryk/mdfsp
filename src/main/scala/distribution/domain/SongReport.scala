package distribution.domain

// Not exactly a domain model in terms of DDD
final case class SongReport(
  release: ReleaseTitle,
  title: SongTitle,
  // should we wrap it in value objects (opaque type)? I'm voting _Yay_, but that may be a little overkill in this case.
  totalStreams: Int,
  monetizedStreams: Int
):
  def appendStream(stream: Stream): SongReport = copy(
    totalStreams = totalStreams + 1,
    monetizedStreams = monetizedStreams + (if stream.isMonetized() then 1 else 0),
  )

object SongReport:
  def apply(input: (ReleaseTitle, Song)): SongReport =
    new SongReport(input._1, input._2.title, 0, 0)
