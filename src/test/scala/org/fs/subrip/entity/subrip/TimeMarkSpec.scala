package org.fs.subrip.entity.subrip

import org.scalatest._
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

/**
 * @author FS
 */
@RunWith(classOf[JUnitRunner])
class TimeMarkSpec
    extends FlatSpec
    with TableDrivenPropertyChecks {

  behavior of "TimeMark"

  it should "construct from total MS" in {
    val examples = Table[Long, TimeMark]( // format: OFF
      (("Total MS", "Expected TimeMmark")),
      (        0L,  TimeMark(  0,  0,  0,   0)),
      (        1L,  TimeMark(  0,  0,  0,   1)),
      (      100L,  TimeMark(  0,  0,  0, 100)),
      (      999L,  TimeMark(  0,  0,  0, 999)),
      (     1000L,  TimeMark(  0,  0,  1,   0)),
      (    59999L,  TimeMark(  0,  0, 59, 999)),
      (    60000L,  TimeMark(  0,  1,  0,   0)),
      (   360000L,  TimeMark(  0,  6,  0,   0)),
      (  3600000L,  TimeMark(  1,  0,  0,   0)),
      (360000000L,  TimeMark(100,  0,  0,   0))
    ) // format: ON
    forAll(examples) { (totalMs, expected) =>
      assert(TimeMark.fromTotalMs(totalMs) === expected)
    }
  }

  it should "add correctly" in {
    val examples = Table[TimeMark, Long, TimeMark]( // format: OFF
      (("Source TimeMmark",        "Delta MS", "Expected TimeMmark")),
      (TimeMark(  0,  0,  0,   0),        0L,  TimeMark(  0,  0,  0,   0)),
      (TimeMark(  0,  0,  0,   1),        0L,  TimeMark(  0,  0,  0,   1)),
      (TimeMark(  0,  0,  0,   1),        1L,  TimeMark(  0,  0,  0,   2)),
      (TimeMark(  0,  0,  0,   1),       -1L,  TimeMark(  0,  0,  0,   0)),
      (TimeMark(  0,  0,  0, 999),        1L,  TimeMark(  0,  0,  1,   0)),
      (TimeMark(100, 58, 58, 750),    61250L,  TimeMark(101,  0,  0,   0))
    ) // format: ON
    forAll(examples) { (src, delta, expected) =>
      assert(src + delta === expected)
    }
  }

  it should "parse time marks" in {
    val examples = Table[String, TimeMark]( // format: OFF
      (("String",      "Expected TimeMmark")),
      ("12:34:56,789", TimeMark( 12,34,56,789)),
      (   "34:56,789", TimeMark(  0,34,56,789)),
      (   "34:56.789", TimeMark(  0,34,56,789))
    ) // format: ON
    forAll(examples) { (str, expected) =>
      val parsed = TimeMark.fromString(str)
      assert(parsed.get === expected)
    }
  }
}
