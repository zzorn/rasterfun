package org.rasterfun.util.curve

import simplex3d.math.float._
import simplex3d.math.float.functions._

/**
 * A a mapping from 0..1 to 0..1.
 */
trait Projection {

  def name: String 
  override def toString = name

  def apply(v: Float): Float = {
    if (v <= 0f) 0f
    else if (v >= 1f) 1f
    else calculate(v)
  }

  protected def calculate(v: Float): Float

}

object Projection {
  val standardProjections = List(
    OneProjection,
    LinearProjection,
    SmoothProjection,
    ConvexProjection,
    ConcaveProjection
  )
}

object OneProjection extends Projection{
  def name = "Solid"
  override def apply(v: Float) = 1f
  def calculate(v: Float) = 1f
}

object LinearProjection extends Projection{
  def name = "Linear"
  def calculate(v: Float) = v
}

object SmoothProjection extends Projection {
  def name = "Smoothed"
  def calculate(v: Float) = 1f - (cos(v * Pi) * 0.5f + 0.5f)
}

object ConcaveProjection extends Projection {
  def name = "Concave"
  def calculate(v: Float) = 1f - sqrt(1f - v*v)
}

object ConvexProjection extends Projection {
  def name = "Convex"
  def calculate(v: Float) = sqrt(1f - (1f-v)*(1f-v))
}
