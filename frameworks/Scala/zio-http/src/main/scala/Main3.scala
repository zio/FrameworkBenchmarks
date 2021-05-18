/*
import zhttp.http._
import zhttp.service._
import zhttp.service.server.ServerChannelFactory
import zio._
import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object WebApp extends App {
  val port: Int = 8080
  val message: String                         = "Hello, World!"
  def createDate: String                      = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now)
  case class Message(message: String)
  implicit val codec: JsonValueCodec[Message] = JsonCodecMaker.make

  val app = Http.collect[Request] {
    case Method.GET -> Root / "plaintext" => Response.text(message)
    case Method.GET -> Root / "json"      => Response.jsonString(writeToString(Message(message)))
  }

  val server = Server.port(port) ++ Server.app(app) ++ Server.disableLeakDetection

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    val nThreads: Int = args.headOption.flatMap(_.toIntOption).getOrElse(0)

    // Create a new server
    server.make
      .use(_ =>
        // Waiting for the server to start
        console.putStrLn(s"Server started on port ${port} and nThreads: ${nThreads}")

          // Ensures the server doesn't die after printing
          *> ZIO.never,
      )
      .provideCustomLayer(ServerChannelFactory.auto ++ EventLoopGroup.auto(nThreads))
      .exitCode
  }

}
*/
