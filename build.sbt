name         := "SubRipEditor"

description  := "Simple editor for SubRipText (.srt) subtitles"

version      := "1.0.1"


scalaVersion           := "2.10.3"

EclipseKeys.withSource := true


libraryDependencies ++= Seq(
  // Test
  "junit"          %  "junit"           % "4.11"  % "test", 
  "org.scalatest"  %% "scalatest"       % "2.1.3" % "test",
  // Logging
  "org.slf4s"      %% "slf4s-api"       % "1.7.6",
  "ch.qos.logback" %  "logback-classic" % "1.1.2",
  // Other
  "com.typesafe"   %  "config"          % "1.2.0",
  "org.scala-lang" %  "scala-swing"     % "2.10.3"
)

buildInfoSettings

sourceGenerators in Compile <+= buildInfo

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion)

buildInfoPackage := "org.fs.subrip"
