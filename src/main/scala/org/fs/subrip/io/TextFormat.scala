package org.fs.subrip.io

/**
 * @author FS
 */
trait TextFormat[A] extends TextReader[A] with TextWriter[A]
