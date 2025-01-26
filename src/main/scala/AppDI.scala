import cats.MonadThrow
import distribution.domain.{ReleaseRepository, ReleaseService, StreamRepository, StreamService}
import distribution.infra.db.{FakeReleaseRepository, FakeStreamRepository}
import distribution.{DistributionHandler, StreamingReportService}
import wallet.domain.{PaymentRequestRepository, PaymentRequestService}
import wallet.infra.db.FakePaymentRequestRepository

case class AppDI[F[_] : MonadThrow](
  releaseRepository: ReleaseRepository[F],
  releaseService: ReleaseService[F],
  streamRepository: StreamRepository[F],
  streamService: StreamService[F],
  streamingReportService: StreamingReportService[F],
  distributionHandler: DistributionHandler[F],
  paymentRequestRepository: PaymentRequestRepository[F],
  paymentRequestService: PaymentRequestService[F],
):
  override def toString: String = "AppDI: I'm pretending to be DI"

object AppDI:
  def apply[F[_] : MonadThrow]: AppDI[F] =
    val releaseRepo = FakeReleaseRepository[F]
    val streamRepo = FakeStreamRepository[F]
    val paymentReqRepo = FakePaymentRequestRepository[F]
    new AppDI[F](
      releaseRepo,
      ReleaseService(releaseRepo),
      streamRepo,
      StreamService(streamRepo, releaseRepo),
      StreamingReportService(releaseRepo, streamRepo),
      DistributionHandler(),
      paymentReqRepo,
      PaymentRequestService(paymentReqRepo, streamRepo),
    )
