package wallet.domain

import distribution.domain.StreamSequenceId
import organisation.domain.ArtistId

import java.time.Instant

opaque type PaymentRequestId = Long

object PaymentRequestId:
  def apply(id: Long): PaymentRequestId = id
  def toLong(id: PaymentRequestId): Long = id

case class PaymentRequest(
  id: PaymentRequestId,
  artistId: ArtistId,
  lastStream: StreamSequenceId,
  requestedAt: Instant,
  // To value-object or not to value-object… Flexibility vs consistency…
  // Is Int enough? Or should we use Long? Domain expert help needed!
  monetizedStreamsCount: Option[Int],
)
