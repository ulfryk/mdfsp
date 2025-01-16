import cats.Id
import distribution.domain.*
import distribution.domain.ReleaseState.Created
import org.scalamock.stubs.{Stub, Stubs}
import organisation.domain.{ArtistId, RecordLabelId}

import java.time.LocalDate
import scala.annotation.experimental
import scala.compiletime.uninitialized

@experimental
class ReleaseServiceTest extends munit.FunSuite with Stubs:
  private var repo: Stub[ReleaseRepository[Id]] = uninitialized
  private var service: ReleaseService[Id] = uninitialized

  private val default = Release(
    id = ReleaseId(1),
    artistId = ArtistId(321),
    recordLabelId = RecordLabelId(123),
    title = ReleaseTitle("Nice Album"),
    state = Created,
    songs = List.empty,
  )

  override def beforeEach(context: BeforeEach): Unit =
    repo = stub[ReleaseRepository[Id]]
    service = ReleaseService[Id](repo)
    super.beforeEach(context)

  test("should add a song"):
    val input = AddSong(ArtistId(321), ReleaseId(1), SongTitle("A first song!"))
    repo.save.returns(_ => Id(default))

    val result = service.addSong(input)

    assertEquals(result, default)
    assertEquals(repo.save.times, 1)
    assertEquals(repo.save.calls(0), input)

  test("should set release date"):
    val input = SetReleaseDate(ArtistId(321), ReleaseId(1), LocalDate.parse("2025-02-10"))
    repo.save.returns(_ => Id(default))

    val result = service.setReleaseDate(input)

    assertEquals(result, default)
    assertEquals(repo.save.times, 1)
    assertEquals(repo.save.calls(0), input)

  test("should approve release date"):
    val input = ApproveReleaseDate(RecordLabelId(123), ReleaseId(1), LocalDate.parse("2025-02-10"))
    repo.save.returns(_ => Id(default))

    val result = service.approveReleaseDate(input)

    assertEquals(result, default)
    assertEquals(repo.save.times, 1)
    assertEquals(repo.save.calls(0), input)

  test("should withdraw release"):
    val input = WithdrawRelease(ArtistId(321), ReleaseId(1))
    repo.save.returns(_ => Id(default))

    val result = service.withdrawRelease(input)

    assertEquals(result, default)
    assertEquals(repo.save.times, 1)
    assertEquals(repo.save.calls(0), input)
