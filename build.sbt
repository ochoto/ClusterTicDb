organization := "com.github.ochoto"

name := "ClusterTicDb"

version := "0.1-SNAPSHOT"

scalaVersion := "2.9.2"

licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "1.7.1" % "test",
  "org.ccil.cowan.tagsoup" % "tagsoup" % "1.2.1",
  "se.fishtank" %% "css-selectors-scala" % "0.1.2"
)

scalacOptions ++= Seq("-deprecation")

// ---------------------------------------------------------------------------
// Publishing criteria

publishTo <<= version { v: String =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>http://ochoto.com/</url>
  <scm>
    <url>git@github.com:ochoto/ClusterTicDb.git/</url>
    <connection>scm:git:git@github.com:ochoto/ClusterTicDb.git</connection>
  </scm>
  <developers>
    <developer>
      <id>Ochoto</id>
      <name>Xabier Ochotorena</name>
      <url>http://ochoto.com</url>
    </developer>
  </developers>
)


