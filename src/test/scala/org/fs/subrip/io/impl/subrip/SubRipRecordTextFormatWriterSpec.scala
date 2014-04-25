package org.fs.subrip.io.impl.subrip

import org.fs.subrip.entity.subrip.SubRipRecord
import org.fs.subrip.entity.subrip.TimeMark
import org.fs.subrip.io.TextWriter
import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.junit.JUnitRunner

/**
 * @author FS
 */
@RunWith(classOf[JUnitRunner])
class SubRipRecordTextFormatWriterSpec
    extends FlatSpec {

  lazy val writer: TextWriter[SubRipRecord] = SubRipRecordTextFormat

  behavior of "srt writer"

  it should "correctly output a single record" in {
    val record = SubRipRecord( // format: OFF
      id    = 1,
      start = TimeMark(0, 0, 0, 000),
      end   = TimeMark(100, 59, 59, 333),
      text  = "Two\nLines"
    ) // format: ON

    assert(
      writer.asString(record) === ("" +
        "1\n" +
        "00:00:00,000 --> 100:59:59,333\n" +
        "Two\n" +
        "Lines\n" +
        "\n"
      )
    )
  }

  it should "correctly output two records" in {
    val record1 = SubRipRecord( // format: OFF
      id    = 1,
      start = TimeMark(0, 0, 10, 000),
      end   = TimeMark(0, 0, 15, 001),
      text  = "Two\nLines"
    ) // format: ON
    val record2 = SubRipRecord( // format: OFF
      id    = 2,
      start = TimeMark(0, 10, 0, 000),
      end   = TimeMark(0, 15, 0, 001),
      text  = "Another\nThree\nLines"
    ) // format: ON

    assert(
      (writer.asString(record1) ++ writer.asString(record2)) ===
        ("""
1
00:00:10,000 --> 00:00:15,001
Two
Lines

2
00:10:00,000 --> 00:15:00,001
Another
Three
Lines

""".trim + "\n\n")
    )
  }
}
