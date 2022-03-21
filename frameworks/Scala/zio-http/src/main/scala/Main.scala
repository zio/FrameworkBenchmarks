import _root_.config.BenchmarkConfig
import config.BenchmarkConfig._
import zhttp.service.server.ServerChannelFactory
import zhttp.service.{EventLoopGroup, Server}
import zio._

object Main extends App {
  private val env = ServerChannelFactory.auto ++ EventLoopGroup.auto(8)

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    for {
      http   <- BenchmarkApp.app
      server <- generateServer
      _      <- (server ++ Server.app(http)).make.use { _ => console.putStrLn("Starting server") *> ZIO.never }
    } yield ()
  }.provideSomeLayer(zio.console.Console.live ++ BenchmarkConfig.make ++ env).exitCode
}
