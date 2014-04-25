package org.fs.subrip.entity

/**
 * @author FS
 */
case class SubtitlesWithComment[A](
  subs: Seq[A],
  comment: Option[String])
