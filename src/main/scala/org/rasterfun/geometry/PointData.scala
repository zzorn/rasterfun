package org.rasterfun.geometry

import simplex3d.math.float._

/**
 * Data for a segment guide point
 */
case class PointData(var pos: Vec2 = Vec2(0f,0f),
                     var strength: Float = 1f,
                     var direction : Float = 0f,
                     var speed: Float = 1f)
