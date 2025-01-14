package wallet.domain

import organisation.domain.ArtistId

import java.time.Instant

opaque type PaymentRequestId = Long

case class PaymentRequest(
  id: PaymentRequestId,
  artistId: ArtistId,
  requestedAt: Instant
)
