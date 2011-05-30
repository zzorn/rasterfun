package org.rasterfun.components

import org.rasterfun.component.Comp
import simplex3d.math.float.functions._
import simplex3d.math.float._

/**
 * Displaces one source using two other sources as x and y displacement.
 */
class Displace() extends Comp {

  val source     = addInput('source, new Radial())
  val xDisplace  = addInput('xDisplace, new Noise())
  val yDisplace  = addInput('yDisplace, new Noise())

  override def channels = source().channels

  def rgba(pos: inVec2): Vec4 = {
    source().rgba(sourcePos(pos))
  }

  override def intensity(pos: inVec2): Float = {
    source().intensity(sourcePos(pos))
  }

  override def channel(channel: Symbol, pos: inVec2): Float = {
    source().channel(channel, sourcePos(pos))
  }

  private def sourcePos(pos: inVec2): Vec2 = {
    val xd: Float = xDisplace().intensity(pos)
    val yd: Float = yDisplace().intensity(pos)
    pos + Vec2(xd, yd)
  }

}