package org.rasterfun.components

import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._
import org.scalaprops.ui.editors.SliderFactory
import org.scalaprops.ui.editors.SliderFactory._
import java.util.Random
import org.rasterfun.util.{FastXorShift, SegmentCell, XorShiftRandom}

/**
 * Tile or board pattern.
 */
class Blocks extends IntensityComp {

  val tileColor = addInput('tileColor, new Noise())
  val tileWidth =  p('tileWidth, 3f).editor(new SliderFactory(0f, 4f,restrictNumberFieldMax = false,restrictNumberFieldMin = false)) onChange(updateSettings())
  val tileHeight =  p('tileHeight, 1f).editor(new SliderFactory(0f, 4f,restrictNumberFieldMax = false,restrictNumberFieldMin = false)) onChange(updateSettings())
  val tileShift =  p('tileShift, 0.5f).editor(new SliderFactory(0f, 1f,restrictNumberFieldMax = false,restrictNumberFieldMin = false)) onChange(updateSettings())
  val widthVariation =  p('widthVariation, 0.2f).editor(new SliderFactory(0f, 1f,restrictNumberFieldMax = false,restrictNumberFieldMin = false)) onChange(updateSettings())
  val heightVariation =  p('heightVariation, 0.2f).editor(new SliderFactory(0f, 1f,restrictNumberFieldMax = false,restrictNumberFieldMin = false)) onChange(updateSettings())
  val shiftVariation =  p('shiftVariation, 0.2f).editor(new SliderFactory(0f, 1f,restrictNumberFieldMax = false,restrictNumberFieldMin = false)) onChange(updateSettings())
  val seed =  p('seed, 42)
  val colorWeight =  p('colorWeight, 0.5f).editor(new SliderFactory(0f, 1f,restrictNumberFieldMax = false,restrictNumberFieldMin = false)) onChange(updateSettings())

  private val verCell = new SegmentCell()
  private val horCell = new SegmentCell()

  scale := 5f

  updateSettings()

  val r = new Random()

  private def updateSettings() {
    horCell.size = tileWidth()
    horCell.sizeVariation = widthVariation()
    verCell.size = tileHeight()
    verCell.sizeVariation = heightVariation()
  }

  override def rgba(pos: inVec2): Vec4 = {
    mix(super.rgba(pos), tileColor().rgba(blockCenter(pos)), colorWeight())
  }

  protected def basicIntensity(pos: inVec2): Float = {

    val intraCellPos = calculateIntraCellPos(pos)

    // Distance from edges
    val vertDist = 1f - abs(intraCellPos.x - 0.5f) * 2f
    val horDist  = 1f - abs(intraCellPos.y - 0.5f) * 2f
    val edgeDist = min(vertDist, horDist)

    edgeDist * 2f - 1f

//    val xId = horCell.cellId
//    val yId = verCell.cellId

/*
    r.setSeed(xId)
    r.setSeed(r.nextInt() ^ yId)
    r.setSeed(r.nextInt())
    r.nextFloat() * 2f - 1f
*/
  }

  
  private def blockCenter(pos: inVec2): Vec2 = {
    calculateIntraCellPos(projectPos(pos))
    Vec2(horCell.center, verCell.center)
  }

  private def calculateIntraCellPos(pos: inVec2): Vec2 = {
    val intraCellY = verCell.calculateCell(pos.y, seed())

    val shift = tileShift() * tileWidth() * verCell.cellId +
            (if (shiftVariation() > 0) tileWidth() * shiftVariation() * randomMinusOneToOne(verCell.cellId)
             else 0f)

    val intraCellX = horCell.calculateCell(pos.x + shift, verCell.cellId ^ seed())

    Vec2(intraCellX, intraCellY)
  }


  private def randomMinusOneToOne(s: Int): Float = {
    r.setSeed(s)
    r.nextFloat()
    r.setSeed(r.nextLong() ^ seed())
    r.nextFloat()
    r.nextFloat() * 2f - 1f
  }

}