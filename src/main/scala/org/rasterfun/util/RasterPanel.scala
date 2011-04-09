package org.rasterfun.util

import javax.swing.JPanel
import java.awt.Graphics
import java.awt.event.{ComponentAdapter, ComponentEvent}
import org.rasterfun.{Area, RasterFunction}

/**
 * 
 */
class RasterPanel(rasterFunction: RasterFunction) extends JPanel {

  private var fastImage: FastImage = null
  private var imagePainted = false
  private var area: Area = Area(-2, -2, 4, 4)

  addComponentListener(new ComponentAdapter {
    override def componentResized(e: ComponentEvent) = {
      fastImage = new FastImage(getWidth, getHeight)
      imagePainted = false
    }
  })

  override def paintComponent(g: Graphics): Unit = {
    if (!imagePainted) {

      rasterFunction.calculateBuffer(fastImage.buffer, fastImage.width, fastImage.height, area, false)
      imagePainted = true
    }


    fastImage.renderToGraphics(g)
  }

}