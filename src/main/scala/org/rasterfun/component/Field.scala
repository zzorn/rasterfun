package org.rasterfun.component

/**
 * 
 */
trait Field {
  def apply(x: Float, y: Float, sampleSize: Float): Float
}