package org.rasterfun.component

/**
 * Called when the structure of the graph changes.
 */
trait GraphStructureListener extends ((Comp) => Unit) {

  def apply(root: Comp)

}