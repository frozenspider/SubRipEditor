package org.fs.subrip.io.impl.subrip

import org.fs.subrip.entity.subrip.SubRipRecord
import org.fs.subrip.entity.subrip.TimeMark

import org.fs.subrip.io.TextCommenter

/**
 * @author FS
 */
object SubRipRecordCommenter extends TextCommenter[SubRipRecord] {
  def addComment(subs: Seq[SubRipRecord], comment: String): Seq[SubRipRecord] = {
    val commentRec = SubRipRecord(
      id = 999999,
      start = TimeMark(9999, 0, 0, 0),
      end = TimeMark(9999, 0, 0, 0),
      text = comment)
    subs :+ commentRec
  }
}
