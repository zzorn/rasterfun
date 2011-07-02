package org.rasterfun.components

import org.rasterfun.component.Comp

import simplex3d.math._
import floatx.Vec2f
import simplex3d.math.float._
import simplex3d.math.float.functions._

/**
 * 
 */
class HeightToNormalMap extends Comp {

  val heightMap = addInput('heightMap, new Noise())

  val sampleRadius = p ('sampleRadius, 0.01f)
  val roughness = p ('roughness, 3f)


//  override def channel(channel: Symbol, pos: inVec2): Float = {

  //}

  def rgba(pos: inVec2): Vec4 = {

    val xSampleRadius: Vec2f = Vec2(sampleRadius(), 0)
    val ySampleRadius: Vec2f = Vec2(0, sampleRadius())
    val xd = heightMap().intensity(pos - xSampleRadius) -
             heightMap().intensity(pos + xSampleRadius)
    val yd = heightMap().intensity(pos - ySampleRadius) -
             heightMap().intensity(pos + ySampleRadius)
    val zd = roughness() * 2 * sampleRadius()

    val normal = normalize(Vec3(xd, yd, zd))

    Vec4(normal.xyz, 1f)
  }
}