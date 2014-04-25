package org.fs.subrip.io.parsing

import scala.util.Try
import org.fs.subrip.io.TextReader
import org.fs.subrip.utility.StopWatch
import org.fs.subrip.utility.SubRipParsers._
import org.slf4s.Logging
import org.fs.subrip.entity.ex.ReadFailedException

/**
 * @author FS
 */
trait TextParsingReader[A] extends TextReader[A] { this: Logging =>
  def parser: Parser[A]

  def logAfterParsing: Option[PartialFunction[(Long, ParseResult[A]), String]] = None

  final def read(reader: java.io.Reader): Try[A] = Try {
    def parse = parseAll(parser, reader)
    val parsed = logAfterParsing match {
      case None     => parse
      case Some(pf) => StopWatch.measureAndLog2(log)(parse)(pf)
    }
    parsed match {
      case Success(r, _)       => r
      case f @ NoSuccess(m, _) => throw new ReadFailedException(m, f.toString)
    }
  }
}

object TextParsingReader {
  def fromParser[A](p: Parser[A]) = new TextParsingReader[A] with Logging {
    override lazy val parser = p
  }
}