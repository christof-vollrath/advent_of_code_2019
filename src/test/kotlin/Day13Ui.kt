import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.awt.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants

const val CELL_SIZE = 10
const val CELL_MARGIN = 2

val scoreFont = Font("Arial", Font.BOLD, 18)
val boardFont = Font("Arial", Font.BOLD, 12)
val scoreFontColor = Color(0xfefef2)
val boardFontColor = Color(0xfdcfbf)
val backgroundColor = Color(0x010203)

fun main(args: Array<String>) {
    val intCodesString = readResource("day13Input.txt")!!
    val intCodes = parseIntCodes09(intCodesString)
    val patchedIntCodes = listOf(2L) + intCodes.drop(1) // Patch so that no coins must be inserted
    val inputChannel = Channel<Long>()
    val outputChannel = Channel<Long>()

    val game = GameScreen()
    val ui = createUi(game, inputChannel)
    runBlocking {
        async {
            patchedIntCodes.executeExtendedIntCodes09Async(inputChannel, outputChannel)
            game.terminated = true
            println("terminated = true")
        }
        async {
            while(!game.terminated) {
                val x = outputChannel.receive().toInt()
                val y = outputChannel.receive().toInt()
                val code = outputChannel.receive().toInt()
                if (x == -1 && y == 0) {
                    game.score += code
                } else game.draw(Coord2(x, y), code)
                ui.repaint()
            }
        }
    }
}

fun createUi(game: GameScreen, inputChannel: Channel<Long>): BreakoutUi {
    val breakoutUi = BreakoutUi(game, inputChannel)
    JFrame().apply {
        title = "Breakout"
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        setSize(524, 380)
        isResizable = false

        add(breakoutUi)

        setLocationRelativeTo(null)
        isVisible = true
    }
    return breakoutUi
}


class BreakoutUi(val game: GameScreen, inputChannel: Channel<Long>) : JPanel() {
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
                if (input != null && !game.terminated) {
                    runBlocking {
                        inputChannel.send(input.toLong())
                    }
                }
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
        val scoreString = "Score: ${game.score}"
        val y = -fm.getLineMetrics(scoreString, g).baselineOffsets[2].toInt()
        g.drawString(scoreString, 2, y + 2)
        for (y in 0 until game.screenHeight) {
            for (x in 0 until game.screenWidth) {
                drawCell(g as Graphics2D, game[Coord2(x, y)], x, y)
            }
        }

    }

    fun offsetCoors(arg: Int): Int {
        return arg * (CELL_MARGIN + CELL_SIZE) + CELL_MARGIN
    }

    fun drawCell(g: Graphics2D, value: Char, x: Int, y: Int) {
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

