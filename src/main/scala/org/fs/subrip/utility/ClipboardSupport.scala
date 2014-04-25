package org.fs.subrip.utility

import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.ClipboardOwner
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable

import scala.swing.UIElement

/**
 * @author FS
 */
trait ClipboardSupport extends ClipboardOwner { this: UIElement =>

  def clipboardContent: Option[String] = {
    val contents = toolkit.getSystemClipboard.getContents(null)
    if (contents == null || !contents.isDataFlavorSupported(DataFlavor.stringFlavor)) None
    else Some(contents.getTransferData(DataFlavor.stringFlavor).asInstanceOf[String])
  }

  override def lostOwnership(clipboard: Clipboard, contents: Transferable) = ()

}