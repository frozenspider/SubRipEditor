package org.fs.subrip.io.impl.subrip

import org.fs.subrip.entity.subrip.SubRipRecord

import org.fs.subrip.entity.subrip.TimeMark.Parser._
import org.fs.subrip.io.TextFormat
import org.fs.subrip.io.parsing.TextParsingReader
import org.fs.subrip.utility.StopWatch._
import org.fs.subrip.utility.SubRipParsers._
import org.slf4s.Logging

/**
 * @author FS
 */
object SubRipRecordTextFormat
    extends TextFormat[SubRipRecord]
    with TextParsingReader[SubRipRecord]
    with Logging {

  override lazy val parser: Parser[SubRipRecord] =
    (
      (`Br*`) ~> // Prefixed with arbitrary many newlines
      (Decimal <~ Br) ~ // Id
      (timeMarkParser <~ " --> ") ~ (timeMarkParser) ~ // Start --> End
      (Br ~> """[^\r\n]+""".r).* <~ // Text lines
      (`Br*` ~ ( // Followed by optional newlines...
        Eof | // ...till the end...
        guard(Decimal) // ...or until new entry ID is encountered
      ))
    ) ^^ { case (id ~ start ~ end ~ lines) => SubRipRecord(id, start, end, lines mkString "\n") }

  override def logAfterParsing = None

  override def asString: (SubRipRecord => String) =
    (rec: SubRipRecord) => {
      rec.id + "\n" +
        rec.start + " --> " + rec.end + "\n" +
        rec.text + "\n\n"
    }
}
