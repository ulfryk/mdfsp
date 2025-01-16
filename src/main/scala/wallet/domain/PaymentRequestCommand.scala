package wallet.domain

import distribution.domain.StreamSequenceId
import organisation.domain.ArtistId

sealed trait PaymentRequestCommand

// this one is not used by the repository
case class FilePaymentRequest(artistId: ArtistId) extends PaymentRequestCommand

// this one is used only by repository
case class FilePaymentRequestSince(artistId: ArtistId, lastStream: StreamSequenceId) extends PaymentRequestCommand
// they are not the same thing

// this one is not used by the repository
case class ComputePaymentRequest(id: PaymentRequestId) extends PaymentRequestCommand

// this one is used only by repository
case class SetPaymentRequestMonetization(id: PaymentRequestId, count: Int) extends PaymentRequestCommand
// they are not the same thing
