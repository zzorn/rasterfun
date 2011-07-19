package org.rasterfun.geometry

/**
 * Indicates type of segment.  Used in drop down lists and the like.
 */
abstract class GuidedSegmentType(name: String, kind: Class[_]) {

  def create(): GuidedSegmentType = kind.newInstance().asInstanceOf[GuidedSegmentType]
  override def toString = name
}

object GuideSegmentType {
  val types = List[GuidedSegmentType](
    StraightSegmentType
  )
}

object StraightSegmentType extends GuidedSegmentType("Straight line", classOf[StraightSegment])

