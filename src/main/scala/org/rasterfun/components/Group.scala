package org.rasterfun.components

import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._

import org.rasterfun.component.Component

/**
 * A group component.
 * Provides modularity to designs, and allows users to create custom components.
 */
// TODO: Allow users to add properties to this component with selectable editors, and bind contained component properties to them
// TODO: Allow adding inputs
class Group extends Component {

  private var _components: List[Component] = Nil
  private var _root: Component = null

  def components = _components
  def root = _root

  def this(root: Component, components: List[Component]) {
    this()
    _components = components
    _root = root
  }

  def addComponent(component: Component) {
    require(component != null, "Should not be null")
    require(!_components.contains(component), "Should not already be contaiend")
    _components ::= component
  }

  def removeComponent(component: Component) = _components = _components.filterNot( _ == component)

  def setRoot(newRoot: Component) {
    require(newRoot != null, "Root should not be null")
    require(_components.contains(newRoot), "Root should be in group")
    _root = newRoot
  }

  // Forward pixel calculations to the root.
  def rgba(pos: inVec2) = _root.rgba(pos)
  override def intensity(pos: inVec2) = _root.intensity(pos)
  override def channel(channel: Symbol, pos: Vec2) = _root.channel(channel, pos)
}