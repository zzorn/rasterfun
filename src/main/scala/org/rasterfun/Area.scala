package org.rasterfun

import simplex3d.math.Vec2i

/**
 * 
 */
case class Area(x: Float, y: Float, width: Float, height: Float) {

  require(width > 0, "Width should be positive")
  require(height > 0, "Height should be positive")

  def minX = x
  def minY = y
  def maxX = x + width
  def maxY = y + height

}