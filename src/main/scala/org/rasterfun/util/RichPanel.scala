package org.rasterfun.util

import net.miginfocom.swing.MigLayout
import javax.swing.border.BevelBorder
import sas.swing.GradientPanel
import sas.swing.plaf.MultiLineShadowUI
import javax.swing._
import java.awt.{Rectangle, Color}
import org.rasterfun.ui.UiSettings

/**
 * Somewhat beefed up panel.
 */
class RichPanel(title: String = null,
                border: Boolean = false,
                constraints: String = "",
                align: Int = SwingConstants.LEADING,
                textColor: Color = Color.BLACK,
                textShadow: Boolean = false,
                bgColor: Color = null,
                scrollableTrackViewportHeight: Boolean = false,
                scrollableTrackViewportWidth: Boolean = false )
        extends JPanel(new MigLayout(constraints)) with Scrollable {

  if (bgColor != null) setBackground(bgColor)

  if (title != null) {
    val label = new JLabel(title, null, align)
    label.setForeground(textColor)
    if (textShadow) label.setUI(MultiLineShadowUI.labelUI)

    add(label, "dock north, growx, alignx 50%, width 100%")
  }

  if (border) setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED))

  def getScrollableTracksViewportHeight = scrollableTrackViewportHeight
  def getScrollableTracksViewportWidth = scrollableTrackViewportWidth
  def getScrollableBlockIncrement(visibleRect: Rectangle, orientation: Int, direction: Int) = UiSettings.scrollBlockSize
  def getScrollableUnitIncrement(visibleRect: Rectangle, orientation: Int, direction: Int) = UiSettings.scrollUnitSize
  def getPreferredScrollableViewportSize = getPreferredSize
}