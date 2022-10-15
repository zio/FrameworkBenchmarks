name := "zio-http"
version := "1.0.0"
scalaVersion := "2.13.6"
lazy val zhttp = ProjectRef(uri(s"https://github.com/---COMMIT_SHA---"), "zioHttp")
// lazy val zhttp = ProjectRef(uri(s"https://github.com/zio/zio-http.git#1c541fa4"), "zioHttp") // AAN Base branch
// lazy val zhttp = ProjectRef(uri(s"https://github.com/zio/zio-http.git#0bf3168b"), "zioHttp") // Main branch
// lazy val zhttp = ProjectRef(uri(s"https://github.com/zio/zio-http.git#9263d554"), "zioHttp") // AAN Perf Iter 1
// lazy val zhttp = ProjectRef(uri(s"https://github.com/zio/zio-http.git#e664e73b"), "zioHttp") // AAN Perf Iter 2
// lazy val zhttp = ProjectRef(uri(s"https://github.com/zio/zio-http.git#ee2a6f73"), "zioHttp") // AAN Perf Iter 3
// lazy val zhttp = ProjectRef(uri(s"https://github.com/zio/zio-http.git#992742fa"), "zioHttp") // AAN Perf Iter 4
// lazy val zhttp = ProjectRef(uri(s"https://github.com/zio/zio-http.git#452a4d98"), "zioHttp") // AAN Perf Iter 5
// lazy val zhttp = ProjectRef(uri(s"https://github.com/zio/zio-http.git#620c1609"), "zioHttp") // AAN Perf Iter 5 (post-rebase)
// lazy val zhttp = ProjectRef(uri(s"https://github.com/zio/zio-http.git#2dd6e9ba"), "zioHttp") // AAN Perf Iter 6
// lazy val zhttp = ProjectRef(uri(s"https://github.com/zio/zio-http.git#22b07606"), "zioHttp") // AAN Perf Iter 7
// lazy val zhttp = ProjectRef(uri(s"https://github.com/zio/zio-http.git#d06e8a86"), "zioHttp") // AAN Perf Iter 8
// lazy val zhttp = ProjectRef(uri(s"https://github.com/zio/zio-http.git#ffd3d91f"), "zioHttp") // AAN Perf Iter 9
lazy val root  = (project in file("."))
  .settings(
    name := "helloExample",
    fork := true,
    libraryDependencies ++=
      Seq(
        "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core"   % "2.9.1",
        "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % "2.9.1" % "compile-internal",
      ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    assembly / assemblyMergeStrategy  := {
      case x if x.contains("io.netty.versions.properties") => MergeStrategy.discard
      case x =>
        val oldStrategy = (assembly / assemblyMergeStrategy).value
        oldStrategy(x)
    }
  ).dependsOn(zhttp)
