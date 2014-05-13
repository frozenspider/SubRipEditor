package org.fs.subrip.io.parsing

import org.fs.subrip.io.TextFormat
import org.fs.subrip.utility.SubRipParsers._
import org.slf4s.Logging

/**
 * @author FS
 */
class SeqParsingFormat[A](
  singleFormat: TextFormat[A] with TextParsingReader[A])
    extends TextFormat[Seq[A]]
    with TextParsingReader[Seq[A]]
    with Logging {

  override def parser: Parser[Seq[A]] =
    singleFormat.parser.+

  override def logAfterParsing = Some {
    case (ms, Success(r, _)) => s"Parsed ${r.size} entries in ${ms} ms"
  }

  override def asString: (Seq[A] => String) =
    (recs: Seq[A]) =>
      (recs map (singleFormat.asString)).mkString
}
