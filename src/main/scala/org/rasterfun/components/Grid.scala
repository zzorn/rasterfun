package org.rasterfun.components

import simplex3d.math.float._
import simplex3d.math.float.functions._
import org.rasterfun.component.Comp
import org.scalaprops.ui.editors.{SliderFactory, SliderEditor, SelectionEditorFactory}
import org.rasterfun.util.MathUtils
import java.util.Random
import org.rasterfun.geometry._

/**
 * A grid with lines crisscrossing it - a mesh or weave.
 */
class Grid extends Comp {

  val density = addInput('density, new Noise())
  val strength = addInput('strength, new Noise())
  val direction = addInput('direction, new Noise())
  val speed = addInput('speed, new Noise())
  val texture = addInput('texture, new SolidColor())

  val lineType = p[LineType]('lineType, StraightLine).editor(new SelectionEditorFactory[LineType](LineType.types))

  val strengthVariation = p('strengthVariation, 0.2f).editor(new SliderFactory[Float](0, 2))
  val directionVariation = p('directionVariation, 0.2f).editor(new SliderFactory[Float](0, 2))
  val speedVariation = p('speedVariation, 0.2f).editor(new SliderFactory[Float](0, 2))

  val gridSize = p('gridSize, 1f).editor(new SliderFactory[Float](0, 10))
  val gridProportions = p('gridProportions, 1f).editor(new SliderFactory[Float](0, 1))

  val maxDensity = p('maxDensity, 4)

  private val random = new Random()
  private val lineStart = new PointData
  private val lineEnd   = new PointData
  private val pixelData: LinePixelData = new LinePixelData()
  private val pd: LinePixelData = new LinePixelData()

  private def randVariation(a: Int, b: Int, variation: Float): Float = {
    random.setSeed(a)
    random.nextLong()
    random.setSeed(random.nextLong() ^ b)
    random.nextLong()
    (random.nextGaussian() * variation).toFloat
  }

  private def initRand(a: Int, b: Int): Random = {
    random.setSeed(a)
    random.nextLong()
    random.setSeed(random.nextLong() ^ b)
    random.nextLong()
    random
  }

  def calculatePixel(pos: inVec2, pixelData: LinePixelData) {
    // Determine the grid this point is in
    val gridWidth = gridSize() * (0f + gridProportions())
    val gridHeight = gridSize() * (1f - gridProportions())
    val cellX = if (gridWidth == 0) 0 else floor(pos.x / gridWidth).toInt
    val cellY = if (gridHeight == 0) 0 else floor(pos.y / gridHeight).toInt
    val topEdge     = Vec2((cellX - 0.5f) * gridWidth, cellY * gridHeight)
    val bottomEdge  = Vec2((cellX + 0.5f) * gridWidth, cellY * gridHeight)
    val leftEdge    = Vec2(cellX * gridWidth, (cellY - 0.5f) * gridHeight)
    val rightEdge   = Vec2(cellX * gridWidth, (cellY + 0.5f) * gridHeight)
    val maxLines: Int = maxDensity()

    // Determine the connections along the edges
    val leftDensity = density().intensity(leftEdge)
    val rightDensity = density().intensity(rightEdge)
    val topDensity = density().intensity(topEdge)
    val bottomDensity = density().intensity(bottomEdge)

    // Randomize the lines crossing the grid
    initRand(cellX, cellY)
    val topLinesCount = clamp(round(random.nextFloat() * topDensity).toInt, 0, maxLines)

    // Pick color & other data from closest line

    // Loop through lines
    // TODO: Calculate line start and end for each line
    var closestDistance = Math.POS_INF_FLOAT
    lineType().calculatePixel(pos, lineStart, lineEnd, pd)
    if (pixelData.distance < closestDistance) {
      closestDistance = pixelData.distance
      pixelData.setFrom(pd)
    }
  }

  def rgba(pos: inVec2) {
    calculatePixel(pos, pixelData)

    // TODO: Get color from texture
    Vec4(pixelData.along, pixelData.sideways, 0, 1f)
  }
}
