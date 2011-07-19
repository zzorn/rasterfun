package org.rasterfun.components

import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._
import org.rasterfun.component.Comp
import org.rasterfun.geometry.{GuideSegmentType, StraightSegmentType, GuidedSegmentType}
import org.scalaprops.ui.editors.{SliderFactory, SliderEditor, SelectionEditorFactory}

/**
 * A grid with lines crisscrossing it.
 */
class Grid extends Comp {

  val density = addInput('density, new Noise())
  val strength = addInput('strength, new Noise())
  val direction = addInput('direction, new Noise())
  val texture = addInput('texture, new SolidColor())

  val segmentType = p[GuidedSegmentType]('segmentType, StraightSegmentType).editor(new SelectionEditorFactory[GuidedSegmentType](GuideSegmentType.types))

  val strengthVariation = p('strengthVariation, 0.2f)
  val directionVariation = p('directionVariation, 0.5f)

  val gridSize = p('gridSize, 1f).editor(new SliderFactory[Float](0, 10))
  val gridProportions = p('gridProportions, 1f).editor(new SliderFactory[Float](0, 1))

  def rgba(pos: inVec2): Vec4 = {
    // Determine the grid this point is in
    val gridWidth = gridSize() * (0f + gridProportions())
    val gridHeight = gridSize() * (1f - gridProportions())
    val cellX = if (gridWidth == 0) 0 else floor(pos.x / gridWidth).toInt
    val cellY = if (gridHeight == 0) 0 else floor(pos.y / gridHeight).toInt

    // Determine the connections along the edges

    // Randomize the lines crossing the grid

    // Pick color & other data from closest line

    Vec4(0, 0, 0, 0)
  }
  
}