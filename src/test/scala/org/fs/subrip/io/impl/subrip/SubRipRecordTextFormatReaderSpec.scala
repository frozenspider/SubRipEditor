package org.fs.subrip.io.impl.subrip

import java.io.StringReader

import org.fs.subrip.entity.subrip.SubRipRecord
import org.fs.subrip.entity.subrip.TimeMark
import org.fs.subrip.io.parsing.SeqParsingFormat
import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.junit.JUnitRunner

/**
 * @author FS
 */
@RunWith(classOf[JUnitRunner])
class SubRipRecordTextFormatReaderSpec
    extends FlatSpec
    with TableDrivenPropertyChecks {

  lazy val reader = SubRipRecordTextFormat

  behavior of "srt parser"

  it should "parse single entry" in {
    val src = ("" +
      "1\n" +
      "00:00,000 --> 100:59:59,333\n" +
      "Two\n" +
      "Lines\n" +
      "\n"
    )
    val actual = reader.read(new StringReader(src)).get
    val expected = SubRipRecord( // format: OFF
      id    = 1,
      start = TimeMark(0, 0, 0, 000),
      end   = TimeMark(100, 59, 59, 333),
      text  = "Two\nLines"
    ) // format: ON
    assert(expected === actual)
  }

  it should "parse single entry terminated without line breaks" in {
    val src = ("" +
      "1\n" +
      "00:00,000 --> 100:59:59,333\n" +
      "Two\n" +
      "Lines"
    )
    val actual = reader.read(new StringReader(src)).get
    val expected = SubRipRecord( // format: OFF
      id    = 1,
      start = TimeMark(0, 0, 0, 000),
      end   = TimeMark(100, 59, 59, 333),
      text  = "Two\nLines"
    ) // format: ON
    assert(expected === actual)
  }

  it should "parse 10000 entries" in {
    def getEntryStr(i: Int): String = {
      s"$i\n" +
        "00:00,000 --> 100:59:59,333\n" +
        "Two\n" +
        "Lines\n" +
        "\n"
    }
    def getEntity(i: Int) =
      SubRipRecord( // format: OFF
        id    = i,
        start = TimeMark(0, 0, 0, 000),
        end   = TimeMark(100, 59, 59, 333),
        text  = "Two\nLines"
      ) // format: ON
    val src = ((1 to 10000) map getEntryStr) mkString ""
    val actual = new SeqParsingFormat(reader).read(new StringReader(src)).get
    val expected = (1 to 10000) map getEntity
    assert(expected === actual)
  }
}
