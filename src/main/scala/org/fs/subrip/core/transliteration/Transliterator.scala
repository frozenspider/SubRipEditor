package org.fs.subrip.core.transliteration

/**
 * @author FS
 */
trait Transliterator {
  def layoutCodes: Seq[String]

  def transliterate(layoutCodes: Seq[String])(s: String): String
}
