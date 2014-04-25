package org.fs.subrip.io

import java.io.File

import scala.io.Codec
import scala.io.Source
import scala.util.Try

import org.fs.subrip.utility.IOSupport
import org.fs.subrip.utility.StopWatch._
import org.slf4s.Logging

object SubtitlesIO extends IOSupport with Logging {

  def readFromFile[A](f: File)(implicit r: TextReader[Seq[A]]): Try[Seq[A]] = Try {
    measureAndLog(log) {
      r.read(Source.fromFile(f, 1042)(Codec.UTF8).reader)
    } (ms => s"File ${f.getName} loaded in $ms ms")
  }.flatten

  def writeToFile[A](s: Seq[A], f: File)(implicit w: TextWriter[A]): Try[Unit] = Try {
    measureAndLog(log) {
      val content = (s map w.asString).mkString
      writeTextFile(f, content)
    } (ms => s"Written ${s.size} entries in file ${f.getName} in $ms ms")
  }
}
