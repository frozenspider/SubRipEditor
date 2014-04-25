package org.fs.subrip.utility

import org.slf4s.Logger

/**
 * @author FS
 */
class StopWatch {
  var timestamp = System.currentTimeMillis

  def peek: Long =
    System.currentTimeMillis - timestamp

  def set(): Long = {
    val now = System.currentTimeMillis
    val res = now - timestamp
    timestamp = now
    res
  }
}

/**
 * @author FS
 */
object StopWatch {
  /** Execute code block and return block result along with time taken */
  def measure[R](block: => R): (R, Long) = {
    val sw = new StopWatch
    val res = block
    (res, sw.peek)
  }

  /** Execute code block, log message and return block result */
  def measureAndLog[R](log: Logger)(block: => R)(msgGen: (Long => String)): R = {
    val (res, time) = measure(block)
    log.info(msgGen(time))
    res
  }

  /** Execute code block, log message and return block result */
  def measureAndLog2[R](log: Logger)(block: => R)(msgGen: PartialFunction[(Long, R), String]): R = {
    val (res, time) = measure(block)
    if (msgGen isDefinedAt (time, res)) {
      log.info(msgGen(time, res))
    }
    res
  }
}
