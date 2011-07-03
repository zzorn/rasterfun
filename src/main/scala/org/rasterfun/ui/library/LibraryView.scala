package org.rasterfun.ui.library

import javax.swing.tree.{TreePath, TreeModel}
import org.rasterfun.library.{LibraryListener, Category, Library}
import org.rasterfun.component.Comp
import net.miginfocom.swing.MigLayout
import java.awt.event.ComponentAdapter
import javax.swing.event.{TreeSelectionEvent, TreeSelectionListener, TreeModelEvent, TreeModelListener}
import javax.swing._
import org.rasterfun.ui.UiSettings
import org.rasterfun.ui.graph.GroupCompView
import java.awt.{Rectangle, FlowLayout, Dimension}
import org.rasterfun.util.{WrapLayout, RichPanel}

/**
 * 
 */
// TODO: Separate category and component browsers?
class LibraryView(library: Library) extends RichPanel("Library") {

  private var categoryListeners: List[TreeModelListener] = Nil

  private val categoryBrowser = new JTree(new TreeModel(){
    def addTreeModelListener(l: TreeModelListener) { categoryListeners ::= l }
    def removeTreeModelListener(l: TreeModelListener) { categoryListeners = categoryListeners filterNot ( _ == l)}
    def getIndexOfChild(parent: Object, child: Object): Int =parent.asInstanceOf[Category].indexOf(child.asInstanceOf[Category])
    def valueForPathChanged(path: TreePath, newValue: Object) {}
    def isLeaf(node: Object) = node.asInstanceOf[Category].isLeafCategory
    def getChildCount(parent: Object) = parent.asInstanceOf[Category].subcategoriesCount
    def getChild(parent: Object, index: Int) = parent.asInstanceOf[Category].subCategoryAt(index)
    def getRoot = library.root
  })

  private var componentBrowser: JComponent = null

  init()

  private def init() {
    categoryBrowser.setPreferredSize(new Dimension(220, 200))
    categoryBrowser.setRootVisible(false)
    categoryBrowser.addTreeSelectionListener(new TreeSelectionListener{
      def valueChanged(e: TreeSelectionEvent) {
        val selectedCategory = e.getNewLeadSelectionPath.getLastPathComponent.asInstanceOf[Category]
        updateComponentBrowser(selectedCategory)
      }
    })

    componentBrowser = new JPanel(new WrapLayout(hGap = 8, vGap = 8)) with Scrollable {
      def getScrollableBlockIncrement(visibleRect: Rectangle, orientation: Int, direction: Int) = UiSettings.scrollBlockSize
      def getScrollableUnitIncrement(visibleRect: Rectangle, orientation: Int, direction: Int) = UiSettings.scrollUnitSize
      def getPreferredScrollableViewportSize = getPreferredSize
      def getScrollableTracksViewportHeight = false
      def getScrollableTracksViewportWidth = true
    }
    
    val componentScroll = new JScrollPane(componentBrowser,
                                          ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                          ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER)
    UiSettings.setScrollIncrements(componentScroll)

    add(new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
                       categoryBrowser,
                       componentScroll), "width 100%, height 100%")

    library.addLibraryListener(new LibraryListener {
      def onCategoryAdded(category: Category) {notifyTreeChanged()}
      def onCategoryRemoved(category: Category) {notifyTreeChanged()}
      def onComponentAdded(category: Category, comp: Comp) {}
      def onComponentRemoved(category: Category, comp: Comp) {}
    })

    categoryBrowser.setSelectionPath(library.defaultPath)
  }

  private def notifyTreeChanged() {
    val path: Array[AnyRef] = Array(library.root)
    categoryListeners foreach {_.treeStructureChanged(new TreeModelEvent(null, path)) }
  }

  def updateComponentBrowser(category: Category) {
    UiSettings.sourceManager.clearSource()

    componentBrowser.removeAll()

    if (category != null) category.components foreach { c =>
      componentBrowser.add(new LibraryCompView(c))
    }

    revalidate()
    repaint()
  }



}