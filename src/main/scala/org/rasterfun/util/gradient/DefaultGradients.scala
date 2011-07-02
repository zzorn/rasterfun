package org.rasterfun.util.gradient


import simplex3d.math.float._
import simplex3d.math.float.functions._

/**
 * 
 */
object DefaultGradients {

  val BlackWhite = new Gradient(List(
    GradientPoint(0, Vec4(0,0,0,1)),
    GradientPoint(1, Vec4(1,1,1,1))
  ))

  val FancyTest = new Gradient(List(
    GradientPoint(0.0f, Vec4(0,0,0,1)),
    GradientPoint(0.3f, Vec4(0,0,1,1)),
    GradientPoint(0.5f, Vec4(1,0,0,1)),
    GradientPoint(0.6f, Vec4(1,0.5f,0,1)),
    GradientPoint(1.0f, Vec4(1,1,0.5f,1))
  ))

  val Intensity = new Gradient(List(
    GradientPoint(-100, Vec4(0,1,0,1)),
    GradientPoint(-10, Vec4(0,1,1,1)),
    GradientPoint(-1, Vec4(0,0,1,1)),
    GradientPoint(-0.0000001f, Vec4(0,0,0,1)),
    GradientPoint(0, Vec4(0,0,0,1)),
    GradientPoint(1, Vec4(1,1,1,1)),
    GradientPoint(10, Vec4(1,1,0,1)),
    GradientPoint(100, Vec4(1,0,0,1))
  ))

}