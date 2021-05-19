name := "zio-http"

version := "1.0.0"
scalaVersion := "2.13.5"
// val zhttpVersion = "1.0.0.0-RC15+19-630c92af-SNAPSHOT"
lazy val zhttp =
  ProjectRef(uri(s"https://github.com/dream11/zio-http.git#c5cf19dabe78d2f56ec8ae74f72c6e9323c92a52"), "zhttp")
lazy val root  = (project in file("."))
  .settings(
    name := "helloExample",
    libraryDependencies ++=
      Seq(
        "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core"   % "2.6.4",
        "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % "2.6.4" % "compile-internal",
        // "io.d11"                                 %% "zhttp"                 % zhttpVersion,
      ),
    resolvers ++= Seq(
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      "Sonatype OSS Snapshots s01" at "https://s01.oss.sonatype.org/content/repositories/snapshots",
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
  )
  .dependsOn(zhttp)
