package org.rasterfun.ui

import org.rasterfun.util.SimpleFrame
import net.miginfocom.swing.MigLayout
import org.rasterfun.components.Group
import java.awt.ScrollPane
import javax.swing._

/**
 * Main Rasterfun UI
 */
class RasterfunUi {

  private val groupView: GroupView = new GroupView()

  private val frame: JFrame = createFrame()

  def setModel(group: Group) {
    groupView.group = group
  }

  private def createFrame(): JFrame = {

    val mainPanel = new JPanel(new MigLayout("fill"))
    mainPanel.add(createGraphView(), "width 64:800:3000, height 64:600:3000, grow")
    mainPanel.add(createToolbar(), "dock north, height 32!, growx")
    mainPanel.add(createLibrary(), "dock west, width 64:256:512")
    mainPanel.add(createComponentEditor(), "dock east, width 100:200:300, growy, growx 0")

    new SimpleFrame("Rasterfun", mainPanel)
  }

  private def createToolbar(): JComponent = {
    val panel = new RichPanel(border = true)
    panel
  }

  private def createLibrary(): JComponent = {
    val panel = new RichPanel("Library", true)
    panel
  }

  private def createComponentEditor(): JComponent = {
    val panel = new RichPanel("Component", true)
    panel
  }

  private def createGraphView(): JComponent = {
    val panel = new RichPanel(constraints = "align 50% 50%")
    panel.setBackground(UiSettings.componentViewBackgroundColor)

    // Some space so that the view can be scrolled so that there is some space to the edges
    val sp = UiSettings.extraScrollSpace
    panel.setBorder(BorderFactory.createEmptyBorder(sp, sp, sp, sp))

    panel.add(groupView, "grow")

    val scrollPane = new JScrollPane(panel)
    scrollPane.getVerticalScrollBar.setUnitIncrement(UiSettings.scrollUnitSize)
    scrollPane.getVerticalScrollBar.setBlockIncrement(UiSettings.scrollBlockSize)
    scrollPane.getHorizontalScrollBar.setUnitIncrement(UiSettings.scrollUnitSize)
    scrollPane.getHorizontalScrollBar.setBlockIncrement(UiSettings.scrollBlockSize)
    scrollPane
  }

}