package org.fs.subrip.io

import scala.util.Try

/**
 * @author FS
 */
trait TextReader[+A] {
  def read(reader: java.io.Reader): Try[A]
}
