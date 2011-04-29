package org.rasterfun

import components._
import functions._
import library.{Category, Library}
import ui.RasterfunUi
import util.{RasterPanel, SimpleFrame}

/**
 * 
 */
object Rasterfun {

  def main(args: Array[ String ])
  {

    val library = new Library()

    val ui = new RasterfunUi(library)

    ui.setModel(new Empty())

  }

}