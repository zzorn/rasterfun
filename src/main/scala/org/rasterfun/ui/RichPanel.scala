package org.rasterfun.ui

import net.miginfocom.swing.MigLayout
import javax.swing.{BorderFactory, JLabel, JPanel}
import javax.swing.border.BevelBorder

/**
 * Somewhat beefed up panel.
 */
class RichPanel(title: String = null, border: Boolean = false, constraints: String = "") extends JPanel(new MigLayout(constraints)) {
  if (title != null) add(new JLabel(title), "dock north")
  if (border) setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED))

}