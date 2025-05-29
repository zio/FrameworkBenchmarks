name := "zio-http"
version := "1.0.0"
scalaVersion := "2.13.16"
lazy val zioHttpJVM = ProjectRef(file("./zio-http"), "zioHttpJVM")
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
    },
    assemblyMergeStrategy in assembly := {
    case PathList("META-INF", "versions", _ @ _*) => MergeStrategy.discard
    case x => (assemblyMergeStrategy in assembly).value(x)
  }
  ).dependsOn(zioHttpJVM)
