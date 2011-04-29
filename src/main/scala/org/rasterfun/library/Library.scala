package org.rasterfun.library

import org.rasterfun.component.Comp
import javax.swing.tree.TreePath

/**
 * 
 */
class Library {

  private var listeners: List[LibraryListener] = Nil

  val root: Category = new Category('Library, this)
  val defaultCategory = BuiltinCategoryBuilder.createBuiltins(root)

  def defaultPath: TreePath = new TreePath(Array[AnyRef](root, defaultCategory))

  def addLibraryListener(l: LibraryListener) {listeners ::= l}
  def removeLibraryListener(l: LibraryListener) {listeners = listeners filterNot(_ == l)}

  def notifyListeners(op: LibraryListener => Unit) {
    listeners foreach (l => op(l) )
  }
}

trait LibraryListener {
  def onCategoryAdded(category: Category)
  def onCategoryRemoved(category: Category)
  def onComponentAdded(category: Category, comp: Comp)
  def onComponentRemoved(category: Category, comp: Comp)
}