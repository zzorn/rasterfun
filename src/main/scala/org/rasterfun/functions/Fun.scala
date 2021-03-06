package org.rasterfun.functions

import org.rasterfun.util.SimplexNoise
import java.util.HashSet
import xml.Elem

/**
 * Calculates a value at given coordinates.
 */
abstract class Fun(val parameterNames: List[Symbol], val constantNames: List[Symbol] = Nil) {
  private var parameters: Map[Symbol, Fun] = Map()
  private var constants: Map[Symbol, AnyRef] = Map()

  def setParameter(parameterName: Symbol, fun: Fun) {
    require(parameterNames contains parameterName)
    parameters += (parameterName -> fun)
  }

  def setConst(name: Symbol, value: AnyRef) {
    constants += (name -> value)
  }

  protected def get(parameter: Symbol): Fun = get(parameter, ZeroFun)
  protected def get(parameter: Symbol, default: Fun): Fun = parameters.getOrElse(parameter, default)

  def apply(x: Float, y: Float, sampleSize: Float): Float

  final def getFuncs(funcs: HashSet[Fun]) {
    if (!funcs.contains(this)) {
      funcs.add(this)
      parameters.values.foreach{ _.getFuncs(funcs)}
    }
  }

  def id: String = "" + hashCode
  def kind: String = getClass.getSimpleName

  def getParam(paramName: String): Fun = {
    getClass.getMethods.find(_.getName == paramName).get.invoke(this).asInstanceOf[Fun]
  }

  def getConst(name: String): AnyRef = {
    getClass.getMethods.find(_.getName == name).get.invoke(this).asInstanceOf[AnyRef]
  }

  def setParam(paramName: String, fun: Fun) {
    getClass.getMethods.find(_.getName == paramName + "_$eq").get.invoke(this, fun.asInstanceOf[AnyRef])
  }

  def toXml(): String = {
    val sb = new StringBuilder()
    toXml(sb)
    sb.toString
  }

  def toXml(sb: StringBuilder) {
    sb.append("  <fun id=\"")
      .append(id)
      .append("\" type=\"")
      .append(kind)
      .append("\"")

    if (parameterNames.isEmpty && constantNames.isEmpty) {
      sb.append("/>\n")
    }
    else {
      sb.append(">\n")
      constantNames foreach { name =>
        sb.append("    <const ")
          .append("name=\"").append(name.name).append("\" ")
          .append("value=\"").append(getConst(name.name).toString.replace("\"", "'")).append("\"")
          .append("/>\n")
      }

      parameterNames.foreach{ name =>
        sb.append("    <param ")
          .append("name=\"").append(name.name).append("\" ")
          .append("ref=\"").append(getParam(name.name).id).append("\"")
          .append("/>\n")
      }
      sb.append("  </fun>\n")
    }

  }
}


case object ZeroFun extends Fun(List()) {
  def apply(x: Float, y: Float, sampleSize: Float) = 0f
}

case object OneFun extends Fun(List()) {
  def apply(x: Float, y: Float, sampleSize: Float) = 1f
}

// ConstFun is special case as it takes a constant as parameter.
case class ConstFun(const: Float) extends Fun(List(), List('const)) {
  def apply(x: Float, y: Float, sampleSize: Float) = const
}


case class AddFun(a: Fun, b: Fun) extends Fun(List('a, 'b)) {
  setParameter('a, a)
  setParameter('b, b)

  def apply(x: Float, y: Float, sample: Float) = get('a)(x, y, sample) + get('b)(x, y, sample)
}

case class SubFun(a: Fun, b: Fun) extends Fun(List('a, 'b)) {
  setParameter('a, a)
  setParameter('b, b)

  def apply(x: Float, y: Float, sample: Float) = a(x, y, sample) - b(x, y, sample)
}

case class MulFun(a: Fun, b: Fun) extends Fun(List('a, 'b)) {
  setParameter('a, a)
  setParameter('b, b)

  def apply(x: Float, y: Float, sample: Float) = a(x, y, sample) * b(x, y, sample)
}

case class DivFun(a: Fun, b: Fun, divZeroValue: Float) extends Fun(List('a, 'b, 'divZeroValue)) {
  setParameter('a, a)
  setParameter('b, b)
  setParameter('divZeroValue, divZeroValue)

  def apply(x: Float, y: Float, sample: Float) = {
    val dividend = b(x, y, sample)
    if (dividend == 0) divZeroValue
    else a(x, y, sample) / dividend
  }
}

case class ModFun(a: Fun, b: Fun, divZeroValue: Float) extends Fun(List('a, 'b, 'divZeroValue)) {
  setParameter('a, a)
  setParameter('b, b)
  setParameter('divZeroValue, divZeroValue)

  def apply(x: Float, y: Float, sample: Float) = {
    val dividend = b(x, y, sample)
    if (dividend == 0) divZeroValue
    else a(x, y, sample) % dividend
  }

}



case class SinFun(a: Fun) extends Fun(List('a)) {
  setParameter('a, a)

  def apply(x: Float, y: Float, sampleSize: Float) = math.sin(a(x, y, sampleSize)).floatValue

}

case class CosFun(a: Fun) extends Fun(List('a)) {
  setParameter('a, a)

  def apply(x: Float, y: Float, sampleSize: Float) = math.cos(a(x, y, sampleSize)).floatValue

}

case class TanFun(a: Fun) extends Fun(List('a)) {
  setParameter('a, a)

  def apply(x: Float, y: Float, sampleSize: Float) = math.tan(a(x, y, sampleSize)).floatValue

}


case class LogFun(a: Fun) extends Fun(List('a)) {
  setParameter('a, a)

  def apply(x: Float, y: Float, sampleSize: Float) = math.log(a(x, y, sampleSize)).floatValue

}

case class PowFun(base: Fun, exp: Fun) extends Fun(List('base, 'exp)) {
  setParameter('base, base)
  setParameter('exp, exp)

  def apply(x: Float, y: Float, sampleSize: Float) = math.pow(base(x, y, sampleSize), exp(x, y, sampleSize)).floatValue

}


case class CeilFun(a: Fun) extends Fun(List('a)) {
  setParameter('a, a)

  def apply(x: Float, y: Float, sampleSize: Float) = math.ceil(a(x, y, sampleSize)).floatValue

}

case class FloorFun(a: Fun) extends Fun(List('a)) {
  setParameter('a, a)

  def apply(x: Float, y: Float, sampleSize: Float) = math.floor(a(x, y, sampleSize)).floatValue

}


case class MinFun(a: Fun, b: Fun) extends Fun(List('a, 'b)) {
  setParameter('a, a)
  setParameter('b, b)

  def apply(x: Float, y: Float, sampleSize: Float) = math.min(a(x, y, sampleSize), b(x, y, sampleSize))

}

case class MaxFun(a: Fun, b: Fun) extends Fun(List('a, 'b)) {
  setParameter('a, a)
  setParameter('b, b)

  def apply(x: Float, y: Float, sampleSize: Float) = math.max(a(x, y, sampleSize), b(x, y, sampleSize))

}

case class AbsFun(a: Fun) extends Fun(List('a)) {
  setParameter('a, a)

  def apply(x: Float, y: Float, sampleSize: Float) = math.abs(a(x, y, sampleSize))
}

case class SigFun(a: Fun) extends Fun(List('a)) {
  setParameter('a, a)

  def apply(x: Float, y: Float, sampleSize: Float) = math.signum(a(x, y, sampleSize))

}

case class PositiveFun(a: Fun) extends Fun(List('a)) {
  setParameter('a, a)

  def apply(x: Float, y: Float, sampleSize: Float) = if (a(x, y, sampleSize) > 0) 1f else 0f

}

case class NegFun(a: Fun) extends Fun(List('a)) {
  setParameter('a, a)

  def apply(x: Float, y: Float, sampleSize: Float) = -a(x, y, sampleSize)

}


case class SelectFun(select: Fun, a: Fun, b: Fun) extends Fun(List('select, 'a, 'b)) {
  setParameter('select, select)
  setParameter('a, a)
  setParameter('b, b)

  def apply(x: Float, y: Float, sampleSize: Float) = {
    if (select(x, y, sampleSize) <= 0) a(x, y, sampleSize)
    else b(x, y, sampleSize)
  }

}

case class MixFun(select: Fun, a: Fun, b: Fun) extends Fun(List('select, 'a, 'b)) {
  setParameter('select, select)
  setParameter('a, a)
  setParameter('b, b)

  def apply(x: Float, y: Float, sampleSize: Float) = {
    val tv = select(x, y, sampleSize)
    if (tv <= 0) a(x, y, sampleSize)
    else if (tv >= 1) b(x, y, sampleSize)
    else {
      val av = a(x, y, sampleSize)
      val bv = b(x, y, sampleSize)
      av + tv * (bv - av)
    }

  }

}

case class InterpolateFun(select: Fun, a: Fun, b: Fun) extends Fun(List('select, 'a, 'b)) {
  setParameter('select, select)
  setParameter('a, a)
  setParameter('b, b)

  def apply(x: Float, y: Float, sampleSize: Float) = {
    val tv = select(x, y, sampleSize)
    val av = a(x, y, sampleSize)
    val bv = b(x, y, sampleSize)
    av + tv * (bv - av)
  }

}



case class NoiseFun(inputScale: Fun = OneFun,
                    resultScale: Fun = OneFun,
                    resultAdd: Fun = ZeroFun,
                    offsetX: Fun = ZeroFun,
                    offsetY: Fun = ZeroFun) extends Fun(List('inputScale, 'resultScale, 'resultAdd, 'offsetX, 'offsetY)) {
  setParameter('inputScale, inputScale)
  setParameter('resultScale, resultScale)
  setParameter('resultAdd, resultAdd)
  setParameter('offsetX, offsetX)
  setParameter('offsetY, offsetY)

  def apply(x: Float, y: Float, sampleSize: Float) = {
    val s = inputScale(x, y, sampleSize)
    (SimplexNoise.noise(s * (x + offsetX(x, y, sampleSize)),
                        s * (y + offsetY(x, y, sampleSize))) *
        resultScale(x, y, sampleSize) +
        resultAdd(x, y, sampleSize)).toFloat
  }

}


case class ScaleFun(source: Fun, scale: Fun) extends Fun(List('source, 'scale)) {
  setParameter('source, source)
  setParameter('scale, scale)

  def apply(x: Float, y: Float, sampleSize: Float) = {
    val s = scale(x, y, sampleSize)
    source(x * s, y * x, sampleSize * s)
  }

}


case class OffsetFun(source: Fun, xOffs: Fun, yOffs: Fun) extends Fun(List('source, 'xOffs, 'yOffs)) {
  setParameter('source, source)
  setParameter('xOffs, xOffs)
  setParameter('yOffs, yOffs)

  def apply(x: Float, y: Float, sampleSize: Float) = {
    source(x + xOffs(x, y, sampleSize),
           y + yOffs(x, y, sampleSize), sampleSize)
  }

}

case class RotFun(source: Fun, angle: Fun) extends Fun(List('source, 'angle)) {
  setParameter('source, source)
  setParameter('angle, angle)

  def apply(x: Float, y: Float, sampleSize: Float) = {
    val a = angle(x, y, sampleSize)
    val cosA = math.cos(a).toFloat
    val sinA = math.sin(a).toFloat
    source(x * cosA - y * sinA,
           x * sinA + y * cosA,
           sampleSize)
  }

}


case class CopyFun(source: Fun, scale: Fun, angle: Fun, xPos: Fun, yPos: Fun) extends Fun(List('source, 'scale, 'angle, 'xPos, 'yPos)) {
  setParameter('source, source)
  setParameter('scale, scale)
  setParameter('angle, angle)
  setParameter('xPos, xPos)
  setParameter('yPos, yPos)

  def apply(x: Float, y: Float, sampleSize: Float) = {
    val a = angle(x, y, sampleSize)
    val cosA = math.cos(a).toFloat
    val sinA = math.sin(a).toFloat
    val sx = x - xPos(x, y, sampleSize)
    val sy = y - yPos(x, y, sampleSize)
    val s = scale(x, y, sampleSize)
    source((sx * cosA - sy * sinA) * s,
           (sx * sinA + sy * cosA) * s,
           sampleSize * s)
  }

}


case class HueFun(red: Fun, green: Fun, blue: Fun) extends Fun(List('red, 'green, 'blue)) {
  setParameter('red, red)
  setParameter('green, green)
  setParameter('blue, blue)

  def apply(x: Float, y: Float, sampleSize: Float) = {
    def clampZeroOne(v: Float): Float = (if (v < 0) 0 else if (v > 1) 1 else v)

    val r = clampZeroOne(red(x, y, sampleSize))
    val g = clampZeroOne(green(x, y, sampleSize))
    val b = clampZeroOne(blue(x, y, sampleSize))

    val max = math.max(math.max(r, g), b)
    val min = math.min(math.min(r, g), b)

    if (max == min) {
      // Greyscale
      0f
    }
    else {
      val d = max - min
      val h = if (max == r) (g - b) / d + (if (g < b) 6f else 0f)
        else if (max == g) (b - r) / d + 2f
        else if (max == b) (r - g) / d + 4f
        else 0f // NaN or similar

      h / 6f
    }
  }

}

case class SatFun(red: Fun, green: Fun, blue: Fun) extends Fun(List('red, 'green, 'blue)) {
  setParameter('red, red)
  setParameter('green, green)
  setParameter('blue, blue)

  def apply(x: Float, y: Float, sampleSize: Float) = {
    def clampZeroOne(v: Float): Float = (if (v < 0) 0 else if (v > 1) 1 else v)

    val r = clampZeroOne(red(x, y, sampleSize))
    val g = clampZeroOne(green(x, y, sampleSize))
    val b = clampZeroOne(blue(x, y, sampleSize))

    val max = math.max(math.max(r, g), b)
    val min = math.min(math.min(r, g), b)

    val l = (max + min) / 2f

    if (max == min) {
      // Greyscale
      0f
    }
    else {
      val d = max - min
      if (l > 0.5f) d / (2f - max - min) else d / (max + min)
    }
  }

}

case class LightnessFun(red: Fun, green: Fun, blue: Fun) extends Fun(List('red, 'green, 'blue)) {
  setParameter('red, red)
  setParameter('green, green)
  setParameter('blue, blue)

  def apply(x: Float, y: Float, sampleSize: Float) = {
    def clampZeroOne(v: Float): Float = (if (v < 0) 0 else if (v > 1) 1 else v)

    val r = clampZeroOne(red(x, y, sampleSize))
    val g = clampZeroOne(green(x, y, sampleSize))
    val b = clampZeroOne(blue(x, y, sampleSize))

    val max = math.max(math.max(r, g), b)
    val min = math.min(math.min(r, g), b)

    (max + min) / 2f
  }

}

case class RedFun(hue: Fun, saturation: Fun, lightness: Fun) extends Fun(List('hue, 'saturation, 'lightness)) {
  setParameter('hue, hue)
  setParameter('saturation, saturation)
  setParameter('lightness, lightness)

  def apply(x: Float, y: Float, sampleSize: Float) = {
    val h = hue(x, y, sampleSize)
    val s = saturation(x, y, sampleSize)
    val l = lightness(x, y, sampleSize)
    ColorComponentUtil.getColorComponent(h, s, l, 1f / 3f)
  }

}

case class GreenFun(hue: Fun, saturation: Fun, lightness: Fun) extends Fun(List('hue, 'saturation, 'lightness)) {
  setParameter('hue, hue)
  setParameter('saturation, saturation)
  setParameter('lightness, lightness)

  def apply(x: Float, y: Float, sampleSize: Float) = {
    val h = hue(x, y, sampleSize)
    val s = saturation(x, y, sampleSize)
    val l = lightness(x, y, sampleSize)
    ColorComponentUtil.getColorComponent(h, s, l, 0f)
  }

}

case class BlueFun(hue: Fun, saturation: Fun, lightness: Fun) extends Fun(List('hue, 'saturation, 'lightness)) {
  setParameter('hue, hue)
  setParameter('saturation, saturation)
  setParameter('lightness, lightness)

  def apply(x: Float, y: Float, sampleSize: Float) = {
    val h = hue(x, y, sampleSize)
    val s = saturation(x, y, sampleSize)
    val l = lightness(x, y, sampleSize)
    ColorComponentUtil.getColorComponent(h, s, l, -1f / 3f)
  }

}

object ColorComponentUtil {
  def getColorComponent(hue: Float, saturation: Float, lightness: Float, colorComponentHue: Float): Float = {
    def clampZeroOne(v: Float): Float = (if (v < 0) 0 else if (v > 1) 1 else v)

    var h = hue
    h = h - math.floor(h).toFloat
    if (h < 0) h += 1f

    val s = clampZeroOne(saturation)
    val l = clampZeroOne(lightness)

    if (l <= 0) {
      // Black
      0
    }
    else if (lightness >= 1) {
      // White
      1
    }
    else if (saturation == 0) {
      // Grayscale
      l
    }
    else {
      // Color 
      val q = if (lightness < 0.5f) (lightness * (1f + saturation)) else (lightness + saturation - lightness * saturation)
      val p = 2 * lightness - q
      var th = hue + colorComponentHue
      if (th < 0) th += 1
      if (th > 1) th -= 1

      val result =
        if (th < 1f / 6f) p + (q - p) * 6f * th
        else if (th < 1f / 2f) q
        else if (th < 2f / 3f) p + (q - p) * (2f / 3f - th) * 6f
        else p

      clampZeroOne(result)
    }
  }
}




