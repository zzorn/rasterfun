package org.rasterfun.ui

import java.awt.{Color, Dimension}
import javax.swing.JScrollPane
import library.LibraryCompView

/**
 * 
 */
object UiSettings {

  val clipboard: Clipboard = new Clipboard()
  val sourceManager: SourceManager = new SourceManager()

  // TODO: Move selection manager to GroupView or similar
  val selectionManager: SelectionManager = new SelectionManager()

  val graphComponentSize = 80
  val libraryPreviewSize = 64

  val previewBorderSize = 4

  var componentColor = Color.LIGHT_GRAY
  var componentViewBackgroundColor = new Color(0.5f, 0.5f, 0.5f)

  val extraScrollSpace = 64

  val scrollUnitSize = 30
  val scrollBlockSize = scrollUnitSize * 10

  def setScrollIncrements(scroller: JScrollPane): JScrollPane = {
    scroller.getVerticalScrollBar.setUnitIncrement(UiSettings.scrollUnitSize)
    scroller.getVerticalScrollBar.setBlockIncrement(UiSettings.scrollBlockSize)
    scroller.getHorizontalScrollBar.setUnitIncrement(UiSettings.scrollUnitSize)
    scroller.getHorizontalScrollBar.setBlockIncrement(UiSettings.scrollBlockSize)
    scroller
  }
}