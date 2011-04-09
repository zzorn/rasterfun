package org.rasterfun.raster

/**
 * 
 */
case class Raster(width: Int, height: Int, channels: Map[Symbol, RasterChannel]) {
  channels.values foreach {c =>
    require(c.width == width, "Channel "+c+" width should be same as the raster width.")
    require(c.height == height, "Channel "+c+" height should be same as the raster height.")
  }


  def apply(channel: Symbol): Option[RasterChannel] = channels.get(channel)

}