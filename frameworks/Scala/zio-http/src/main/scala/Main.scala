import _root_.config.BenchmarkConfig
import zhttp.service.server.ServerChannelFactory
import zhttp.service.{EventLoopGroup, Server}
import zio._

object Main extends App with BenchmarkServer {
  val env = ServerChannelFactory.auto ++ EventLoopGroup.auto(8)

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    for {
      server <- ZIO.fromEither(BenchmarkConfig.make(args))
      _ <- (server ++ Server.app(app)).make.use { _ => console.putStrLn("Starting server") *> ZIO.never }
    } yield ()
  }.provideCustomLayer(env).exitCode
}
