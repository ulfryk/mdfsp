package wallet.domain

import organisation.domain.ArtistId

final case class PaymentRequestAndPrevious(current: PaymentRequest, prev: Option[PaymentRequest])

trait PaymentRequestRepository[F[_]]:
  def getLatest(artistId: ArtistId): F[Option[PaymentRequest]]
  def getAndPrevious(id: PaymentRequestId): F[PaymentRequestAndPrevious]
  def save(command: PaymentRequestCommand): F[PaymentRequest]
