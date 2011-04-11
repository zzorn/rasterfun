package org.rasterfun.component

/**
 * Depicts the range of allowed input values.
 */
case class ValueRange(min: Float, max: Float)

case object AllValues extends ValueRange(Float.NegativeInfinity, Float.PositiveInfinity)
case object Positive extends ValueRange(0, Float.PositiveInfinity)
case object ZeroToOne extends ValueRange(0, 1)
case object MinusOneToOne extends ValueRange(-1, 1)