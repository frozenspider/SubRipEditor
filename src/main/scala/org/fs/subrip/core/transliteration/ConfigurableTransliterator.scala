package org.fs.subrip.core.transliteration

import org.fs.subrip.core.internal.ConfigSupport

/**
 * @author FS
 */
class ConfigurableTransliterator
    extends Transliterator
    with ConfigSupport {

  private val layoutsFileName = "transliteration.conf"

  private var inner: Transliterator = new PredefinedTransliterator(Map.empty)

  def reloadConfig(): Unit = {
    val configContent = loadConfigFile(layoutsFileName)
    val layouts = configContent map {
      case (key, value) => (key -> value.toIndexedSeq)
    }
    inner = new PredefinedTransliterator(layouts)
  }

  override lazy val layoutCodes =
    inner.layoutCodes

  override def transliterate(layoutCodes: Seq[String])(s: String): String =
    inner.transliterate(layoutCodes)(s)
}
