import java.awt.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants

fun main(args: Array<String>) {
    with(JFrame()) {
        title = "Breakout"
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        setSize(964, 600)
        isResizable = false

        add(BreakoutScreen())

        setLocationRelativeTo(null)
        isVisible = true
    }

}

const val FONT_NAME = "Arial"
const val BACKGROUND_COLOR = 0x010203
const val FONT_COLOR = 0xfdcfbf
const val SCORE_FONT_COLOR = 0xfefef2
const val CELL_SIZE = 10
const val CELL_MARGIN = 2

val scoreFont = Font(FONT_NAME, Font.BOLD, 18)
val boardFont = Font(FONT_NAME, Font.BOLD, 12)
val scoreFontColor = Color(SCORE_FONT_COLOR)
val boardFontColor = Color(FONT_COLOR)
val backgroundColor = Color(BACKGROUND_COLOR)

class BreakoutScreen() : JPanel() {
    init {
        isFocusable = true
        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                val input = when (e.keyCode) {
                    KeyEvent.VK_LEFT -> -1
                    KeyEvent.VK_RIGHT -> 1
                    KeyEvent.VK_SPACE -> 0
                    else -> null
                }
                println(input)
                repaint() // TODO move repaint in handling output of engine
            }
        })
    }

    override fun paint(g: Graphics) {
        super.paint(g)
        g.color = backgroundColor
        g.fillRect(0, 0, this.size.width, this.size.height)
        g.color = scoreFontColor
        g.font = scoreFont
        val fm = getFontMetrics(font)
        val string = "Score: 45"
        val y = -fm.getLineMetrics(string, g).baselineOffsets[2].toInt()
        g.drawString("Score:", 2, y + 2)
        for (y in 0..40) {
            for (x in 0..79) {
                drawCell(g as Graphics2D, 0, x, y)
            }
        }

    }

    fun offsetCoors(arg: Int): Int {
        return arg * (CELL_MARGIN + CELL_SIZE) + CELL_MARGIN
    }

    fun drawCell(g: Graphics2D, value: Int, x: Int, y: Int) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE)

        val xOffset = offsetCoors(x)
        val yOffset = offsetCoors(y) + 24
        g.color = boardFontColor
        g.font = boardFont
        val s = value.toString()
        val fm = getFontMetrics(font)
        val w = fm.stringWidth(s)
        val h = -fm.getLineMetrics(s, g).baselineOffsets[2].toInt()

        g.drawString(s, xOffset + (CELL_SIZE - w) / 2, yOffset + CELL_SIZE - (CELL_SIZE - h) / 2 - 2)
    }
}

