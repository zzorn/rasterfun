package org.rasterfun.ui

import javax.swing.tree.{TreePath, TreeModel}
import org.rasterfun.library.{LibraryListener, Category, Library}
import org.rasterfun.component.Comp
import javax.swing.event.{TreeModelEvent, TreeModelListener}
import javax.swing.{JPanel, JSplitPane, JTree}

/**
 * 
 */

class LibraryView(library: Library) extends RichPanel("Library", true) {

  private var categoryListeners: List[TreeModelListener] = Nil

  private def notifyTreeChanged() {
    val path: Array[AnyRef] = Array(library.root)
    categoryListeners foreach {_.treeStructureChanged(new TreeModelEvent(null, path)) }
  }

  library.addLibraryListener(new LibraryListener {
    def onCategoryAdded(category: Category) {notifyTreeChanged()}
    def onCategoryRemoved(category: Category) {notifyTreeChanged()}
    def onComponentAdded(category: Category, comp: Comp) {}
    def onComponentRemoved(category: Category, comp: Comp) {}
  })

  val categoryBrowser = new JTree(new TreeModel(){

    def addTreeModelListener(l: TreeModelListener) { categoryListeners ::= l }
    def removeTreeModelListener(l: TreeModelListener) { categoryListeners = categoryListeners filterNot ( _ == l)}
    def getIndexOfChild(parent: Object, child: Object): Int =parent.asInstanceOf[Category].indexOf(child.asInstanceOf[Category])
    def valueForPathChanged(path: TreePath, newValue: Object) {}
    def isLeaf(node: Object) = node.asInstanceOf[Category].isLeafCategory
    def getChildCount(parent: Object) = parent.asInstanceOf[Category].subcategoriesCount
    def getChild(parent: Object, index: Int) = parent.asInstanceOf[Category].subCategoryAt(index)
    def getRoot = library.root
  })


  add(new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, categoryBrowser, new JPanel()))


}