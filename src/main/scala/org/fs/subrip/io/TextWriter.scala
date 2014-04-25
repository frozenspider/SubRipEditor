package org.fs.subrip.io

/**
 * @author FS
 */
trait TextWriter[A] {
  def asString: (A => String)
}
