package org.rasterfun.components

import org.rasterfun.component.Comp

import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._

/**
 * A component that is a reference to another component.
 */
class Reference(_referred: Comp) extends Comp {
  require(_referred != null, "Referred component should not be null")
  require(!_referred.isInstanceOf[Reference], "Referred component should not be a reference")
  // TODO: Check for cycles

  name.bind(_referred.name, {s => "Ref. " + s})

  def referred = _referred

  override protected def createCopy = new Reference(referred)

  // Forward pixel calculations to the referred component.
  def rgba(pos: inVec2) = _referred.rgba(pos)
  override def intensity(pos: inVec2) = _referred.intensity(pos)
  override def channel(channel: Symbol, pos: inVec2) = _referred.channel(channel, pos)
}