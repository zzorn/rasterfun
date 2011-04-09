package org.rasterfun

import functions.Fun
import raster.{RasterChannel, Raster}

/**
 * Function that can calculate a raster.
 */
case class RasterFunction(channels: Map[Symbol, Fun]) {

  def calculateValue(channel: Symbol, x: Float, y: Float, sampleSize: Float): Float = {
    channels.get(channel) match {
      case Some(ch) => ch.apply(x, y, sampleSize)
      case None => 0f
    }
  }

  def calculateArea(area: Area, width: Int, height: Int): Raster = {
    require(width  > 0, "Width should be positive")
    require(height > 0, "Height should be positive")

    val calculatedRasterChannels = channels.mapValues{field =>
      new RasterChannel(width, height, field, area)
    }

    new Raster(calculatedRasterChannels)
  }
}