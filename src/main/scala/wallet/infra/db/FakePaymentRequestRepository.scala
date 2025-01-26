package wallet.infra.db

import cats.MonadThrow
import cats.data.NonEmptyList
import cats.syntax.all.*
import distribution.domain.StreamSequenceId.given_Ordering_StreamSequenceId.mkOrderingOps
import distribution.domain.{ProcessingFailure, StreamSequenceId}
import organisation.domain.ArtistId
import wallet.domain.*
import FakePaymentRequestRepository.paymentRequests

import java.time.Instant
import scala.collection.mutable

private class FakePaymentRequestRepository[F[_] : MonadThrow] extends PaymentRequestRepository[F]:
  def getLatest(artistId: ArtistId): F[Option[PaymentRequest]] =
    NonEmptyList.fromList(paymentRequests.toList)
      .map(_.toList.maxBy(_.lastStream))
      .pure()

  def getAndPrevious(id: PaymentRequestId): F[PaymentRequestAndPrevious] =
    paymentRequests.find(_.id == id) match
      case None => MonadThrow[F].raiseError(ProcessingFailure())
      case Some(request) =>
        PaymentRequestAndPrevious(
          request,
          paymentRequests.findLast { other =>
            other.artistId == request.artistId && other.lastStream < request.lastStream
          }).pure

  def save(command: PaymentRequestCommand): F[PaymentRequest] =
    command match
      case FilePaymentRequestSince(artistId, lastStream) =>
        val nextId =
          if paymentRequests.isEmpty then 1L
          else paymentRequests.toList.map(req => PaymentRequestId.toLong(req.id)).max + 1
        val newPaymentRequest = PaymentRequest(PaymentRequestId(nextId), artistId, lastStream, Instant.now(), None)
        paymentRequests.addOne(newPaymentRequest)
        newPaymentRequest.pure()

      case SetPaymentRequestMonetization(id, count) =>
        paymentRequests.find(_.id == id) match
          case None => MonadThrow[F].raiseError(ProcessingFailure())
          case Some(request) =>
            val updated = request.copy(monetizedStreamsCount = count.some)
            paymentRequests.addOne(updated)
            updated.pure()

      // Ugly inconsistency, this shouldn't leak here
      case FilePaymentRequest(_) | ComputePaymentRequest(_) => MonadThrow[F].raiseError(ProcessingFailure())

object FakePaymentRequestRepository:
  def apply[F[_] : MonadThrow]: PaymentRequestRepository[F] = new FakePaymentRequestRepository[F]()

  val paymentRequests: mutable.ListBuffer[PaymentRequest] = mutable.ListBuffer()
