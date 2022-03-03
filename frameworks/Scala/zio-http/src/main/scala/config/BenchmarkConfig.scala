package config

import zhttp.service.{Server, UServer}
import zio.config._
import zio.config.magnolia.DeriveConfigDescriptor._


object BenchmarkConfig {
  private val default = Map(
    "leakDetectionLevel" -> "simple",
    "acceptContinue" -> "false",
    "disableKeepAlive" -> "false",
    "consolidateFlush" -> "false",
    "disableFlowControl" -> "false",
    "maxRequestSize" -> "-1"
  )

  def fromString(level: String): UServer = level match {
    case "disabled" => Server.disableLeakDetection
    case "simple" => Server.simpleLeakDetection
    case "advanced" => Server.advancedLeakDetection
    case "paranoid" => Server.paranoidLeakDetection
  }


  def make(args: List[String] = Nil): Either[ReadError[String], UServer] =
    read(descriptor[Service] from ConfigSource.fromMap(default)).map { config =>
      val server = Server.port(8090)

      server ++ fromString(config.leakDetectionLevel)
      if (config.acceptContinue) server ++ Server.acceptContinue
      if (config.keepAlive) server ++ Server.disableKeepAlive
      if (config.consolidateFlush) server ++ Server.consolidateFlush
      if (config.disableKeepAlive) server ++ Server.disableFlowControl
      if (config.maxRequestSize > -1) server ++ Server.enableObjectAggregator(config.maxRequestSize)

      server
    }

  case class Service(
                      leakDetectionLevel: String = "disabled",
                      acceptContinue: Boolean = false,
                      keepAlive: Boolean = true,
                      consolidateFlush: Boolean = false,
                      disableKeepAlive: Boolean = true,
                      maxRequestSize: Int = -1
                    )
}
