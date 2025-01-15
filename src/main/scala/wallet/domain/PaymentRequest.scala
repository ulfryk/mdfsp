package wallet.domain

import organisation.domain.ArtistId

import java.time.Instant

opaque type PaymentRequestId = Long
object PaymentRequestId:
  def apply(id: Long): PaymentRequestId = id

case class PaymentRequest(
  id: PaymentRequestId,
  artistId: ArtistId,
  requestedAt: Instant
)
