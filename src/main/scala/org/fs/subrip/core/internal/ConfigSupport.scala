package org.fs.subrip.core.internal

import java.io.File

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigParseOptions

/**
 * @author FS
 */
private[core] trait ConfigSupport extends IOSupport {
  private[core] def loadConfigFile(fileName: String): Map[String, String] = {
    import scala.collection.JavaConversions._
    val config = ConfigFactory.parseFileAnySyntax(new File(fileName), ConfigParseOptions.defaults.setAllowMissing(false))
    val configContent = config.root.unwrapped.toMap map {
      case (k, v: String) => (k -> v)
    }
    configContent
  }

  private[core] def saveConfigFile(fileName: String, data: Map[String, String]): Unit = {
    val file = new File(fileName)
    val content = data map {
      case (k, v) => s"""  "$k" : "$v""""
    } mkString ("{\n", "\n", "\n}")
    writeTextFile(file, content)
  }
}
