package org.rasterfun.geometry


/**
 * Mutable data holder
 */
class LinePixelData(var strength: Float = 1f,
                         var speed: Float = 1f,
                         var along: Float = 0f,
                         var sideways: Float = 0f,
                         var direction: Float = 0f,
                         var distance: Float = 0f) {

  def setFrom(source: LinePixelData) {
    strength = source.strength
    speed = source.speed
    along = source.along
    sideways = source.sideways
    direction = source.direction
    distance = source.distance
  }

}