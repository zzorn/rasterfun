package org.rasterfun.util.coloreditor

import simplex3d.math.float._
import simplex3d.math.float.functions._
import org.scalaprops.ui.{EditorFactory, Editor}
import org.rasterfun.util.ColorUtils
import net.miginfocom.swing.MigLayout
import org.rasterfun.util.ui.{Slider, SliderBgLinePainter}
import javax.swing.{JPanel, JLabel}
import java.awt.event.{MouseEvent, MouseAdapter}
import java.awt._

class ColorEditorFactory(includeAlpha: Boolean) extends EditorFactory[Vec4] {
  protected def createEditorInstance = new ColorEditor(includeAlpha)
}

/**
 * 
 */
// TODO: Make the scalaprops slider UI more easy to reuse, and move the UI part of it to own library
class ColorEditor(includeAlpha: Boolean = true) extends Editor[Vec4] {

  private val hueBackgroundPainter = new SliderBgLinePainter(
    ColorUtils.HSLtoRGB(_, 1f, 0.5f, 1f))

  private val satBackgroundPainter = new SliderBgLinePainter(
    ColorUtils.HSLtoRGB(currentHue, _, 0.5f, 1f))

  private val lumBackgroundPainter = new SliderBgLinePainter(
    ColorUtils.HSLtoRGB(currentHue, 1f, _, 1f))

  private val light = Vec4(0.6f, 0.6f, 0.6f, 1f)
  private val dark  = Vec4(0.3f, 0.3f, 0.3f, 1f)
  private val alphaBackgroundPainter = new SliderBgLinePainter(
      mix(light, value, _),
      mix(dark, value, _))

  private val hueEditor   = new Slider(0, 1, updateValue _, hueBackgroundPainter)
  private val satEditor   = new Slider(0, 1, updateValue _, satBackgroundPainter)
  private val lumEditor   = new Slider(0, 1, updateValue _, lumBackgroundPainter)
  private val alphaEditor = new Slider(0, 1, updateValue _, alphaBackgroundPainter)

  private val SliderHeight: Int = 24

  private val preview = new JPanel() {
    val correctSize: Dimension = new Dimension(( SliderHeight * 1.68 ).toInt, SliderHeight)
    setPreferredSize(correctSize)
    setMaximumSize(correctSize)
    setMinimumSize(correctSize)

    override def paintComponent(p1: Graphics) {
      ColorUtils.paintColorPreview(value, p1, getWidth, getHeight, SliderHeight / 2)
      p1.setColor(Color.BLACK)
      p1.drawRect(0,0,getWidth - 1, getHeight - 1)
    }
  }

  private val editorPanel = new JPanel()
  private var editorPanelShown = false

  private def currentHue: Float = hueEditor.value.toFloat

  def buildUi(name: String) {
    setLayout(new MigLayout("wrap 1, fillx, filly, insets 2 0 2 0","[grow]","0[]0[]0"))
    setPreferredSize(new Dimension(200, SliderHeight))

    val namePanel = new JPanel(new BorderLayout())
    namePanel.setPreferredSize(new Dimension(200, SliderHeight))
    val label = new JLabel(name)
    namePanel.add(label, BorderLayout.CENTER)
    preview.setPreferredSize(new Dimension(2 * SliderHeight, SliderHeight))
    namePanel.add(preview, BorderLayout.EAST)
    namePanel.addMouseListener(new MouseAdapter() {
      override def mouseClicked(p1: MouseEvent) {
        toggleEditorPanel()
        println("editor panel "+ editorPanelShown)
      }
    })

    editorPanel.setLayout(new MigLayout("wrap 1, fillx, filly, insets 2 8 8 0","[grow]","0[]0[]0"))
    editorPanel.setPreferredSize(new Dimension(200, SliderHeight*(if (includeAlpha) 4 else 3)))

    hueEditor.init()
    lumEditor.init()
    satEditor.init()
    alphaEditor.init()

    editorPanel.add(hueEditor, "width 100%, height 100%")
    editorPanel.add(satEditor, "width 100%, height 100%")
    editorPanel.add(lumEditor, "width 100%, height 100%")
    if (includeAlpha) editorPanel.add(alphaEditor, "width 100%, height 100%")

    add(namePanel, "width 100%, height 100%")

    if (editorPanelShown) add(editorPanel, "width 100%, height 100%")

    repaint()
  }

  private def toggleEditorPanel() {
    editorPanelShown = !editorPanelShown
    if (editorPanelShown) add(editorPanel, "width 100%, height 100%")
    else remove(editorPanel)
    revalidate()
  }

  private def updateValue(oldValue: Double, newValue: Double) {
    val color = ColorUtils.HSLtoRGB(hueEditor.value.toFloat,
                                    satEditor.value.toFloat,
                                    lumEditor.value.toFloat,
                                    alphaEditor.value.toFloat)

    repaint()

    onEditorChange(color)
  }

  private def updateUi(newValue: Vec4) {
    hueEditor.value = ColorUtils.hue(newValue)
    satEditor.value = ColorUtils.saturation(newValue)
    lumEditor.value = ColorUtils.lightness(newValue)
    alphaEditor.value = newValue.a

    repaint()
  }

  protected def onInit(initialValue: Vec4, name: String) {
    buildUi(name)
    updateUi(initialValue)
  }

  protected def onExternalValueChange(oldValue: Vec4, newValue: Vec4) {
    updateUi(newValue)
  }

}
