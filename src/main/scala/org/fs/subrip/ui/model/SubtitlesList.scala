package org.fs.subrip.ui.model

import java.awt.Color
import java.awt.Font

import scala.swing.ListView

import org.fs.subrip.io.TextWriter

import javax.swing.AbstractListModel
import javax.swing.BorderFactory
import javax.swing.JList
import javax.swing.ListSelectionModel

/**
 * @author FS
 */
class SubtitlesList[A](initialData: IndexedSeq[A])(implicit writer: TextWriter[A]) extends ListView[A](initialData) {

  /** Workaround to Scala Type System bug caused by absence of peer type in scala-swing */
  private lazy val typedPeer = peer.asInstanceOf[JList[A]]

  private lazy val renderingComponent = new SubtitlesCellRenderer {
    cellRenderer.border = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray)
  }

  renderer = renderingComponent
  typedPeer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)

  override def font_=(font: Font): Unit = {
    super.font = font
    renderingComponent.cellRenderer.font = font
  }

  override def listData_=(items: Seq[A]): Unit = {
    typedPeer.setModel(new SubtitlesListModel(items.toIndexedSeq))
  }

  override def listData: IndexedSeq[A] =
    typedPeer.getModel match {
      case m: SubtitlesListModel => m.items
      case _                     => super.listData.toIndexedSeq
    }

  def selectedEntry: Option[A] =
    selectedItems.headOption

  /**
   * Workaround for `selection.items` being a lazy value (what the hell, seriously?)
   *
   * TODO: Remove this workaround when scala-swing starts makins sense
   */
  private def selectedItems: Seq[A] =
    typedPeer.getSelectedValues.map(_.asInstanceOf[A])

  private def subtitlesModel: SubtitlesListModel =
    typedPeer.getModel.asInstanceOf[SubtitlesListModel]

  def fireSubtitlesUpdated(): Unit =
    subtitlesModel.fireSubtitlesUpdated

  def fireSubtitlesUpdated(changedIdx: Int): Unit =
    subtitlesModel.fireSubtitlesUpdated(changedIdx)

  private class SubtitlesListModel(val items: IndexedSeq[A]) extends AbstractListModel[A] {
    override def getElementAt(n: Int) = items(n)
    override def getSize = items.size

    def fireSubtitlesUpdated(): Unit =
      fireContentsChanged(this, 0, getSize)

    def fireSubtitlesUpdated(changedIdx: Int): Unit =
      fireContentsChanged(this, changedIdx, changedIdx)
  }
}