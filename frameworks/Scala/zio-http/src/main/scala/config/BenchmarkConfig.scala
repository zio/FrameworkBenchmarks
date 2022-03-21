package config

import zhttp.service.{Server, UServer}
import zio.config._
import zio.config.magnolia.DeriveConfigDescriptor._
import zio.{Has, ZIO, ZLayer, system}

object BenchmarkConfig {
  type BenchmarkConfig = Has[Service]

  private def leakDetectionLevel(level: String): UServer = level match {
    case "disabled" => Server.disableLeakDetection
    case "simple"   => Server.simpleLeakDetection
    case "advanced" => Server.advancedLeakDetection
    case "paranoid" => Server.paranoidLeakDetection
  }

  private val config = descriptor[Service]

  def make: ZLayer[system.System, ReadError[String], Has[Service]] = {
    for {
      source        <- ConfigSource.fromSystemEnv
      configuration <- ZIO.fromEither(read(config from source))
    } yield configuration
  }.toLayer

  def generateServer: ZIO[BenchmarkConfig, Nothing, UServer] = ZIO.service[Service].map { config =>
    val server = Server.port(8080)

    server ++ leakDetectionLevel(config.leakDetectionLevel)
    if (config.acceptContinue) server ++ Server.acceptContinue
    if (config.disableKeepAlive) server ++ Server.disableKeepAlive
    if (config.consolidateFlush) server ++ Server.consolidateFlush
    if (config.disableFlowControl) server ++ Server.disableFlowControl
    if (config.maxRequestSize > -1) server ++ Server.enableObjectAggregator(config.maxRequestSize)

    server
  }

  final case class Service(
    leakDetectionLevel: String = "disabled",
    acceptContinue: Boolean = false,
    disableKeepAlive: Boolean = false,
    consolidateFlush: Boolean = true,
    disableFlowControl: Boolean = true,
    maxRequestSize: Int = -1,
  )
}
