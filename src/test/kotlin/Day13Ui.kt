import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
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

    val game = Game(patchedIntCodes)
    game.play()
}

class GameState(val index: Long, val base: Long, val codes: Map<Long, Long>, val score: Int, val chars: List<List<Char>>)


class Game(intCodes: List<Long>) {
    val inputChannel = Channel<Long>(500)
    val outputChannel = Channel<Long>()
    val processor = IntCodeProcessor(inputChannel, outputChannel, intCodes)
    val screen = GameScreen()
    val ui = createUi(this, inputChannel)
    var score = 0
    var terminated = false
    var gameStates = mutableListOf<GameState>()

    fun play() {
        runBlocking {
            async {
                println("Executing processor in thread ${Thread.currentThread().name}")
                processor.execute()
                terminated = true
                println("terminated!")
            }
            async {
                println("Drawing output in thread ${Thread.currentThread().name}")
                while(!terminated) {
                    val x = outputChannel.receive().toInt()
                    val y = outputChannel.receive().toInt()
                    val code = outputChannel.receive().toInt()
                    if (x == -1 && y == 0) {
                        score = code
                    } else screen.draw(Coord2(x, y), code)
                    ui.repaint()
                }
            }
        }
    }

    fun undo() {
        if (gameStates.size > 500) {
            // Shorten to save memory
            gameStates = gameStates.drop(gameStates.size - 500).toMutableList()
        }
        if (gameStates.size > 0) {
            val state = gameStates.last()
            gameStates = gameStates.dropLast(1).toMutableList()
            processor.currentIndex = state.index
            processor.currentBase = state.base
            processor.currentState = state.codes.toMutableMap()
            score = state.score
            screen.chars = state.chars.map { it.toMutableList() }.toMutableList()
            ui.repaint()
        }
    }
}

fun createUi(game: Game, inputChannel: Channel<Long>): BreakoutUi {
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


class BreakoutUi(val game: Game, inputChannel: Channel<Long>) : JPanel() {
    init {
        isFocusable = true
        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                val input = when (e.keyCode) {
                    KeyEvent.VK_LEFT -> -1
                    KeyEvent.VK_RIGHT -> 1
                    KeyEvent.VK_SPACE -> 0
                    else -> {
                        if (e.keyChar == 'u') {
                                game.undo()
                        }
                        null
                    }
                }
                println("Before launch send input $input")
                if (input != null && !game.terminated) {
                    GlobalScope.launch {
                        println("Sending input $input in ${Thread.currentThread().name}")
                        game.processor.afterInputHook = { _, _ ->
                            println("Saving state in ${Thread.currentThread().name}")
                            val currentGameState = GameState(
                                game.processor.currentIndex,
                                game.processor.currentBase,
                                game.processor.currentState.toMap(),
                                game.score,
                                game.screen.chars.map { it.toList() }
                            )
                            game.gameStates.add(currentGameState)

                        }
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
        for (y in 0 until game.screen.screenHeight) {
            for (x in 0 until game.screen.screenWidth) {
                val c = game.screen[Coord2(x, y)]
                val outputChar = if (c == 'o' && y >= 24) 'O' else c
                drawCell(g as Graphics2D, outputChar, x, y)
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

