package org.fs.subrip.core.internal

import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

import scala.io.Codec

/**
 * @author FS
 */
private[core] trait IOSupport {
  private[core] def writeTextFile(f: File, content: String): Unit = {
    val osw = new OutputStreamWriter(new FileOutputStream(f), Codec.UTF8.charSet)
    try {
      osw.write(content)
      osw.flush()
    } finally {
      osw.close
    }
  }
}
