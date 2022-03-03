import zhttp.http.{Http, Request, Response}
import zio.ZIO

trait BenchmarkServer {
  val app = Http.collectZIO[Request] {
    case _ => ZIO(Response.text("Hello, ZIO!"))
  }
}
