package zio.http

import zio._
import zio.http._
import io.netty.util.AsciiString

object Main extends ZIOAppDefault {
  private val message: String = "Hello, World!"
  private val json: String    = s"""{"message":"$message"}"""

  private val plaintextPath = "/plaintext"
  private val jsonPath      = "/json"

  private val STATIC_SERVER_NAME = AsciiString.cached("zio-http")

  private val JsonResponse = Response
    .json(json)
    .withServerTime
    .withServer(STATIC_SERVER_NAME)
    .freeze

  private val PlainTextResponse = Response
    .text(message)
    .withServerTime
    .withServer(STATIC_SERVER_NAME)
    .freeze

  private def plainTextApp(response: Response) = Http.fromHExit(HExit.succeed(response)).whenPathEq(plaintextPath)

  private def jsonApp(json: Response) = Http.fromHExit(HExit.succeed(json)).whenPathEq(jsonPath)

  private def app = for {
    plainTextResponse <- PlainTextResponse
    jsonResponse      <- JsonResponse
  } yield plainTextApp(plainTextResponse) ++ jsonApp(jsonResponse)


  val config = ZLayer.succeed(
    ServerConfig.default
      .copy(
        nThreads = 8,
        consolidateFlush = true,
        flowControl = false,
        leakDetectionLevel = ServerConfig.LeakDetectionLevel.DISABLED,
      ),
  )

  override val run =
  app
    .tap(_ => ZIO.debug(s">>>>>>>>>>>>>>>>>>>>>> STARTING BENCHMARK SERVER <<<<<<<<<<<<<<<<<<<<<<<<<<"))
    .flatMap(Server.serve(_, None))
    .provide(config, Server.live)

}