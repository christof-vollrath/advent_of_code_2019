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
        setSize(340, 400)
        isResizable = false

        add(BreakoutScreen())

        setLocationRelativeTo(null)
        isVisible = true
    }

}

private val FONT_NAME = "Arial"

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
        g.color = Color(0xcdc1b4)
        g.fillRect(0, 0, this.size.width, this.size.height)
        val font = Font(FONT_NAME, Font.BOLD, 18)
        g.color = Color(0x000000)
        g.font = font
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
/*
    private fun offsetCoors(arg: Int): Int {
        return arg * (TILES_MARGIN + TILE_SIZE) + TILES_MARGIN
    }

    private fun drawTile(g: Graphics2D, value: Int, x: Int, y: Int) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE)

        val xOffset = offsetCoors(x)
        val yOffset = offsetCoors(y)
        g.color = settings.getBackgroundColor(value)
        g.fillRoundRect(xOffset, yOffset, TILE_SIZE, TILE_SIZE, 14, 14)
        g.color = settings.getForegroundColor(value)
        val size = if (value < 100) 36 else if (value < 1000) 32 else 24
        val font = Font(FONT_NAME, Font.BOLD, size)
        g.font = font

        val s = value.toString()
        val fm = getFontMetrics(font)

        val w = fm.stringWidth(s)
        val h = -fm.getLineMetrics(s, g).baselineOffsets[2].toInt()

        if (value != 0)
            g.drawString(s, xOffset + (TILE_SIZE - w) / 2, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 - 2)

        if (game.hasWon() || game.canMove() == false) {
            g.color = Color(255, 255, 255, 30)
            g.fillRect(0, 0, width, height)
            g.color = Color(78, 139, 202)
            g.font = Font(FONT_NAME, Font.BOLD, 48)
            if (game.hasWon()) {
                g.drawString("You won!", 68, 150)
            }
            if (!game.canMove()) {
                g.drawString("Game over!", 45, 160)
            }
        }
        g.font = Font(FONT_NAME, Font.PLAIN, 18)
    }

 */
}

