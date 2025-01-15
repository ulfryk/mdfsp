import distribution.domain.{AddStream, ReleaseRepository, SongId, Stream, StreamId, StreamRepository, StreamSequenceId, StreamService}
import org.scalamock.stubs.{Stub, Stubs}

import java.time.Instant
import java.util.concurrent.TimeUnit
import scala.annotation.experimental
import scala.compiletime.uninitialized
import scala.concurrent.duration.FiniteDuration
import scala.util.Try

@experimental
class StreamServiceTest extends munit.FunSuite with Stubs:
  private var repo: Stub[StreamRepository[Try]] = uninitialized
  private var releaseRepo = stub[ReleaseRepository[Try]]
  private var service: StreamService[Try] = uninitialized

  private val default = Stream(
    id = StreamId(321),
    sequenceId = StreamSequenceId(876),
    songId = SongId(55),
    duration = FiniteDuration.apply(37, TimeUnit.SECONDS),
    startedAt = Instant.EPOCH,
  )

  override def beforeEach(context: BeforeEach): Unit =
    repo = stub[StreamRepository[Try]]
    releaseRepo = stub[ReleaseRepository[Try]]
    service = StreamService[Try](repo, releaseRepo)
    super.beforeEach(context)

  test("should add a stream"):
    val input = AddStream(SongId(55), FiniteDuration.apply(37, TimeUnit.SECONDS), Instant.EPOCH)
    repo.save.returns(_ => Try(default))

    val result = service.add(input)

    assertEquals(result.get, default)
    assertEquals(repo.save.times, 1)
    assertEquals(repo.save.calls(0), input)
