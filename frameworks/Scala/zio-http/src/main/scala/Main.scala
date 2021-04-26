import zhttp.http._
import zhttp.service.Server
import zio._
import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object WebApp extends App {
  val msg = "Hello, World!"
  val byteMessage = Chunk.fromArray(msg.getBytes(HTTP_CHARSET))
  val msgLength = msg.length.toLong

  def createDate: String = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now)

  case class Message(message: String)

  implicit val codec: JsonValueCodec[Message] = JsonCodecMaker.make

  val app = HttpApp.collect {
    case Method.GET -> Root / "plaintext" => Response.bytes(byteMessage, msgLength)
    case Method.GET -> Root / "json" => Response.jsonString(writeToString(Message(msg)))
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = Server.start(8080, app).exitCode

}
