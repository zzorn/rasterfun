package org.rasterfun.components

import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._

import org.rasterfun.component.Comp

/**
 * A group component.
 * Provides modularity to designs, and allows users to create custom components.
 */
// TODO: Allow users to add properties to this component with selectable editors, and bind contained component properties to them
// TODO: Allow adding inputs
// TODO: Make root a property?
class Group(private var _root: Comp) extends Comp {
  require(_root != null, "Root should not be null")
  
  def root = _root

  def root_=(newRoot: Comp) {
    require(newRoot != null, "Root should not be null")
    _root = newRoot
  }

  override protected def createCopy = new Group(root)

  // Forward pixel calculations to the root.
  def rgba(pos: inVec2) = _root.rgba(pos)
  override def intensity(pos: inVec2) = _root.intensity(pos)
  override def channel(channel: Symbol, pos: inVec2) = _root.channel(channel, pos)
}