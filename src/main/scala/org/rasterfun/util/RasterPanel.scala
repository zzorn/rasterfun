package org.rasterfun.util

import javax.swing.JPanel
import java.awt.Graphics
import java.awt.event.{ComponentAdapter, ComponentEvent}
import org.rasterfun.{Area}
import org.rasterfun.functions.{Fun}

/**
 * 
 */
class RasterPanel(initialArea: Area, red: Fun, green: Fun, blue: Fun, alpha: Fun) extends JPanel {

  private var fastImage: FastImage = null
  private var imagePainted = false
  private var area: Area = initialArea

  addComponentListener(new ComponentAdapter {
    override def componentResized(e: ComponentEvent) = {
      fastImage = new FastImage(getWidth, getHeight)
      imagePainted = false
    }
  })


  def calculateBuffer(buffer: Array[Int], width: Int, height: Int, area: Area,
                      red: Fun, green: Fun, blue: Fun, alpha: Fun) {
    require(buffer.length == width * height, "Buffer size should match width * heigth")

    def clampScale(v: Float): Int = {
      if (v < 0) 0
      else if (v > 1) 0xff
      else (v * 0xff).toInt & 0xff
    }

    val sxStep = area.width / width
    val syStep = area.height / height
    val sampleSize = (sxStep + syStep) * 0.5f

    var index = 0
    var sy = area.minY
    var y = 0
    while (y < height) {

      var sx = area.minX
      var x = 0
      while (x < width) {
        val r = clampScale(red(sx, sy, sampleSize))
        val g = clampScale(green(sx, sy, sampleSize))
        val b = clampScale(blue(sx, sy, sampleSize))
        val a = clampScale(alpha(sx, sy, sampleSize))
        buffer(index) =
                (a << 24) |
                (r << 16) |
                (g << 8) |
                (b << 0)

        sx += sxStep
        x += 1
        index += 1
      }

      sy += syStep
      y += 1
    }
  }


  override def paintComponent(g: Graphics): Unit = {
    if (!imagePainted) {

      calculateBuffer(fastImage.buffer, fastImage.width, fastImage.height, area, red, green, blue, alpha)
      imagePainted = true
    }


    fastImage.renderToGraphics(g)
  }

}