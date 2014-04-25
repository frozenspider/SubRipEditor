package org.fs.subrip.entity.subrip

import org.fs.subrip.entity.SubtitleRecord

/**
 * @author FS
 */
case class SubRipRecord(
    id: Int,
    start: TimeMark,
    end: TimeMark,
    text: String) extends SubtitleRecord {

  /** The most frequently used comment style is setting an ID to 9999 or more */
  override def isComment =
    id >= 9999

  override def toString =
    s"$id $start --> $end : $text"
}
