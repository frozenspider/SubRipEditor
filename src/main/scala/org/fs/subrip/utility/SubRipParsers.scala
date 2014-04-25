package org.fs.subrip.utility

import scala.util.parsing.combinator.RegexParsers

/**
 * Serves for sharing parsers across code.
 *
 * (Why the hell RegexParsers is a trait and Parser/ParseResult are inner classes?)
 *
 * @author FS
 */
object SubRipParsers extends RegexParsers {
  override val skipWhitespace = false

  val Eof: Parser[Unit] = new Parser[Unit] {
    override def apply(in: Input): ParseResult[Unit] = if (in.atEnd) Success((), in) else Failure("Not EOF", in)
  }

  def digits(i: Int): Parser[Int] = s"\\d{$i}".r ^^ { _.toInt }

  val Decimal: Parser[Int] = """\d+""".r ^^ { _.toInt }

  val Br: Parser[Unit] = """\r?\n""".r ^^^ ()

  val `Br*`: Parser[Unit] = """(?:\r?\n)*""".r ^^^ ()

}
