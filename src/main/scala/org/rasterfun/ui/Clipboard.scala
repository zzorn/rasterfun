package org.rasterfun.ui

import org.rasterfun.component.Comp
import org.rasterfun.components.Reference

/**
 * Stores copied components or references, and allow getting them.
 */
class Clipboard {

  private var clip: Option[Comp] = None

  /**
   * Put a copy of the specified component on the clipboard.
   */
  def putCopy(comp: Comp) {clip = Some(comp.copyTree)}

  /**
   * Put a reference to the specified component on the clipboard.
   */
  def putReference(comp: Comp) {clip = Some(new Reference(comp))}

  /**
   * Returns a copy of just the component on the clipboard (its parameter components are set to Empty), or None if the clipboard is empty.
   */
  def getComponent: Option[Comp] = clip.map(c => c.copyComponent )

  /**
   * Returns a copy of the whole tree on the clipboard (component and its parameter components), or None if the clipboard is empty.
   */
  def getTree: Option[Comp] = clip.map(c => c.copyTree )

  /**
   * Returns the whole component on the clipboard, and empties the clipboard.
   */
  def getTreeAndClear: Option[Comp] = {
    val c = clip
    clear()
    c
  }

  def isEmpty = clip.isEmpty

  def hasContent = !isEmpty

  /**
   * Empty clipboard
   */
  def clear() { clip = None}


}
