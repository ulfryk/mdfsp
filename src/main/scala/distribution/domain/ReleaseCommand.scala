package distribution.domain

import distribution.domain.{ReleaseId, SongTitle}
import organisation.domain.{ArtistId, RecordLabelId}

import java.time.LocalDate

sealed trait ReleaseCommand

case class AddSong(
  artistId: ArtistId,
  releaseId: ReleaseId,
  title: SongTitle,
  // fileId: FileId
) extends ReleaseCommand

case class ApproveReleaseDate(
  recordLabelId: RecordLabelId,
  releaseId: ReleaseId,
  date: LocalDate, // It's here to avoid "read skew"
) extends ReleaseCommand

case class SetReleaseDate(
  artistId: ArtistId,
  releaseId: ReleaseId,
  date: LocalDate,
) extends ReleaseCommand

case class WithdrawRelease(
  artistId: ArtistId,
  releaseId: ReleaseId,
) extends ReleaseCommand
