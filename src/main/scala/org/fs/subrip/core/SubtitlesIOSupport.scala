package org.fs.subrip.core

import java.io.BufferedInputStream

import java.io.File
import java.io.FileInputStream
import java.io.StringReader
import java.nio.charset.CodingErrorAction

import scala.io.Codec
import scala.io.Source
import scala.util.Try

import org.fs.subrip.core.internal.IOSupport
import org.fs.subrip.entity.SubtitleRecord
import org.fs.subrip.io.TextCommenter
import org.fs.subrip.io.TextReader
import org.fs.subrip.io.TextWriter
import org.fs.subrip.utility.StopWatch._
import org.mozilla.universalchardet.UniversalDetector
import org.slf4s.Logging

trait SubtitlesIOSupport extends IOSupport { this: Logging =>
  private def readFromFile[A](f: File)(implicit r: TextReader[Seq[A]]): Try[Seq[A]] = Try {
    log.info(s" === Loading file '${f.getName}' ===")
    measureAndLog(log) {
      val codec = determineCodec(f)
      val content = Source.fromFile(f, 1024)(codec).mkString
      val normalized = normalizeContent(content)
      r.read(new StringReader(normalized))
    }(ms => s" === Done in $ms ms ===")
  }.flatten

  private def determineCodec(f: File): Codec = {
    val chunk = readFileChunk(f, 4096)
    val detector = new UniversalDetector(null)
    detector.handleData(chunk.toArray, 0, chunk.length)
    detector.dataEnd()
    val encoding = detector.getDetectedCharset
    if (encoding == null) {
      log.info("Encoding undetermined, using UTF-8")
      Codec.UTF8 onMalformedInput CodingErrorAction.IGNORE
    } else {
      log.info("Encoding determined to be " + encoding)
      Codec(encoding) onMalformedInput CodingErrorAction.IGNORE
    }
  }

  private def readFileChunk(f: File, maxLen: Int): Seq[Byte] = {
    val bis = new BufferedInputStream(new FileInputStream(f))
    try {
      val buffer = Array.ofDim[Byte](maxLen)
      val len = bis.read(buffer)
      buffer.take(len)
    } finally {
      bis.close()
    }
  }

  private def writeToFile[A](s: Seq[A], f: File)(implicit w: TextWriter[A]): Try[Unit] = Try {
    measureAndLog(log) {
      val content = (s map w.asString).mkString
      writeTextFile(f, content)
    }(ms => s"Written ${s.size} entries in file ${f.getName} in $ms ms")
  }

  def loadSubtitlesFile[A <: SubtitleRecord](file: File)(implicit reader: TextReader[Seq[A]]): Try[(Seq[A], String)] = {
    readFromFile(file) map { subsWithComments =>
      val (comments, subs) = subsWithComments.partition(_.isComment)
      val comment = comments map (_.text) mkString "\n"
      (subs, comment)
    }
  }

  /** To workaround UTF BOM symbol */
  private def normalizeContent(s: String): String =
    s.replace("\ufeff", "");

  def saveSubtitlesFile[A <: SubtitleRecord](file: File, subs: Seq[A], comment: String)(implicit w: TextWriter[A], commenter: TextCommenter[A]): Unit = {
    val commentNormalized = comment.trim match {
      case "" => None
      case x  => Some(x)
    }
    val subsWithComment = commentNormalized map (c => commenter.setComment(subs, c)) getOrElse subs
    writeToFile(subsWithComment, file)
  }
}
