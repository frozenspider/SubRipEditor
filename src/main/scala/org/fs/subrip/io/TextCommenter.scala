package org.fs.subrip.io

/**
 * @author FS
 */
trait TextCommenter[A] {
  def addComment(subs: Seq[A], comment: String): Seq[A]
}
