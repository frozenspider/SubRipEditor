package org.fs.subrip.entity.subrip

import scala.util.Try
import org.fs.subrip.io.parsing.TextParsingReader

/**
 * @author FS
 */
case class TimeMark(hrs: Int, mins: Int, secs: Int, ms: Int) {
  require(hrs >= 0, "Hour was negative")
  require(mins >= 0 && mins < 60, "Minute was outside of bounds")
  require(secs >= 0 && secs < 60, "Second was outside of bounds")
  require(ms >= 0 && ms < 1000, "Millisecond was outside of bounds")

  val totalMs: Long =
    (((hrs * 60L + mins) * 60L + secs) * 1000L + ms)

  def +(deltaMs: Long): TimeMark =
    TimeMark.fromTotalMs(totalMs + deltaMs)

  override def toString = f"$hrs%02d:$mins%02d:$secs%02d,$ms%03d"
}

object TimeMark {
  import org.fs.subrip.io.TextReader
  import org.fs.subrip.io.parsing.TextParsingReader

  private val reader = TextParsingReader.fromParser(Parser.timeMarkParser)

  private def hrsMinsSecsMsFromTotalMs(totalMs: Long): (Int, Int, Int, Int) = {
    val divSeq = Seq(1000, 60, 60)
    val (minsSecsMs, hrs) = divSeq.foldLeft((Seq.empty[Int], totalMs)){
      case ((acc, remainder), divison) => ((remainder % divison).toInt +: acc, remainder / divison)
    }
    (hrs.toInt, minsSecsMs(0), minsSecsMs(1), minsSecsMs(2))
  }

  def fromTotalMs(totalMs: Long): TimeMark = {
    val (hrs, mins, secs, ms) = hrsMinsSecsMsFromTotalMs(totalMs)
    TimeMark(hrs, mins, secs, ms)
  }

  def fromString(s: String): Try[TimeMark] =
    reader.read(new java.io.StringReader(s))

  object Parser {
    import org.fs.subrip.utility.SubRipParsers._

    // Parser combinator is too cumbersome to use here. Besides, they don't use backtracking without PackratParsers
    lazy val timeMarkParser: Parser[TimeMark] =
      """(\d+:)?\d?\d:\d?\d[,.]\d?\d?\d""".r ^^ { s =>
        val split = s.split("[:,.]").map(_.toInt).reverse.padTo(4, 0).reverse
        TimeMark(split(0), split(1), split(2), split(3))
      }
  }
}
