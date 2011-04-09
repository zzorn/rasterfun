package org.rasterfun.raster

/**
 * 
 */
case class Raster(channels: Map[Symbol, RasterChannel]) {

  def apply(channel: Symbol): Option[RasterChannel] = channels.get(channel)

}