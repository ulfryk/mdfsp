import distribution.domain.*
import distribution.domain.ReleaseState.*
import org.scalamock.stubs.{Stub, Stubs}
import organisation.domain.{ArtistId, RecordLabelId}

import java.time.LocalDate
import scala.annotation.experimental
import scala.compiletime.uninitialized
import scala.util.Try

@experimental
class ReleaseServiceTest extends munit.FunSuite with Stubs:
  private var repo: Stub[ReleaseRepository[Try]] = uninitialized
  private var service: ReleaseService[Try] = uninitialized

  private val default = Release(
    id = ReleaseId(1),
    artistId = ArtistId(321),
    recordLabelId = RecordLabelId(123),
    title = ReleaseTitle("Nice Album"),
    state = Created,
    songs = List.empty,
  )

  override def beforeEach(context: BeforeEach): Unit =
    repo = stub[ReleaseRepository[Try]]
    service = ReleaseService[Try](repo)
    super.beforeEach(context)

  test("should add a song"):
    val input = AddSong(ArtistId(321), ReleaseId(1), SongTitle("A first song!"))
    repo.get.returns(_ => Try(default))
    repo.save.returns(_ => Try(default))

    val result = service.addSong(input)

    assertEquals(result.get, default)
    assertEquals(repo.save.times, 1)
    assertEquals(repo.save.calls(0), input)

  test("should set release date"):
    val input = SetReleaseDate(ArtistId(321), ReleaseId(1), LocalDate.parse("2025-02-10"))
    repo.get.returns(_ => Try(default))
    repo.save.returns(_ => Try(default))

    val result = service.setReleaseDate(input)

    assertEquals(result.get, default)
    assertEquals(repo.save.times, 1)
    assertEquals(repo.save.calls(0), input)

  test("should approve release date"):
    val existing = default.copy(state = Proposed(LocalDate.parse("2025-02-10")))
    val updated = default.copy(state = Approved(LocalDate.parse("2025-02-10")))
    val input = ApproveReleaseDate(RecordLabelId(123), ReleaseId(1), LocalDate.parse("2025-02-10"))
    repo.get.returns(_ => Try(existing))
    repo.save.returns(_ => Try(updated))

    val result = service.approveReleaseDate(input)

    assertEquals(result.get, updated)
    assertEquals(repo.save.times, 1)
    assertEquals(repo.save.calls(0), input)

  test("should fail to approve if different date is provided"):
    val existing = default.copy(state = Proposed(LocalDate.parse("2025-01-01")))
    val input = ApproveReleaseDate(RecordLabelId(123), ReleaseId(1), LocalDate.parse("2025-02-10"))
    repo.get.returns(_ => Try(existing))

    val result = service.approveReleaseDate(input)

    assert(result.failed.get.isInstanceOf[ProcessingFailure])
    assertEquals(repo.save.times, 0)

  test("should set release for distribution"):
    val existing = default.copy(state = Approved(LocalDate.parse("2025-01-17")))
    val updated = default.copy(state = Distributed)
    val input = DistributeRelease(ArtistId(321), ReleaseId(1))
    repo.get.returns(_ => Try(existing))
    repo.save.returns(_ => Try(updated))

    val result = service.distributeRelease(input)

    assertEquals(result.get, updated)
    assertEquals(repo.save.times, 1)
    assertEquals(repo.save.calls(0), input)

  test("should fail to set release for distribution if it's not approved"):
    val existing = default.copy(state = Created)
    val input = DistributeRelease(ArtistId(321), ReleaseId(1))
    repo.get.returns(_ => Try(existing))

    val result = service.distributeRelease(input)

    assert(result.failed.get.isInstanceOf[ProcessingFailure])
    assertEquals(repo.save.times, 0)

  test("should withdraw release"):
    val existing = default.copy(state = Distributed)
    val updated = default.copy(state = Withdrawn)
    val input = WithdrawRelease(ArtistId(321), ReleaseId(1))
    repo.get.returns(_ => Try(existing))
    repo.save.returns(_ => Try(updated))

    val result = service.withdrawRelease(input)

    assertEquals(result.get, updated)
    assertEquals(repo.save.times, 1)
    assertEquals(repo.save.calls(0), input)
