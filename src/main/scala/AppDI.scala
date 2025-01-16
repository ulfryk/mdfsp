import cats.MonadThrow
import distribution.{DistributionHandler, StreamingReportService}
import distribution.domain.{ReleaseRepository, ReleaseService, StreamRepository, StreamService}
import distribution.infra.{FakeReleaseRepository, FakeStreamRepository}
import wallet.domain.{PaymentRequestRepository, PaymentRequestService}
import wallet.infra.FakePaymentRequestRepository

case class AppDI[F[_] : MonadThrow](
  releaseRepository: ReleaseRepository[F],
  releaseService: ReleaseService[F],
  streamRepository: StreamRepository[F],
  streamService: StreamService[F],
  streamingReportService: StreamingReportService[F],
  distributionHandler: DistributionHandler[F],
  paymentRequestRepository: PaymentRequestRepository[F],
  paymentRequestService: PaymentRequestService[F],
)

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
      StreamingReportService(streamRepo),
      DistributionHandler(),
      paymentReqRepo,
      PaymentRequestService(paymentReqRepo, streamRepo),
    )
