package org.fs.subrip.ui.model

import scala.swing.ListView
import scala.swing.TextArea
import scala.swing.TextComponent

import org.fs.subrip.io.TextWriter

/**
 * @author FS
 */
private[model] class SubtitlesCellRenderer[A](implicit writer: TextWriter[A])
    extends ListView.AbstractRenderer[A, TextComponent](new TextArea) {

  def cellRenderer: TextComponent =
    component

  override def configure(list: ListView[_],
                         selected: Boolean,
                         focused: Boolean,
                         value: A,
                         index: Int): Unit = {
    component.text = writer.asString(value).trim
  }
}
