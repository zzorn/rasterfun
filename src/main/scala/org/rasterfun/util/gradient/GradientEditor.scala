package org.rasterfun.util.gradient

import simplex3d.math.float._
import simplex3d.math.float.functions._

import org.scalaprops.Property
import org.scalaprops.ui.{EditorFactory, Editor}
import java.awt._
import event.{MouseWheelEvent, InputEvent, MouseEvent, MouseAdapter}
import javax.swing.{SwingUtilities, JPanel}
import sun.swing.SwingUtilities2
import com.sun.java.swing.SwingUtilities3
import org.rasterfun.util.gradient.Gradient._

/**
 * Interactive editor for gradients.
 */
// TODO: Interaction: click to add new midpoint,
// click color tab to open color selector, drag color tab to move it,
// drag color tab away to remove it.
// TODO: Support for larger intervals than 0..1?  Useful e.g. for terrain or -1..1.
class GradientEditor() extends Editor[Gradient] {

  var backgroundColor: Color = Color.GRAY
  var borderColor: Color = Color.BLACK
  var pointSize: Int = 24

  private val view = new GradientView()
  private val markersView = new MarkersView()

  buildUi()

  def gradient: Gradient = view.gradient
  def gradient_= (g: Gradient) {
    view.gradient = g
    onEditorChange(g)
  }

  private def buildUi() {
    setPreferredSize(new Dimension(128, 32))
    setLayout(new BorderLayout())
    add(view, BorderLayout.CENTER)
    add(markersView, BorderLayout.SOUTH)

    markersView.setPreferredSize(new Dimension(128, 24))
  }

  protected def onInit(initialValue: Gradient, name: String) {
    view.gradient = initialValue
    repaint()
  }

  protected def onExternalValueChange(oldValue: Gradient, newValue: Gradient) {
    view.gradient = newValue
    repaint()
  }

  def isLeftMousePressed(e: MouseEvent): Boolean = {
    (e.getModifiersEx & InputEvent.BUTTON1_DOWN_MASK) != 0
  }

  def isRightMousePressed(e: MouseEvent): Boolean = {
    (e.getModifiersEx & InputEvent.BUTTON3_DOWN_MASK) != 0
  }

  def isShiftPressed(e: MouseEvent): Boolean = {
    (e.getModifiersEx & InputEvent.SHIFT_DOWN_MASK) != 0
  }

  def isControlPressed(e: MouseEvent): Boolean = {
    (e.getModifiersEx & InputEvent.CTRL_DOWN_MASK) != 0
  }

  class MarkersView extends JPanel {

    private var dragActive = false
    private var originalMarker: GradientPoint = null
    private var draggedMarker: GradientPoint = null

    private def getMarkerAtPos(e: MouseEvent): GradientPoint = {
      val relativePos = 1.0f * e.getX / getWidth
      val closestMarker: GradientPoint = gradient.getClosestPoint(relativePos)
      if (closestMarker != null &&
          abs(closestMarker.value - relativePos) * getWidth <= pointSize/1.5f) {
        closestMarker
      }
      else null
    }

    private val listener = new MouseAdapter() {
      override def mouseDragged(e: MouseEvent) {
        val relativePos = 1.0f * e.getX / getWidth

        if (!dragActive && isLeftMousePressed(e) && !isRightMousePressed(e)) {
          dragActive = true
          val markerAtPos = getMarkerAtPos(e)
          if (markerAtPos != null) {
            // Start dragging
            originalMarker = markerAtPos
            draggedMarker = markerAtPos
          }
        }

        // Move dragged point
        if (dragActive && draggedMarker != null) {
          gradient -= draggedMarker

          // Cancel the drag it is dragged to the left
          if (e.getX < 0) {
            draggedMarker = originalMarker
            gradient += originalMarker
          }
          else if (e.getX >= getWidth || e.getY > getHeight) {
            // Hide the marker if it is dragged to the right or down
          } else {
            // Update drag marker
            draggedMarker = draggedMarker.newValue(relativePos)
            gradient += draggedMarker
          }

          repaint()
        }
      }

      override def mouseClicked(e: MouseEvent) {
        if (!dragActive &&
            SwingUtilities.isLeftMouseButton(e) &&
            getMarkerAtPos(e) == null) {
          // Add new point on click
          val relativePos = 1.0f * e.getX / getWidth
          val color = gradient(relativePos)
          gradient += GradientPoint(relativePos, color)
          repaint()

        }
      }

      override def mouseReleased(e: MouseEvent) {
        if (dragActive) {
          if (SwingUtilities.isLeftMouseButton(e) && !isRightMousePressed(e)) {
            // Stop drag
            originalMarker = null
            draggedMarker = null
            dragActive = false
            repaint()
          }
          else if (SwingUtilities.isRightMouseButton(e) && !isLeftMousePressed(e)) {
            // Cancel drag
            gradient -= draggedMarker
            gradient += originalMarker
            originalMarker = null
            draggedMarker = null
            dragActive = false
            repaint()
          }
        }
      }

      override def mouseWheelMoved(e: MouseWheelEvent) {
        // Change the color with the wheel and modifiers

        val change = -e.getWheelRotation / 16f

        val marker: GradientPoint = getMarkerAtPos(e)

        if (marker != null && change != 0) {

          gradient -= marker
          gradient += (if (isControlPressed(e)) {
            marker.newAdjustedHue(change / 2f)
          } else if (isShiftPressed(e)) {
            marker.newAdjustedSat(change)
          } else  {
            marker.newAdjustedLum(change)
          })
        }

      }
    }

    addMouseListener(listener)
    addMouseMotionListener(listener)
    addMouseWheelListener(listener)

    override def paintComponent(g: Graphics) {
      val g2 = g.asInstanceOf[Graphics2D]

      // Paint bg
      g2.setColor(backgroundColor)
      g2.fillRect(0,0,getWidth, getHeight)

      // Paint control points
      gradient.controlPoints foreach {gp: GradientPoint =>
        val cx = (gp.value * getWidth).toInt
        val x1 = cx - pointSize / 2
        val y1 = 0

        g2.setColor(gp.solidJavaColor)
        g2.fillRect(x1, y1, pointSize, pointSize)

        g2.setColor(borderColor)
        g2.drawRect(x1, y1, pointSize, pointSize)
      }
    }

  }
}


object GradientEditorFactory extends EditorFactory[Gradient] {

  protected def createEditorInstance = new GradientEditor()

}

