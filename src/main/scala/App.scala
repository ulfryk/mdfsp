import cats.effect.{IO, IOApp}

object App extends IOApp.Simple:
  override def run: IO[Unit] =
    val appContext = AppDI[IO]
    // TODO: Add api server…
    IO.println(s"Hello world! $appContext").void
