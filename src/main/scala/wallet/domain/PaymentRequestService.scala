package wallet.domain

import cats.syntax.all.*
import cats.{MonadThrow, Monoid}
import distribution.domain.StreamSequenceId.given_Ordering_StreamSequenceId.mkOrderingOps
import distribution.domain.{ProcessingFailure, Stream, StreamRepository, StreamSequenceId}

class PaymentRequestService[F[_] : MonadThrow](
  private val repository: PaymentRequestRepository[F],
  private val streamRepository: StreamRepository[F],
):

  def filePaymentRequest(command: FilePaymentRequest): F[PaymentRequest] =
    for {
      lastStream <- streamRepository.getLatest(command.artistId)
      previousPayment <- repository.getLatest(command.artistId)
      latest <- getValidLatestSeqId(lastStream, previousPayment)
      newPaymentRequest <- repository.save(commandToCreate(command, latest))
    } yield newPaymentRequest

  def computePaymentRequest(command: ComputePaymentRequest): F[PaymentRequest] =
    for {
      withPrev <- repository.getAndPrevious(command.id)
      count <- streamRepository.getMonetizedCountInRange(
        withPrev.current.artistId, withPrev.prev.map(_.lastStream), withPrev.current.lastStream)
      updated <- repository.save(SetPaymentRequestMonetization(command.id, count))
    } yield updated

  private def getValidLatestSeqId(
    lastStream: Option[Stream],
    previousPayment: Option[PaymentRequest],
  ): F[Option[StreamSequenceId]] =
    lastStream match
      case None => MonadThrow[F].raiseError(ProcessingFailure())
      case Some(stream) => previousPayment match
        case None => stream.sequenceId.some.pure()
        case Some(prev) =>
          if prev.lastStream >= stream.sequenceId then MonadThrow[F].raiseError(ProcessingFailure())
          else stream.sequenceId.some.pure()

  private def commandToCreate(command: FilePaymentRequest, latest: Option[StreamSequenceId]): PaymentRequestCommand =
    val lastSeqId = latest.getOrElse(StreamSequenceId(Monoid[Long].empty))
    FilePaymentRequestSince(command.artistId, lastSeqId)
