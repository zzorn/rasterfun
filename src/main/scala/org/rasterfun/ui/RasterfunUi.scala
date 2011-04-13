package org.rasterfun.ui

import org.rasterfun.util.SimpleFrame
import net.miginfocom.swing.MigLayout
import javax.swing.{JLabel, JFrame, JPanel}
import org.rasterfun.components.Group

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

  private def createToolbar(): JPanel = {
    val panel = new RichPanel(border = true)
    panel
  }

  private def createLibrary(): JPanel = {
    val panel = new RichPanel("Library", true)
    panel
  }

  private def createComponentEditor(): JPanel = {
    val panel = new RichPanel("Component", true)
    panel
  }

  private def createGraphView(): JPanel = {
    val panel = new RichPanel(constraints = "align 50% 50%")
    panel.add(groupView, "grow")
    panel
  }

}