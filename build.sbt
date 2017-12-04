name         := "subrip-editor"
description  := "Simple editor for SubRipText (.srt) subtitles"
version      := "1.0.6-dev"
scalaVersion := "2.11.7"

sourceManaged            <<= baseDirectory { _ / "src_managed" }
sourceManaged in Compile <<= baseDirectory { _ / "src_managed" / "main" / "scala" }
sourceManaged in Test    <<= baseDirectory { _ / "src_managed" / "test" / "scala" }

lazy val root = (project in file("."))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, buildInfoBuildNumber),
    buildInfoPackage := "org.fs.subrip"
  )

libraryDependencies ++= Seq(
  // Test
  "junit"                            %  "junit"                    % "4.11"  % "test", 
  "org.scalatest"                    %% "scalatest"                % "2.2.4" % "test",
  // Logging
  "org.slf4s"                        %% "slf4s-api"                % "1.7.12",
  "ch.qos.logback"                   %  "logback-classic"          % "1.1.2",
  // Other
  "com.typesafe"                     %  "config"                   % "1.2.0",
  "org.scala-lang.modules"           %  "scala-swing_2.11"         % "2.0.0-M2",
  "org.scala-lang.modules"           %% "scala-parser-combinators" % "1.0.4",
  "org.scala-lang.modules"           %% "scala-xml"                % "1.0.5",
  "com.googlecode.juniversalchardet" %  "juniversalchardet"        % "1.0.3"
)
