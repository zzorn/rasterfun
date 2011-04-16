package org.rasterfun.library

import org.rasterfun.component.Comp

/**
 * 
 */
class Library {

  private var listeners: List[LibraryListener] = Nil

  val root: Category = new Category('Library, this)
  BuiltinCategoryBuilder.createBuiltins(root)

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