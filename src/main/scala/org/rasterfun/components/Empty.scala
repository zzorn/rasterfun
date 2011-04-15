package org.rasterfun.components

import org.rasterfun.component.Comp

import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._

/**
 * Represents an empty slot.
 */
class Empty extends Comp {

  def rgba(pos: inVec2) = Vec4(0f)
  override def intensity(pos: inVec2) = 0f
  override def channel(channel: Symbol, pos: inVec2) = 0f
}