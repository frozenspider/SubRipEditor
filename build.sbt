name         := "subrip-editor"
description  := "Simple editor for SubRipText (.srt) subtitles"
version      := "1.0.6-dev"
scalaVersion := "2.12.3"

sourceManaged            := baseDirectory.value / "src_managed"
sourceManaged in Compile := baseDirectory.value / "src_managed" / "main" / "scala"
sourceManaged in Test    := baseDirectory.value / "src_managed" / "test" / "scala"

lazy val root = (project in file("."))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, buildInfoBuildNumber),
    buildInfoPackage := "org.fs.subrip"
  )

libraryDependencies ++= Seq(
  // Logging
  "org.slf4s"                        %% "slf4s-api"                % "1.7.25",
  "ch.qos.logback"                   %  "logback-classic"          % "1.1.2",
  // Other
  "org.scala-lang.modules"           %% "scala-swing"              % "2.1.1",
  "org.scala-lang.modules"           %% "scala-parser-combinators" % "1.1.2",
  "org.scala-lang.modules"           %% "scala-xml"                % "1.0.6",
  "com.typesafe"                     %  "config"                   % "1.3.2",
  "com.googlecode.juniversalchardet" %  "juniversalchardet"        % "1.0.3",
  // Test
  "junit"                            %  "junit"                    % "4.12"  % "test",
  "org.scalactic"                    %% "scalactic"                % "3.0.4" % "test",
  "org.scalatest"                    %% "scalatest"                % "3.0.4" % "test"
)
