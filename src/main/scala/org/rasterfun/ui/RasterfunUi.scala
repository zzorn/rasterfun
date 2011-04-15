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
  private val preview: Preview = new Preview()

  private val frame: JFrame = createFrame()

  def setModel(group: Group) {
    groupView.group = group
    preview.comp = group.root
  }

  private def createFrame(): JFrame = {

    val mainPanel = new JPanel(new MigLayout("fill"))
    mainPanel.add(createGraphView(), "width 100%, height 100%, grow")
    mainPanel.add(createToolbar(), "dock north, height 32!, growx")

    val eastPanel = new JPanel(new MigLayout())
    eastPanel.add(createPreview(), "dock north, width 300!, height 300!")
    eastPanel.add(createComponentEditor(), "dock south, growx, growy")
    mainPanel.add(eastPanel, "dock east, growy, growx")

    mainPanel.add(createLibrary(), "dock west, width 64:256:512")


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

  private def createPreview(): JComponent = {
    val panel = new RichPanel("Preview", true)
    panel.add(preview, "grow, width 100%, height 100%")
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