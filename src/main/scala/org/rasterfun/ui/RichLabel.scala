package org.rasterfun.ui

import java.awt.{Color, Graphics}
import javax.swing.text.View
import javax.swing.plaf.basic.BasicHTML
import javax.swing.{SwingConstants, ImageIcon, JLabel}

/**
 * Label that supports outline
 */
class RichLabel(text: String,
                icon: ImageIcon = null,
                horizontalAlignment: Int = SwingConstants.LEADING,
                color: Color = Color.BLACK,
                outlineColor: Color = Color.BLACK,
                outline: Boolean = false) extends JLabel(text, icon, horizontalAlignment) {

  override def paintComponent(g: Graphics) {
    if (!outline) {
      setForeground(color)
      super.paintComponent(g)
    } else {
      def paintAt(x: Int, y: Int) {
        setLocation(x, y)
        super.paintComponent(g)
      }

      val x = getX
      val y = getY

      // Paint outline
      val offset = 3
      setForeground(outlineColor)
      paintAt(x - offset, y - offset)
      paintAt(x - offset, y + offset)
      paintAt(x + offset, y - offset)
      paintAt(x + offset, y + offset)

      // Paint actual text
      setForeground(color)
      paintAt(x, y)
    }

  }
}