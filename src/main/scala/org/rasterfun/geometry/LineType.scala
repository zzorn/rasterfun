package org.rasterfun.geometry

import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._
import org.rasterfun.util.MathUtils

/**
 * Non-freehand segment, which path is rendered using some function.
 */
// TODO: Calculate all parameters with one call and return them in a writable case class or such.
trait LineType {

  def name: String = getClass.getSimpleName

  def calculatePixel(pos: inVec2, start: PointData, end: PointData, outData: LinePixelData)

  def speed     (pos: inVec2, start: PointData, end: PointData): Float
  def strength  (pos: inVec2, start: PointData, end: PointData): Float
  def sideways  (pos: inVec2, start: PointData, end: PointData): Float
  def along     (pos: inVec2, start: PointData, end: PointData): Float
  def direction (pos: inVec2, start: PointData, end: PointData): Float
  def distance  (pos: inVec2, start: PointData, end: PointData): Float = abs(sideways(pos, start, end))

  override def toString = name
}

object LineType {
  def types: List[LineType] = List(StraightLine)
}
