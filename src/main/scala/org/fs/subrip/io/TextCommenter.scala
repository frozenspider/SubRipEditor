package org.fs.subrip.io

/**
 * Knows about comment format amongst subtitle record, and can set one.
 *
 * @author FS
 */
trait TextCommenter[A] {
  def setComment(subs: Seq[A], comment: String): Seq[A]
}
