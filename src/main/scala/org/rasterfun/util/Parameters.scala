package org.rasterfun.util

import java.awt.Color

/**
 * A set of immutable parameters, with some typed convenience getters.
 */
case class Parameters(parameters: Map[Symbol, Any]) {

  def get[T](name: Symbol, default: T): T = {
    parameters.getOrElse(name, default).asInstanceOf[T]
  }

  def getString(name: Symbol, default: String): String = {
    parameters.getOrElse(name, default).toString
  }

  def getColor(name: Symbol, default: Color): Color = {
    if (!parameters.contains(name)) default
    else {
      val c: Map[Symbol, Any] = parameters(name).asInstanceOf[Map[Symbol, Any]]
      new Color(
        c.getOrElse('r, 0f).asInstanceOf[Number].floatValue,
        c.getOrElse('g, 0f).asInstanceOf[Number].floatValue,
        c.getOrElse('b, 0f).asInstanceOf[Number].floatValue,
        c.getOrElse('a, 1f).asInstanceOf[Number].floatValue)
    }
  }

  def getSymbol(name: Symbol, default: Symbol): Symbol = {
    if (!parameters.contains(name)) default
    else {
      val value = parameters(name)
      if (value.isInstanceOf[Symbol]) value.asInstanceOf[Symbol]
      else Symbol(value.toString)
    }
  }

  def getFloat(name: Symbol, default: Float): Float = {
    parameters.getOrElse(name, default).asInstanceOf[Number].floatValue
  }

  def getInt(name: Symbol, default: Int): Int = {
    parameters.getOrElse(name, default).asInstanceOf[Int]
  }

  def chain(secondary: Parameters): Parameters = {
    new Parameters(secondary.parameters ++ parameters)
  }

  override def toString = parameters.iterator.mkString("{", ", ", "}")
}

object Parameters  {

  def apply(): Parameters = EmptyParameters

}

object EmptyParameters extends Parameters(Map())