package org.fs.subrip.core

import org.fs.subrip.utility.UIOptions
import org.fs.subrip.core.internal.ConfigSupport

trait OptionsSupport extends ConfigSupport {

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
}