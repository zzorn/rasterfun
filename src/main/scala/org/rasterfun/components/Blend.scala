package org.rasterfun.components

import org.rasterfun.component.Comp
import simplex3d.math.float.functions._
import simplex3d.math.float._

/**
 * Blends between two components using a third.
 */
class Blend extends Comp {

  val background = addInput('background)
  val foreground = addInput('foreground)
  val selector   = addInput('selector)
  val clamp = p('clamp, false)

  override def channels = background().channels union foreground().channels

  def rgba(pos: inVec2): Vec4 = {
    val t: Float = selector().intensity(pos)
    val c = clamp()
    if (c && t <= 0f)
      background().rgba(pos)
    else if (c &&t >= 1f)
      foreground().rgba(pos)
    else mix(
      background().rgba(pos),
      foreground().rgba(pos),
      t)
  }

  override def intensity(pos: inVec2): Float = {
    val t: Float = selector().intensity(pos)
    val c = clamp()
    if (c && t <= 0f)
      background().intensity(pos)
    else if (c && t >= 1f)
      foreground().intensity(pos)
    else mix(
      background().intensity(pos),
      foreground().intensity(pos),
      t)
  }

  override def channel(channel: Symbol, pos: inVec2): Float = {
    val t: Float = selector().intensity(pos)
    val c = clamp()
    if (c && t <= 0f)
      background().channel(channel, pos)
    else if (c && t >= 1f)
      foreground().channel(channel, pos)
    else mix(
      background().channel(channel, pos),
      foreground().channel(channel, pos),
      t)
  }
}