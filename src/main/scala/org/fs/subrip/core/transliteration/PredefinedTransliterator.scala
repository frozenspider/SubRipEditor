package org.fs.subrip.core.transliteration

/**
 * Corrects text written in wrong keyboard layout (for multi-lingual keyboards).
 *
 * @author FS
 */
class PredefinedTransliterator(layouts: Map[String, IndexedSeq[Char]]) extends Transliterator {
  require({
    val encodingSizes = layouts.values.map(_.size)
    encodingSizes.toSet.size <= 1
  }, "Encodings has inequal size")

  override lazy val layoutCodes = layouts.keys.toSeq

  override def transliterate(layoutCodes: Seq[String])(s: String) =
    determineSourceDestLayouts(layoutCodes, s) match {
      case None =>
        s
      case Some((from, to)) => {
        val layoutIndices = getLayoutIndices(s, from)
        val stringWithIndices = s zip layoutIndices
        val remapped = stringWithIndices map {
          case (c, None)                 => c
          case (c, Some(i)) if c.isUpper => to(i).toUpper
          case (c, Some(i))              => to(i)
        }
        remapped.mkString
      }
    }

  private def determineSourceDestLayouts(layoutCodes: Seq[String],
                                         s: String): Option[(IndexedSeq[Char], IndexedSeq[Char])] = {
    val layoutsSeq = layoutCodes map layouts
    val hits = layoutsSeq map (encSeq => getLayoutIndices(s, encSeq).count(_.isDefined))
    val maxHit = hits.max
    if (hits.count(_ == maxHit) != 1) {
      None
    } else {
      val maxHitIdx = hits.indexOf(maxHit)
      val layoutsSeq2 = layoutsSeq :+ layoutsSeq.head
      Some((layoutsSeq2(maxHitIdx), layoutsSeq2(maxHitIdx + 1)))
    }
  }

  private def getLayoutIndices(s: IndexedSeq[Char],
                               layout: IndexedSeq[Char]): Seq[Option[Int]] =
    s map (c => layout.indexOf(c.toLower) match {
      case -1 => None
      case x  => Some(x)
    })
}
