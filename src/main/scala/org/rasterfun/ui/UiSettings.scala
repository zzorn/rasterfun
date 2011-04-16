package org.rasterfun.ui

import java.awt.{Color, Dimension}
import javax.swing.JScrollPane

/**
 * 
 */
object UiSettings {

  val componentWidth = 128
  val componentHeight = 128
  val libraryPreviewSize = 80

  var componentSize = new Dimension(componentWidth, componentHeight)
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