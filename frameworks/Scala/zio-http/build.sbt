name := "zio-http"

version := "1.0.0"
scalaVersion := "2.13.6"

lazy val zhttp = ProjectRef(uri(s"https://github.com/dream11/zio-http.git#8e4f6bf3ba8f11d3eb081714330d131d216f4299"), "zhttp")
lazy val root = (project in file("."))
  .settings(
    name := "helloExample",
    fork := true,
    libraryDependencies ++=
      Seq(
        "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core" % "2.9.1",
        "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % "2.9.1" % "compile-internal",
        "com.amazonaws" % "aws-java-sdk" % "1.11.500",
        "dev.zio" %% "zio-config" % "1.0.6",
        "dev.zio" %% "zio-config-magnolia" % "1.0.6",

      ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
  .dependsOn(zhttp)
assembly / assemblyMergeStrategy := {
  case x if x.contains("io.netty.versions.properties") => MergeStrategy.discard
  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}
