package org.rasterfun.util.ui

import java.awt.event._
import java.awt._
import org.scalaprops.utils.{MathUtils, GraphicsUtils}
import simplex3d.math.float._
import simplex3d.math.floatx.functions._
import javax.swing.{JLabel, BorderFactory}

/**
 *
 */
// TODO: Split various UI components into own library
class Slider(start: Double = 0,
             end: Double = 1,
             changeListener: (Double, Double) => Unit = null,
             backgroundPainter: SliderBackgroundPainter = DefaultSliderBackgroundPainter)
        extends Editor[Double] {

  private val WHEEL_STEP = 0.05f
  private val SIZE = 32
  private val DEFAULT_HEIGHT: Int = 24

  private val darkColor: Color = new Color( 0.25f, 0.25f, 0.25f )
  private val mediumColor: Color = new Color( 0.75f, 0.75f, 0.75f)
  private val lightColor: Color = new Color( 1f,1f,1f )

  private var relativePosition: Double = 0.0;

  final override def paintComponent(g: Graphics) {

    // Paint background
    if (backgroundPainter != null && !(getWidth == 0 || getHeight== 0)) {
      backgroundPainter.paint(g.asInstanceOf[Graphics2D], getWidth, getHeight, relativePosition, isVertical)
    }
    else super.paintComponent(g)

    // Paint current location indicator
    GraphicsUtils.paintIndicator(g.asInstanceOf[Graphics2D],
                                 getWidth,
                                 getHeight,
                                 relativePosition,
                                 isVertical,
                                 darkColor,
                                 mediumColor,
                                 lightColor)

  }

  protected override def buildUi(initialValue: Double, uiValueChangeListener: ( Double ) => Unit) {
    setPreferredSize(new Dimension(SIZE, DEFAULT_HEIGHT))
    setMinimumSize(new Dimension(DEFAULT_HEIGHT,DEFAULT_HEIGHT))
    setMaximumSize(new Dimension(10000, 10000))
    setBorder(BorderFactory.createLineBorder(Color.BLACK, 1))

    addMouseListener(mouseUpdateListener)
    addMouseMotionListener(mouseUpdateListener)
    addMouseWheelListener(mouseUpdateListener)

    if (changeListener != null) addListener(changeListener)

    repaint()
  }

  protected override def updateUi(newValue: Double) {
    updateSliderUi(newValue)
    repaint()
  }

  override protected def initialValue = 0.0

  private val mouseUpdateListener = new MouseAdapter() {
    override def mousePressed(e: MouseEvent) {updatePosition(e)}
    override def mouseReleased(e: MouseEvent) {updatePosition(e)}
    override def mouseDragged(e: MouseEvent) {updatePosition(e)}
    override def mouseWheelMoved(e: MouseWheelEvent) {
      val amount = -e.getWheelRotation
      relativePosition = MathUtils.clampToZeroToOne(relativePosition + WHEEL_STEP * amount)
      repaint()
      val value = sliderUiToValue
      updateSliderUi(value)
      onUiChange(value)
    }
  }

  private def isVertical = false

  private def updatePosition(e: MouseEvent) {
    val x = e.getX
    val y = e.getY

    if (isVertical) relativePosition = 1.0f - (1.0f * y) / (1.0f * getHeight)
    else            relativePosition = (1.0f * x) / (1.0f * getWidth)

    relativePosition = MathUtils.clampToZeroToOne(relativePosition)

    repaint()
    val value = sliderUiToValue
    
    onUiChange(value)
    updateSliderUi(value)
  }

  private def updateSliderUi(d: Double) {
    val r = if (end == start) 0.5
            else (d - start) / (end - start)
    if (r != relativePosition) {
      relativePosition = r
      repaint()
    }
  }

  private def sliderUiToValue: Double = {
    start + (end - start) * relativePosition
  }

}


