import cats.implicits.given
import distribution.domain.*
import distribution.domain.ReleaseState.*
import organisation.domain.{ArtistId, RecordLabelId}
import wallet.domain.{ComputePaymentRequest, FilePaymentRequest, PaymentRequest}

import java.time.{Instant, LocalDate}
import scala.concurrent.duration.{FiniteDuration, SECONDS}
import scala.util.Try

// Testing it as a single End-To-End scenario.
// For a proper integration test, it should be split into separate steps.
// Each step should have state prepared and then checked afterwards.
// But to test if we can make a demo with MVP, this should be enough :)
class HappyPathFlowTest extends munit.FunSuite:
  private val context = AppDI[Try]
  private val theReleaseId = ReleaseId(1)
  private val theArtistId = ArtistId(321)
  private val theRecordLabelId = RecordLabelId(123)

  // Quite a funny way to approach munit.
  // Test are run in order (ListBuffer is used in the implementation).
  // Putting them inside assures that no one runs a single one separately.
  runScenario()

  private def runScenario(): Unit =
    test("1. A song was added to the release by an artist"):
      val songsWhereAdded = for {
        _ <- addSong("First Song") // id 1
        _ <- addSong("Second Song") // id 2
        _ <- addSong("Third Song") // id 3
        withFourSongs <- addSong("Fourth Song") // id 4
      } yield withFourSongs

      assert(songsWhereAdded.isSuccess)
      assertEquals(songsWhereAdded.get.songs.size, 4)
      assertEquals(songsWhereAdded.get.state, Created)

    test("2. A release date was proposed by artist"):
      val dateWasSet = setDate("2025-02-11")

      assert(dateWasSet.isSuccess)
      assertEquals(dateWasSet.get.state, Proposed(LocalDate.parse("2025-02-11")))

    test("3. The proposed date was approved by the record label"):
      val dateWasApproved = approve("2025-02-11")

      assert(dateWasApproved.isSuccess)
      assertEquals(dateWasApproved.get.state, Approved(LocalDate.parse("2025-02-11")))

    test("4. Songs where distributed for streaming"):
      val releaseWasSetForDistribution = distribute()

      assert(releaseWasSetForDistribution.isSuccess)
      assertEquals(releaseWasSetForDistribution.get.state, Distributed)

    test("5. Released songs where searched by title using Levenshtein distance algorithm."):
      val songs = context.releaseService.listReleasedSongs("third")
      assert(songs.isSuccess)
      assertEquals(songs.get.get(0).get.title, SongTitle("Third Song"))
      assertEquals(songs.get.get(1).get.title, SongTitle("First Song"))
      assertEquals(songs.get.get(2).get.title, SongTitle("Fourth Song"))
      assertEquals(songs.get.get(3).get.title, SongTitle("Second Song"))

    test("6. New stream was created"):
      val streamsCreated = for {
        _ <- createStream(1, 20)
        _ <- createStream(1, 10)
        _ <- createStream(2, 20)
        _ <- createStream(2, 40)
        _ <- createStream(3, 31)
        latest <- createStream(3, 121)
      } yield latest

      assert(streamsCreated.isSuccess)
      assertEquals(streamsCreated.get.sequenceId, StreamSequenceId(6))

    test("7. Artist requested and received a report of streamed songs (both monetized and not)"):
      val report = context.streamingReportService.getReport(theArtistId)

      assert(report.isSuccess)

      val groupedReport = report.get.map { x => (x.title, x) }.toMap

      assertEquals(groupedReport(SongTitle("First Song")).totalStreams, 2)
      assertEquals(groupedReport(SongTitle("First Song")).monetizedStreams, 0)
      assertEquals(groupedReport(SongTitle("Second Song")).totalStreams, 2)
      assertEquals(groupedReport(SongTitle("Second Song")).monetizedStreams, 1)
      assertEquals(groupedReport(SongTitle("Third Song")).totalStreams, 2)
      assertEquals(groupedReport(SongTitle("Third Song")).monetizedStreams, 2)
      assertEquals(groupedReport(SongTitle("Fourth Song")).totalStreams, 0)
      assertEquals(groupedReport(SongTitle("Fourth Song")).monetizedStreams, 0)

    test("8. Payment request was created"):
      val paymentRequestCreated = createPaymentRequest()

      assert(paymentRequestCreated.isSuccess)
      assertEquals(paymentRequestCreated.get.monetizedStreamsCount.get, 3)

    test("9. Release and its songs were withdrawn from distribution"):
      val releaseWithdrawn = withdraw()

      assert(releaseWithdrawn.isSuccess)
      assertEquals(releaseWithdrawn.get.state, Withdrawn)

  end runScenario

  private def addSong(title: String): Try[Release] =
    context.releaseService.addSong(AddSong(theArtistId, theReleaseId, SongTitle(title)))

  private def setDate(date: String): Try[Release] =
    context.releaseService.setReleaseDate(SetReleaseDate(theArtistId, theReleaseId, LocalDate.parse(date)))

  private def approve(date: String): Try[Release] =
    context.releaseService.approveReleaseDate(ApproveReleaseDate(theRecordLabelId, theReleaseId, LocalDate.parse(date)))

  private def withdraw(): Try[Release] =
    context.releaseService.withdrawRelease(WithdrawRelease(theArtistId, theReleaseId))

  private def distribute(): Try[Release] =
    context.releaseService.distributeRelease(DistributeRelease(theArtistId, theReleaseId))

  private def createStream(song: Long, duration: Int): Try[Stream] =
    context.streamService.add(
      AddStream(SongId(song), FiniteDuration(duration.toLong, SECONDS), Instant.now().minusSeconds(3601)))

  private def createPaymentRequest(): Try[PaymentRequest] =
    for {
      initialized <- context.paymentRequestService.filePaymentRequest(FilePaymentRequest(theArtistId))
      ready <- context.paymentRequestService.computePaymentRequest(ComputePaymentRequest(initialized.id))
    } yield ready
