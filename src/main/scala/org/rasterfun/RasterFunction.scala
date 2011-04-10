package org.rasterfun

import functions.{OneFun, Fun}
import scala.xml._


object RasterFunction {

  def toXml(funcs: List[Fun]): String = {

    val sb = new StringBuilder()

    sb.append("<raster>\n")
    funcs.foreach{_.toXml(sb)}
    sb.append("</raster>")
    sb.toString
  }


  def fromXml(xml: Elem): List[Fun] = {
    Nil
  }

}
