name := "zio-http"
version := "1.0.0"
scalaVersion := "2.13.6"
lazy val zhttp = ProjectRef(uri(s"https://github.com/---COMMIT_SHA---"), "zhttp")
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