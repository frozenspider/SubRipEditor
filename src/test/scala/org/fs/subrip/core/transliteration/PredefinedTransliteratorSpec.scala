package org.fs.subrip.core.transliteration

import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.TableDrivenPropertyChecks

/**
 * @author FS
 */
@RunWith(classOf[JUnitRunner])
class PredefinedTransliteratorSpec
    extends FlatSpec
    with TableDrivenPropertyChecks {

  behavior of "Predefined Transliterator"

  it should "transliterate various cases" in {
    val tl = new PredefinedTransliterator(Map(
      "ENG" -> "abcde",
      "NUM" -> "12345"
    ))
    val examples = Table[String, String]( // format: OFF
      (("src",      "expected")),
      // Obvious
      ("abcde",     "12345"),
      ("aaeebbccd", "115522334"),
      ("12345",     "abcde"),
      ("115522334", "aaeebbccd"),
      // Empty
      ("",          ""),
      // Some non-matching
      ("zxcv",      "zx3v"),
      ("zx3v",      "zxcv"),
      // All non-matching
      ("xyz",       "xyz"),
      // Ambiguous
      ("123abc",    "123abc")
    ) // format: ON
    forAll(examples) { (src, expected) =>
      assert(tl.transliterate(Seq("ENG", "NUM"))(src) === expected)
    }
  }
}
