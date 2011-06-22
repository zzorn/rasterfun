package org.rasterfun.util.gradient

import simplex3d.math.float._
import simplex3d.math.float.functions._

/**
 * 
 */
// TODO: Move to scalaprops or utility lib?
trait AbstractGradient {

  def apply(v: Float): Vec4

}


