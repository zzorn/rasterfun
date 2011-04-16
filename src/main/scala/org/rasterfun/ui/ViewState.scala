package org.rasterfun.ui

import java.awt.Color

/**
 * 
 */
abstract class ViewState(val color: Color) {

}

case object Normal extends ViewState(Color.BLACK)
case object Selected extends ViewState(new Color(1f, 0.5f, 0.2f))
case object Source extends ViewState(new Color(0.3f, 0.8f, 1f))

