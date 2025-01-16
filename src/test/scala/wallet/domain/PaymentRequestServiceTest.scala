package wallet.domain

import cats.implicits.given
import cats.syntax.all.*
import distribution.domain.{ProcessingFailure, SongId, Stream, StreamId, StreamRepository, StreamSequenceId}
import org.scalamock.stubs.{Stub, Stubs}
import organisation.domain.ArtistId
import wallet.domain.*

import java.time.Instant
import scala.annotation.experimental
import scala.compiletime.uninitialized
import scala.concurrent.duration.{FiniteDuration, SECONDS}
import scala.util.{Failure, Try}

@experimental
class PaymentRequestServiceTest extends munit.FunSuite with Stubs:
  private var repo: Stub[PaymentRequestRepository[Try]] = uninitialized
  private var streamRepo: Stub[StreamRepository[Try]] = uninitialized
  private var service: PaymentRequestService[Try] = uninitialized

  override def beforeEach(context: BeforeEach): Unit =
    repo = stub[PaymentRequestRepository[Try]]
    streamRepo = stub[StreamRepository[Try]]
    service = PaymentRequestService[Try](repo, streamRepo)
    super.beforeEach(context)

  test("should file first payment request"):
    val artistId = ArtistId(1)
    val lastStreamSeqId = StreamSequenceId(135)
    val input = FilePaymentRequest(artistId)
    val latestStream = Stream(StreamId(123), lastStreamSeqId, SongId(2), FiniteDuration(45, SECONDS), Instant.EPOCH)
    val created = PaymentRequest(PaymentRequestId(1), artistId, lastStreamSeqId, Instant.now(), None)
    repo.getLatest.returns(_ => Try(None))
    streamRepo.getLatest.returns(_ => Try(latestStream.some))
    repo.save.returns(_ => Try(created))

    val result = service.filePaymentRequest(input)

    assertEquals(result.get, created)
    assertEquals(repo.save.times, 1)
    assertEquals(repo.save.calls(0), FilePaymentRequestSince(artistId, lastStreamSeqId))

  test("should file new payment request"):
    val artistId = ArtistId(1)
    val lastStreamSeqId = StreamSequenceId(135)
    val input = FilePaymentRequest(artistId)
    val latestStream = Stream(StreamId(123), lastStreamSeqId, SongId(2), FiniteDuration(45, SECONDS), Instant.EPOCH)
    val previous = PaymentRequest(PaymentRequestId(1), artistId, StreamSequenceId(2), Instant.EPOCH, 12.some)
    val created = PaymentRequest(PaymentRequestId(2), artistId, lastStreamSeqId, Instant.now(), None)
    repo.getLatest.returns(_ => Try(previous.some))
    streamRepo.getLatest.returns(_ => Try(latestStream.some))

    repo.save.returns(_ => Try(created))

    val result = service.filePaymentRequest(input)
    assertEquals(result.get, created)
    assertEquals(repo.save.times, 1)
    assertEquals(repo.save.calls(0), FilePaymentRequestSince(artistId, lastStreamSeqId))

  test("should fail to file a payment request if there is no valid stream"):
    val artistId = ArtistId(1)
    val input = FilePaymentRequest(artistId)
    repo.getLatest.returns(_ => Try(None))
    streamRepo.getLatest.returns(_ => Try(None))

    val result = service.filePaymentRequest(input)

    assert(result.isInstanceOf[Failure[PaymentRequest]])
    assert(result.failed.get.isInstanceOf[ProcessingFailure])
    assertEquals(repo.save.times, 0)

  test("should fail to file a payment request if no new streams since last payment"):
    val artistId = ArtistId(1)
    val lastStreamSeqId = StreamSequenceId(135)
    val input = FilePaymentRequest(artistId)
    val latestStream = Stream(StreamId(123), lastStreamSeqId, SongId(2), FiniteDuration(45, SECONDS), Instant.EPOCH)
    val previous = PaymentRequest(PaymentRequestId(1), artistId, lastStreamSeqId, Instant.EPOCH, 12.some)
    repo.getLatest.returns(_ => Try(previous.some))
    streamRepo.getLatest.returns(_ => Try(latestStream.some))

    val result = service.filePaymentRequest(input)

    assert(result.isInstanceOf[Failure[PaymentRequest]])
    assert(result.failed.get.isInstanceOf[ProcessingFailure])
    assertEquals(repo.save.times, 0)

  test("should compute monetization for payment request"):
    val paymentRequestId = PaymentRequestId(123)
    val artistId = ArtistId(1)
    val lastStreamSeqId = StreamSequenceId(135)
    val prevLastStreamSeqId = StreamSequenceId(13)
    val input = ComputePaymentRequest(paymentRequestId)
    val current = PaymentRequest(paymentRequestId, artistId, lastStreamSeqId, Instant.now(), None)
    val previous = PaymentRequest(PaymentRequestId(1), artistId, prevLastStreamSeqId, Instant.EPOCH, 12.some)
    val updated = current.copy(monetizedStreamsCount = 500.some)
    repo.getAndPrevious.returns(_ => Try(PaymentRequestAndPrevious(current, previous.some)))
    streamRepo.getMonetizedCountInRange.returns(_ => Try(500))
    repo.save.returns(_ => Try(updated))

    val result = service.computePaymentRequest(input)

    assertEquals(result.get, updated)
    assertEquals(repo.save.times, 1)
    assertEquals(repo.save.calls(0), SetPaymentRequestMonetization(paymentRequestId, 500))
