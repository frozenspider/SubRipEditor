package org.fs.subrip.entity

/**
 * @author FS
 */
trait SubtitleRecord {
  def text: String
  def isComment: Boolean
}