package org.fs.subrip.utility

import java.io.File
import java.io.OutputStreamWriter
import java.io.FileOutputStream
import scala.io.Codec

/**
 * @author FS
 */
trait IOSupport {
  def writeTextFile(f: File, content: String): Unit = {
    val osw = new OutputStreamWriter(new FileOutputStream(f), Codec.UTF8.charSet)
    try {
      osw.write(content)
      osw.flush()
    } finally {
      osw.close
    }
  }
}