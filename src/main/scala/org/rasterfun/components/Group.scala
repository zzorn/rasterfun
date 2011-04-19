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
class Group(initialResult: Comp = new Empty()) extends Comp {
  require(initialResult != null, "Result should not be null")

  val result = addInput('result, initialResult)
  
  override protected def createCopy = new Group()

  // Forward pixel calculations to the root.
  def rgba(pos: inVec2) = result().rgba(pos)
  override def intensity(pos: inVec2) = result().intensity(pos)
  override def channel(channel: Symbol, pos: inVec2) = result().channel(channel, pos)
}