package org.fs.subrip.core

import java.io.File

import scala.io.Codec
import scala.io.Source
import scala.util.Try

import org.fs.subrip.core.internal.IOSupport
import org.fs.subrip.entity.SubtitleRecord
import org.fs.subrip.io.TextCommenter
import org.fs.subrip.io.TextReader
import org.fs.subrip.io.TextWriter
import org.fs.subrip.utility.StopWatch._
import org.slf4s.Logging

trait SubtitlesIOSupport extends IOSupport { this: Logging =>
  private def readFromFile[A](f: File)(implicit r: TextReader[Seq[A]]): Try[Seq[A]] = Try {
    measureAndLog(log) {
      r.read(Source.fromFile(f, 1042)(Codec.UTF8).reader)
    } (ms => s"File ${f.getName} loaded in $ms ms")
  }.flatten

  private def writeToFile[A](s: Seq[A], f: File)(implicit w: TextWriter[A]): Try[Unit] = Try {
    measureAndLog(log) {
      val content = (s map w.asString).mkString
      writeTextFile(f, content)
    } (ms => s"Written ${s.size} entries in file ${f.getName} in $ms ms")
  }

  def loadSubtitlesFile[A <: SubtitleRecord](file: File)(implicit reader: TextReader[Seq[A]]): Try[(Seq[A], String)] = {
    readFromFile(file) map { subsWithComments =>
      val (comments, subs) = subsWithComments.partition(_.isComment)
      val comment = comments map (_.text) mkString "\n"
      (subs, comment)
    }
  }

  /** To workaround UTF BOM symbol */
  private def normalizeString(s: String): String =
    s.replace("\ufeff", "");

  def saveSubtitlesFile[A <: SubtitleRecord](file: File, subs: Seq[A], comment: String) // format: OFF
                                            (implicit w: TextWriter[A], commenter: TextCommenter[A]): Unit = { // format: ON
    val commentNormalized = comment.trim match {
      case "" => None
      case x  => Some(x)
    }
    val subsWithComment = commentNormalized map (c => commenter.addComment(subs, c)) getOrElse subs
    writeToFile(subsWithComment, file)
  }
}
