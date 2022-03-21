import zhttp.http.{Http, Request, Response}

trait BenchmarkApp {
  protected val app: Http[Any, Throwable, Request, Response] = Http.collect[Request] { case _ =>
    Response.text("Hello, ZIO!")
  }
}
