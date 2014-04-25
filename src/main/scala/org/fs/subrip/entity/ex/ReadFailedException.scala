package org.fs.subrip.entity.ex

/**
 * @author FS
 */
case class ReadFailedException(msg: String, explanation: String)
  extends Exception(s"$msg: $explanation")
