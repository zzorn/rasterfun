package org.rasterfun

package object functions {

  implicit def float2ConstFun(v: Float): ConstFun = new ConstFun(v)
  implicit def double2ConstFun(v: Double): ConstFun = new ConstFun(v.toFloat)
}


