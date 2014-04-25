package org.fs.subrip.core

import java.io.File

import scala.util.Try

import org.fs.subrip.entity.SubtitleRecord
import org.fs.subrip.io.SubtitlesIO
import org.fs.subrip.io.TextCommenter
import org.fs.subrip.io.TextReader
import org.fs.subrip.io.TextWriter
import org.fs.subrip.utility.ConfigSupport

/**
 * @author FS
 */
trait SubRipEditorUISupport extends ConfigSupport {

  private val optionsFileName = "options.conf"
  private val lastAccessFileKey = "last-accessed-file"

  private var _options: UIOptions = new UIOptions(
    lastAccessedFilePath = ""
  )

  def options = _options

  def updateOptions(newOptions: UIOptions): Unit = {
    _options = newOptions
    saveOptions()
  }

  def reloadOptions(): Unit = {
    val optionsContent = loadConfigFile(optionsFileName)
    _options = new UIOptions(
      lastAccessedFilePath = optionsContent(lastAccessFileKey)
    )
  }

  def saveOptions(): Unit = {
    saveConfigFile(optionsFileName,
      Map(
        lastAccessFileKey -> options.lastAccessedFilePath
      ) map { case (k, v) => k -> v.replaceAllLiterally("""\""", """\\""") }
    )
  }

  def loadSubtitlesFileContent[A <: SubtitleRecord](file: File)(implicit reader: TextReader[Seq[A]]): Try[(Seq[A], String)] = {
    SubtitlesIO.readFromFile(file) map { subsWithComments =>
      val (comments, subs) = subsWithComments.partition(_.isComment)
      val comment = comments map (_.text) mkString "\n"
      (subs, comment)
    }
  }

  /** To workaround UTF BOM symbol */
  private def normalizeString(s: String): String =
    s.replace("\ufeff", "");

  def saveChanges[A <: SubtitleRecord](file: File, subs: Seq[A], comment: String) // format: OFF
                                      (implicit w: TextWriter[A], commenter: TextCommenter[A]): Unit = { // format: ON
    val commentNormalized = comment.trim match {
      case "" => None
      case x  => Some(x)
    }
    val subsWithComment = commentNormalized map (c => commenter.addComment(subs, c)) getOrElse subs
    SubtitlesIO.writeToFile(subsWithComment, file)
  }
}
