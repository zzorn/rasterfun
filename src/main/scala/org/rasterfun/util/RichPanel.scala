package org.rasterfun.util

import net.miginfocom.swing.MigLayout
import javax.swing.border.BevelBorder
import java.awt.Color
import javax.swing.{SwingConstants, BorderFactory, JLabel, JPanel}
import sas.swing.GradientPanel
import sas.swing.plaf.MultiLineShadowUI

/**
 * Somewhat beefed up panel.
 */
class RichPanel(title: String = null,
                border: Boolean = false,
                constraints: String = "",
                align: Int = SwingConstants.LEADING,
                textColor: Color = Color.BLACK,
                textShadow: Boolean = false,
                bgColor: Color = null)
        extends JPanel(new MigLayout(constraints)) {

  if (bgColor != null) setBackground(bgColor)

  if (title != null) {
    val label = new JLabel(title, null, align)
    label.setForeground(textColor)
    if (textShadow) label.setUI(MultiLineShadowUI.labelUI)

    add(label, "dock north, growx, alignx 50%, width 100%")
  }

  if (border) setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED))

}