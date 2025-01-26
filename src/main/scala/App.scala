import cats.effect.std.Console
import cats.effect.{Async, ExitCode, IO, IOApp}
import com.comcast.ip4s.{ipv4, port}
import distribution.infra.api.distributionRoutes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.http4s.server.middleware.Logger
import org.http4s.{HttpApp, HttpRoutes}

def httpAppLogged[F[_] : {Async, Console}](app: HttpRoutes[F]): HttpApp[F] =
  Logger.httpRoutes[F](
    logHeaders = true,
    logBody = true,
    logAction = Some((msg: String) => Console[F].println(msg))
  )(app).orNotFound

object App extends IOApp.Simple:

  private def startServer(di: AppDI[IO]): IO[Nothing] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8114")
      .withHttpApp(httpAppLogged(Router("/" -> distributionRoutes(di.releaseService, di.streamService))))
      .build
      .useForever

  override def run: IO[Unit] =
    val appContext = AppDI[IO]
    IO.println(s"Hello world! $appContext") *> startServer(appContext).as(ExitCode.Success)
