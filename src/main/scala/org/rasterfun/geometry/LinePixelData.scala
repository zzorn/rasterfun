package org.rasterfun.geometry

import java.lang.Float

/**
 * Mutable data holder
 */
case class LinePixelData(var strength: Float = 1f,
                         var speed: Float = 1f,
                         var along: Float = 0f,
                         var sideways: Float = 0f,
                         var direction: Float = 0f,
                         var distance: Float = 0f) {

}