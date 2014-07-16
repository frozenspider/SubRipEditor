package org.fs.subrip.ui;

import java.awt.Color
import java.awt.Font
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.io.File

import scala.swing._
import scala.swing.event.ButtonClicked
import scala.swing.event.MouseClicked
import scala.util.Failure
import scala.util.Success

import org.fs.subrip.BuildInfo
import org.fs.subrip.core.ClipboardSupport
import org.fs.subrip.core.OptionsSupport
import org.fs.subrip.core.SubtitlesIOSupport
import org.fs.subrip.core.transliteration.ConfigurableTransliterator
import org.fs.subrip.entity.subrip.SubRipRecord
import org.fs.subrip.entity.subrip.TimeMark
import org.fs.subrip.io.impl.subrip.SubRipRecordCommenter
import org.fs.subrip.io.impl.subrip.SubRipRecordTextFormat
import org.fs.subrip.io.parsing.SeqParsingFormat
import org.fs.subrip.ui.model.SubtitlesList
import org.slf4s.Logging

import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.KeyStroke
import javax.swing.WindowConstants
import javax.swing.filechooser.FileNameExtensionFilter

/**
 * @author FS
 */
class SubRipEditorUI extends Frame
    with SubtitlesIOSupport
    with ClipboardSupport
    with OptionsSupport
    with Logging {

  private implicit val srtFormat = SubRipRecordTextFormat
  private implicit val srtSeqFormat = new SeqParsingFormat(SubRipRecordTextFormat)
  private implicit val srtCommenter = SubRipRecordCommenter

  private val defaultFont = new Font("Courier New", Font.PLAIN, 18)
  private def defaultBorder = BorderFactory.createLineBorder(Color.gray, 1)

  private lazy val editorPane = new TextArea
  private lazy val commentsPane = new TextArea
  private lazy val fromField = new TextField
  private lazy val toField = new TextField
  private lazy val searchField = new TextField
  private lazy val subtitlesList = new SubtitlesList(IndexedSeq(
    SubRipRecord(
      id = 1,
      start = TimeMark(01, 23, 45, 678),
      end = TimeMark(12, 34, 56, 789),
      text = "Double-click entity to copy it's content into editor screen"
    )
  ))

  private val transliterator = new ConfigurableTransliterator
  private var currFile: Option[File] = None
  private var unsavedChanges: Boolean = false
  private var comments = "";

  //
  // Initialization block
  //
  try {
    def UnfocusableButton(label: String) = new Button(label) { focusable = false }

    val findNextBtn = UnfocusableButton("Find Next")
    val findFromStartBtn = UnfocusableButton("Find From Start")
    val pasteLeftBtn = UnfocusableButton("Paste")
    val pasteRightBtn = UnfocusableButton("Paste")
    val loadBtn = UnfocusableButton("Load")
    val reloadBtn = UnfocusableButton("Reload")
    val saveBtn = UnfocusableButton("Save")
    val closeBtn = UnfocusableButton("Close")
    val transliterateBtn = UnfocusableButton("Transliterate")
    val addBtn = UnfocusableButton("Add")
    val insertBeforeBtn = UnfocusableButton("Insert Before")
    val setBtn = UnfocusableButton("Set")
    val removeBtn = UnfocusableButton("Remove")
    val shiftAllBtn = UnfocusableButton("Shift All")
    val shiftBeforeBtn = UnfocusableButton("Shift Before")
    val shiftAfterBtn = UnfocusableButton("Shift After")

    val arrowLabel = new Label(" --> ")
    val editorScrollPane = new ScrollPane(editorPane)
    contents = new BorderPanel {
      def addHotkey(key: String, event: Int, mod: Int, f: => Unit): Unit = {
        peer.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
          .put(KeyStroke.getKeyStroke(event, mod), key)
        peer.getActionMap.put(key, new javax.swing.AbstractAction {
          def actionPerformed(arg: java.awt.event.ActionEvent): Unit = f
        })
      }

      import BorderPanel.Position._
      val topPanel = new BorderPanel {
        val fileButtonsPanel = new FlowPanel(
          loadBtn,
          reloadBtn,
          saveBtn,
          closeBtn
        )
        layout(fileButtonsPanel) = West
      }
      layout(topPanel) = North
      val centerPanel = new BorderPanel {
        val tabbedPane = new TabbedPane {
          pages += new TabbedPane.Page("Subtitles", new ScrollPane(subtitlesList))
          pages += new TabbedPane.Page("Comments", new ScrollPane(commentsPane))
        }
        layout(tabbedPane) = Center
        val searchPanel = new FlowPanel(FlowPanel.Alignment.Center)(
          new Label("Search: "),
          searchField,
          findNextBtn,
          findFromStartBtn
        )
        layout(searchPanel) = South
      }
      layout(centerPanel) = Center
      val bottomPanel = new BorderPanel {
        val bottomTopPanel = new FlowPanel(
          pasteLeftBtn,
          fromField,
          arrowLabel,
          toField,
          pasteRightBtn
        )
        layout(bottomTopPanel) = North
        layout(editorScrollPane) = Center
        val buttonPanel = new BorderPanel {
          val controlButtonsPanel = new FlowPanel(
            transliterateBtn,
            addBtn,
            insertBeforeBtn,
            setBtn,
            removeBtn
          )
          layout(controlButtonsPanel) = Center
          val timingButtonsPanel = new FlowPanel(
            shiftAllBtn,
            shiftBeforeBtn,
            shiftAfterBtn
          )
          layout(timingButtonsPanel) = East
        }
        layout(buttonPanel) = South
      }
      layout(bottomPanel) = South
      addHotkey("save", KeyEvent.VK_S, InputEvent.CTRL_MASK, ???)
    }
    def styleComponents(): Unit = {
      findNextBtn.margin = new Insets(0, 2, 0, 2)
      findFromStartBtn.margin = new Insets(0, 2, 0, 2)
      pasteLeftBtn.margin = new Insets(2, 2, 2, 2)
      pasteRightBtn.margin = new Insets(2, 2, 2, 2)
      searchField.columns = 30
      searchField.font = defaultFont
      subtitlesList.border = defaultBorder
      subtitlesList.font = defaultFont
      subtitlesList.fireSubtitlesUpdated()
      commentsPane.font = defaultFont
      commentsPane.border = defaultBorder
      editorPane.font = defaultFont
      editorScrollPane.border = defaultBorder
      editorScrollPane.preferredSize = new Dimension(0, 100)
      arrowLabel.font = defaultFont
      fromField.font = defaultFont
      fromField.columns = 14
      toField.font = defaultFont
      toField.columns = 14
    }
    styleComponents()

    listenTo(findNextBtn, findFromStartBtn, pasteLeftBtn, pasteRightBtn, loadBtn, reloadBtn, saveBtn, closeBtn,
      transliterateBtn, addBtn, insertBeforeBtn, setBtn, removeBtn, shiftAllBtn, shiftBeforeBtn, shiftAfterBtn,
      subtitlesList.mouse.clicks)
    // Button reactions
    reactions += {
      case ButtonClicked(`findNextBtn`)      => findNextAction()
      case ButtonClicked(`findFromStartBtn`) => findFromStartAction()
      case ButtonClicked(`pasteLeftBtn`)     => pasteFromClipboard(fromField)
      case ButtonClicked(`pasteRightBtn`)    => pasteFromClipboard(toField)
      case ButtonClicked(`loadBtn`)          => loadSubtitlesFileAction()
      case ButtonClicked(`reloadBtn`)        => reloadSubtitlesFileAction()
      case ButtonClicked(`saveBtn`)          => saveSubtitlesFileAction()
      case ButtonClicked(`closeBtn`)         => closeSubtitlesFileAction()
      case ButtonClicked(`transliterateBtn`) => transliterateAction()
      case ButtonClicked(`addBtn`)           => addEntryAction
      case ButtonClicked(`insertBeforeBtn`)  => insertEntryBeforeAction()
      case ButtonClicked(`setBtn`)           => setEntryAction()
      case ButtonClicked(`removeBtn`)        => removeEntryAction()
      case ButtonClicked(`shiftAllBtn`)      => shiftAllTimingAction()
      case ButtonClicked(`shiftBeforeBtn`)   => shiftTimingBeforeAction()
      case ButtonClicked(`shiftAfterBtn`)    => shiftTimingAfterAction()
    }
    // Mouse click reactions
    reactions += {
      case MouseClicked(`subtitlesList`, _, _, clicks, _) if clicks >= 2 => subtitlesList.selectedEntry map editRecord
    }

    title = SubRipEditorUI.DefaultTitle
    size = new Dimension(1000, 700)
    peer.setLocationRelativeTo(null)
    peer.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)
    reloadOptions()
    transliterator.reloadConfig()
  } catch {
    case th: Throwable => showError(th)
  }

  override def closeOperation(): Unit = {
    val shouldDispose: Boolean =
      if (!unsavedChanges)
        true
      else {
        import Dialog._
        Dialog.showConfirmation(message = "Do you want to save your changes?", title = "Unsaved changes", optionType = Options.YesNoCancel) match {
          case Result.Yes    => { saveSubtitlesFileAction() }
          case Result.No     => true
          case Result.Cancel => false
          case Result.Closed => false
        }
      }
    if (shouldDispose) {
      saveOptions()
      dispose()
    }
  }

  private def showError(ex: Throwable): Unit = {
    ex.printStackTrace()
    Dialog.showMessage(message = ex, title = "Something bad happened", messageType = Dialog.Message.Error)
    // TODO: Show more info about line:character if present
  }

  private def showError(s: String): Unit = {
    Dialog.showMessage(message = "Error: " + s, title = "Something bad happened", messageType = Dialog.Message.Error)
  }

  private def editRecord(record: SubRipRecord): Unit = {
    fromField.text = record.start.toString
    toField.text = record.end.toString
    editorPane.text = record.text
  }

  /** Tries to create a new record (with id = 0) from values in input components */
  private def createRecord: Option[SubRipRecord] = {
    val tryStart = TimeMark.fromString(fromField.text)
    val tryEnd = TimeMark.fromString(toField.text)
    val tryRes = for {
      start <- tryStart
      end <- tryEnd
    } yield SubRipRecord(
      id = 0,
      start = start,
      end = end,
      text = editorPane.text
    )
    tryRes match {
      case Success(res) =>
        Some(res)
      case Failure(ex) =>
        Dialog.showMessage(
          message = "Incorrect time format, right ones: 34:56.789  12:34:56.789  12:34:56,789",
          messageType = Dialog.Message.Error
        )
        None
    }
  }

  private def loadSubtitlesFileInner(file: File, scrollToTop: Boolean): Unit = {
    currFile = Some(file)
    updateOptions(options copy (lastAccessedFilePath = file.getAbsolutePath))
    loadSubtitlesFile(file) match {
      case Success((subs, comment)) => {
        subtitlesList.listData = subs
        if (scrollToTop) subtitlesList.ensureIndexIsVisible(0)
        commentsPane.text = comment
      }
      case Failure(ex) => showError(ex)
    }
    unsavedChanges = false
    clearSelection()
    this.title = file.getName + " - " + SubRipEditorUI.DefaultTitle
  }

  private def createFileChooser: FileChooser = {
    val lastAccessdFile = new File(options.lastAccessedFilePath)
    val fc = new FileChooser(if (lastAccessdFile.exists) lastAccessdFile else lastAccessdFile.getParentFile)
    fc.fileFilter = new FileNameExtensionFilter(".srt subtitles", "srt")
    fc.peer.setPreferredSize(new Dimension(800, 600))
    fc
  }

  private def noChangesOrSure: Boolean =
    !unsavedChanges || accepted(Dialog.showConfirmation(message = "Are you sure?"))

  private def accepted(dialog: => Dialog.Result.Value): Boolean = {
    val res = dialog
    res match {
      case Dialog.Result.Yes | Dialog.Result.Ok => true
      case _                                    => false
    }
  }

  //
  // Selection
  //
  private def selectedIdxOption: Option[Int] =
    subtitlesList.selection.anchorIndex match {
      case -1 => None
      case x  => Some(x)
    }

  private def clearSelection(): Unit = {
    // FIXME: This isn't working!
    val listPeer: javax.swing.JList[_] = subtitlesList.peer.asInstanceOf[javax.swing.JList[_]]
    listPeer.getSelectionModel().clearSelection()
  }

  //
  // Actions and Callbacks
  //
  private def pasteFromClipboard(tf: TextField): Unit = {
    clipboardContent foreach (tf.text = _)
  }

  private def findNextAction(): Unit = {
    val toFind = searchField.text
    val from = selectedIdxOption.getOrElse(-1) + 1
    val idx = subtitlesList.listData drop (from) indexWhere (_.text.toLowerCase contains toFind)
    if (idx != -1) {
      subtitlesList.selectIndices(from + idx)
      subtitlesList.ensureIndexIsVisible(from + idx)
    }
  }

  private def findFromStartAction(): Unit = {
    val toFind = searchField.text
    val idx = subtitlesList.listData indexWhere (_.text.toLowerCase contains toFind)
    if (idx != -1) {
      subtitlesList.selectIndices(idx)
      subtitlesList.ensureIndexIsVisible(idx)
    }
  }

  private def loadSubtitlesFileAction(): Unit = {
    val fc = createFileChooser
    fc.showOpenDialog(null) match {
      case FileChooser.Result.Approve => loadSubtitlesFileInner(fc.selectedFile, true)
      case _                          => ()
    }
  }

  private def reloadSubtitlesFileAction(): Unit =
    currFile foreach { file =>
      if (noChangesOrSure)
        loadSubtitlesFileInner(file, false)
    }

  private def saveSubtitlesFileAction(): Boolean = {
    val fileOpt =
      if (subtitlesList.listData.isEmpty)
        None
      else if (currFile.isDefined)
        currFile
      else {
        val fc = createFileChooser
        fc.showSaveDialog(null) match {
          case FileChooser.Result.Approve => Some(fc.selectedFile)
          case _                          => None
        }
      }
    fileOpt foreach { file =>
      unsavedChanges = false
      saveSubtitlesFile(file, subtitlesList.listData, commentsPane.text)
    }
    fileOpt.isDefined
  }

  def closeSubtitlesFileAction(): Unit =
    currFile foreach { file =>
      if (noChangesOrSure) {
        currFile = None
        unsavedChanges = false
        subtitlesList.listData = Nil
        clearSelection()
        this.title = SubRipEditorUI.DefaultTitle
      }
    }

  private def transliterateAction(): Unit = {
    // TODO: Make it configurable
    editorPane.text = transliterator.transliterate(Seq("English", "Russian"))(editorPane.text)
  }

  private def addEntryAction(): Unit =
    createRecord match {
      case Some(rec) => {
        val oldSubs = subtitlesList.listData
        val newRec = rec copy (id = oldSubs.lastOption.map(_.id).getOrElse(0) + 1)
        val newIdx = oldSubs.size
        subtitlesList.listData = oldSubs :+ newRec
        subtitlesList.fireSubtitlesUpdated(newIdx)
        subtitlesList.ensureIndexIsVisible(newIdx)
        unsavedChanges = true
      }
      case None => ()
    }

  def insertEntryBeforeAction(): Unit =
    (createRecord, selectedIdxOption) match {
      case (Some(rec), Some(idx)) => {
        val oldSubs = subtitlesList.listData
        val newRec = rec copy (id = oldSubs(idx).id)
        subtitlesList.listData = (oldSubs.take(idx) :+ newRec) ++ oldSubs.drop(idx).map (r => r copy (id = r.id + 1))
        subtitlesList.fireSubtitlesUpdated()
        subtitlesList.ensureIndexIsVisible(idx)
        unsavedChanges = true
      }
      case _ => ()
    }

  def setEntryAction(): Unit =
    (createRecord, selectedIdxOption) match {
      case (Some(rec), Some(idx)) => {
        val oldSubs = subtitlesList.listData
        val newRec = rec copy (id = oldSubs(idx).id)
        subtitlesList.listData = oldSubs.updated(idx, newRec)
        subtitlesList.fireSubtitlesUpdated(idx)
        subtitlesList.ensureIndexIsVisible(idx)
        subtitlesList.selectIndices(idx)
        unsavedChanges = true
      }
      case _ => ()
    }

  def removeEntryAction(): Unit =
    selectedIdxOption match {
      case Some(idx) if accepted(Dialog.showConfirmation(message = "Are you sure?")) => {
        val oldSubs = subtitlesList.listData
        subtitlesList.listData = oldSubs.take(idx) ++ oldSubs.drop(idx + 1).map(r => r copy (id = r.id - 1))
        subtitlesList.fireSubtitlesUpdated(idx)
        subtitlesList.ensureIndexIsVisible(idx - 1)
        unsavedChanges = true
      }
      case _ => ()
    }

  def shiftAllTimingAction(): Unit =
    for {
      msStr <- Dialog.showInput[String](message = "Number of milliseconds to shift?", initial = "0")
      if msStr matches "-?\\d+"
    } try {
      val ms = msStr.toLong
      subtitlesList.listData = subtitlesList.listData map {
        case SubRipRecord(id, start, end, text) => SubRipRecord(id, start + ms, end + ms, text)
      }
      subtitlesList.fireSubtitlesUpdated()
      unsavedChanges = true
      selectedIdxOption map (idx => subtitlesList.selectIndices(idx))
    } catch {
      case th: Throwable => showError(th)
    }

  def shiftTimingBeforeAction(): Unit =
    for {
      idx <- selectedIdxOption
      msStr <- Dialog.showInput[String](message = "Number of milliseconds to shift?", initial = "0")
      if msStr matches "-?\\d+"
    } try {
      val targetId = subtitlesList.listData(idx).id
      val ms = msStr.toLong
      subtitlesList.listData = subtitlesList.listData collect {
        case SubRipRecord(id, start, end, text) if (id <= targetId) => SubRipRecord(id, start + ms, end + ms, text)
        case rest: SubRipRecord                                     => rest
      }
      subtitlesList.fireSubtitlesUpdated()
      unsavedChanges = true
      subtitlesList.selectIndices(idx)
    } catch {
      case th: Throwable => showError(th)
    }

  def shiftTimingAfterAction(): Unit =
    for {
      idx <- selectedIdxOption
      msStr <- Dialog.showInput[String](message = "Number of milliseconds to shift?", initial = "0")
      if msStr matches "-?\\d+"
    } try {
      val targetId = subtitlesList.listData(idx).id
      val ms = msStr.toLong
      subtitlesList.listData = subtitlesList.listData collect {
        case SubRipRecord(id, start, end, text) if (id >= targetId) => SubRipRecord(id, start + ms, end + ms, text)
        case rest: SubRipRecord                                     => rest
      }
      subtitlesList.fireSubtitlesUpdated()
      unsavedChanges = true
      subtitlesList.selectIndices(idx)
    } catch {
      case th: Throwable => showError(th)
    }

}

object SubRipEditorUI {
  lazy val DefaultTitle = BuildInfo.name + " v" + BuildInfo.version
}
