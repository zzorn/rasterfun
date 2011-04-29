package org.rasterfun.ui

import graph.{GroupCompView, GroupView}
import library.LibraryView
import net.miginfocom.swing.MigLayout
import org.rasterfun.components.Group
import javax.swing._
import org.rasterfun.library.Library
import org.rasterfun.util.{RichPanel, SimpleFrame}
import java.awt.{Dimension, BorderLayout, ScrollPane}
import org.rasterfun.component.Comp

/**
 * Main Rasterfun UI
 */
class RasterfunUi(library: Library) {

  private val groupView: GroupView = new GroupView()
  private val preview: Preview = new Preview(showTitle = false)
  private val frame: JFrame = createFrame()

  def setModel(comp: Comp) {
    UiSettings.selectionManager.clearSelection()
    groupView.group = comp
    preview.comp = comp
  }

  private def createFrame(): JFrame = {


    val mainPanel = new JPanel(new MigLayout("fill"))

    //mainPanel.add(createToolbar(), "dock north, height 32!, growx")

    //mainPanel.add(createLibrary(), "dock west, width 200!, height 100%, growx, growy")
    //val eastPanel = new JPanel(new MigLayout())
    //eastPanel.add(createPreview(), "dock north, width 300!, height 300!")
    //eastPanel.add(createComponentEditor(), "dock south, height 100%, width 100%, growx, growy")
    //mainPanel.add(eastPanel, "dock east, width 300px, growy")

    val right = new JPanel(new MigLayout("fill"))
    val rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, createPreview(), createComponentEditor())
    rightSplit.setResizeWeight(0)
    rightSplit.setDividerLocation(256)
    right.add(rightSplit, "push, grow")

    val centerRight = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createGraphView(), right)
    centerRight.setDividerLocation(330)
    centerRight.setResizeWeight(1)
    val root = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createLibrary(), centerRight)
    root.setResizeWeight(0)
    root.setDividerLocation(160)

    mainPanel.add(root, "width 100%, height 100%, grow")

    new SimpleFrame("Rasterfun", mainPanel)
  }

  private def createToolbar(): JComponent = {
    val panel = new RichPanel(border = true)
    panel
  }

  private def createLibrary(): JComponent = {
    return new LibraryView(library)
  }

  private def createGraphView(): JComponent = {
    val panel = new RichPanel(constraints = "align 50% 50%, fill")
    panel.setBackground(UiSettings.componentViewBackgroundColor)

    // Some space so that the view can be scrolled so that there is some space to the edges
    val sp = UiSettings.extraScrollSpace
    panel.setBorder(BorderFactory.createEmptyBorder(sp, sp, sp, sp))

    panel.add(groupView, "align 50% 50%")

    val scrollPane = new JScrollPane(panel)
    UiSettings.setScrollIncrements(scrollPane)
    scrollPane.getViewport.setBackground(UiSettings.componentViewBackgroundColor)
    scrollPane
  }


  private def createPreview(): JComponent = {
    val panel = new JPanel(new MigLayout("fill"))
    panel.add(preview, "dock north, w 100%, h 100%")

    UiSettings.selectionManager.addSelectionListener({(view: GroupCompView) =>
      if (view != null) preview.comp = view.comp
      else preview.comp = null
    })

    panel.setPreferredSize(new Dimension(320, 320))

    panel
  }


  private def createComponentEditor(): JComponent = {
    val panel = new RichPanel("Component", scrollableTrackViewportWidth = true)

    val scrollPane = new JScrollPane(panel,
                                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER)

    UiSettings.selectionManager.addSelectionListener({(view: GroupCompView) =>
      if (view == null) {
        panel.removeAll()
        panel.revalidate()
        panel.repaint()
      }
      else {
        panel.removeAll()
        val editor = view.comp.createEditor
        //editor.setPreferredSize(new Dimension(300, 300))
        panel.add(editor, "growy, width 100%")
        panel.revalidate()
        panel.invalidate()
        panel.repaint()
        scrollPane.revalidate()
        scrollPane.invalidate()
        scrollPane.repaint()
      }
    })

    scrollPane
  }



}