import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

/*
--- Day 13: Care Package ---

As you ponder the solitude of space and the ever-increasing three-hour roundtrip for messages between you and Earth,
you notice that the Space Mail Indicator Light is blinking.
To help keep you sane, the Elves have sent you a care package.

It's a new game for the ship's arcade cabinet!
Unfortunately, the arcade is all the way on the other end of the ship.
Surely, it won't be hard to build your own - the care package even comes with schematics.

The arcade cabinet runs Intcode software like the game the Elves sent (your puzzle input).
It has a primitive screen capable of drawing square tiles on a grid.
The software draws tiles to the screen with output instructions:
every three output instructions specify the x position (distance from the left),
y position (distance from the top), and tile id.
The tile id is interpreted as follows:

0 is an empty tile. No game object appears in this tile.
1 is a wall tile. Walls are indestructible barriers.
2 is a block tile. Blocks can be broken by the ball.
3 is a horizontal paddle tile. The paddle is indestructible.
4 is a ball tile. The ball moves diagonally and bounces off objects.

For example, a sequence of output values like 1,2,3,6,5,4 would draw a horizontal paddle tile
(1 tile from the left and 2 tiles from the top) and a ball tile (6 tiles from the left and 5 tiles from the top).

Start the game. How many block tiles are on the screen when the game exits?

--- Part Two ---

The game didn't run because you didn't put in any quarters.
Unfortunately, you did not bring any quarters.
Memory address 0 represents the number of quarters that have been inserted; set it to 2 to play for free.

The arcade cabinet has a joystick that can move left and right.
The software reads the position of the joystick with input instructions:

If the joystick is in the neutral position, provide 0.
If the joystick is tilted to the left, provide -1.
If the joystick is tilted to the right, provide 1.

The arcade cabinet also has a segment display capable of showing a single number
that represents the player's current score.
When three output instructions specify X=-1, Y=0, the third output instruction is not a tile;
the value instead specifies the new score to show in the segment display.
For example, a sequence of output values like -1,0,12345 would show 12345 as the player's current score.

Beat the game by breaking all the blocks. What is your score after the last block is broken?

 */

class Day13Spec : Spek({

    describe("part 1") {
        given("intCodes of exercise and screen") {
            val intCodesString = readResource("day13Input.txt")!!
            val intCodes = parseIntCodes09(intCodesString)
            val screen = GameBoard()
            val inputChannel = Channel<Long>()
            val outputChannel = Channel<Long>()
            val processor = IntCodeProcessor(inputChannel, outputChannel, intCodes)
            on("execute int codes") {
                runBlocking {
                    var terminated = false
                    launch {
                        processor.execute()
                        terminated = true
                    }
                    launch {
                        while(!terminated) {
                            val x = outputChannel.receive().toInt()
                            val y = outputChannel.receive().toInt()
                            val code = outputChannel.receive().toInt()
                            screen.draw(Coord2(x, y), code)
                        }
                    }

                }
                println(screen)
                it("should have found the right number of blocks") {
                    val nrOfBlocks = screen.chars.flatten().filter { it == 'x'}.count()
                    nrOfBlocks `should equal` 452
                }
            }
        }
    }
    describe("part 2") {
        describe("disassemble int codes") {
            val intCodesString = readResource("day13Input.txt")!!
            val intCodes = parseIntCodes09(intCodesString)
            it("should disassemble to the right codes") {
                val disassembled = intCodes.disassemble()
                disassembled.lines().take(5).joinToString("\n") `should equal` """
                    
                """.trimIndent()
            }

        }
        given("intCodes of exercise and screen") {
            val intCodesString = readResource("day13Input.txt")!!
            val intCodes = parseIntCodes09(intCodesString)
            println(intCodes.disassemble())


        }
    }
})

fun List<Long>.disassemble(): String =
    sequence {
        var currentIndex = 0
        while (currentIndex < size) {
            val commandWithParameterModes = this@disassemble.get(currentIndex)
            val disassembedCommand = try {
                val (command, parameterModes) = commandWithParameterModes.toCommand09()
                 when(command) {
                    1L -> { // Add
                        currentIndex += 4
                        "ADD"
                    }
                    2L -> { // Multiply
                        currentIndex += 4
                        "MUL"
                    }
                    3L -> { // Input
                        currentIndex += 2
                        "INP"
                    }
                    4L -> { // Ouput
                        currentIndex += 2
                        "OUT"
                    }
                    5L -> { // Jump if true
                        currentIndex += 3
                        "JMP TRUE"
                    }
                    6L -> { // Jump if false
                        currentIndex += 3
                        "JMP FALSE"
                    }
                    7L -> { // Less than
                        currentIndex += 4
                        "JMP LESS"
                    }
                    8L -> { // Equals
                        currentIndex += 4
                        "JMP EQ"
                    }
                    9L -> { // Add relative base
                        currentIndex += 2
                        "ADB"
                    }
                    99L ->  {
                        currentIndex += 1
                        "END"
                    }
                    else -> {
                        currentIndex += 1
                        commandWithParameterModes.toString()
                    }
                }

            } catch(e: java.lang.IllegalArgumentException) { // Illegal parameter mode
                currentIndex += 1
                commandWithParameterModes.toString()
            }
            yield(disassembedCommand)
        }
    }.joinToString("\n")


class GameBoard {
    val screenWidth = 51
    val screenHeight = 26
    var chars = MutableList(screenHeight) { MutableList(screenWidth) { ' '} }

    operator fun set(coord: Coord2, c: Char) {
        chars[coord.y][coord.x] = c
    }
    operator fun get(coord: Coord2) = chars[coord.y][coord.x]

    fun draw(coord: Coord2, code: Int) {
        val c = when(code) {
            0 -> ' '
            1 -> '#'
            2 -> 'x'
            3 -> '='
            4 -> 'o'
            else -> throw IllegalArgumentException("Unkown code $code")
        }
        set(coord, c)
    }

    override fun toString(): String = chars.map { row -> row.joinToString("") }.joinToString("\n")
}

typealias ExecutionHook = (commnd: Long, parameterModes: List<ParameterMode>) -> Boolean

class IntCodeProcessor(val inputChannel: Channel<Long>, val outputChannel: Channel<Long>, intCodes: List<Long>, val id: Int = 1) {
    var currentIndex = 0L
    var currentBase = 0L
    var currentState = mutableMapOf<Long, Long>() // Use map to emulate virtual infinite memory
    var executionPreHook: ExecutionHook? = null
    var afterInputHook: ExecutionHook? = null

    init {
        intCodes.forEachIndexed { index, value -> currentState[index.toLong()] = value  }
    }

    suspend fun execute() {

        while(true) {
            val commandWithParameterModes = currentState.getOrDefault(currentIndex, 0L)
            val (command, parameterModes) = commandWithParameterModes.toCommand09()
            val currentExecutionPreHook = executionPreHook
            if (currentExecutionPreHook != null)  {
                val hookResult = currentExecutionPreHook(command, parameterModes)
                if (hookResult) continue // Skip execution
            }
            //println("curentIndex=$currentIndex commandWithParameterModes=$commandWithParameterModes command=$command")
            when(command) {
                1L -> { // Add
                    val indexes = getParameterIndexes09(currentIndex, parameterModes, currentState, 1..3, currentBase)
                    currentState[indexes[2]] = currentState.getOrDefault(indexes[0], 0L) + currentState.getOrDefault(indexes[1], 0L)
                    currentIndex += 4
                }
                2L -> { // Multiply
                    val indexes = getParameterIndexes09(currentIndex, parameterModes, currentState, 1..3, currentBase)
                    currentState[indexes[2]] = currentState.getOrDefault(indexes[0], 0L) * currentState.getOrDefault(indexes[1], 0L)
                    currentIndex += 4
                }
                3L -> { // Input
                    val inputInt = inputChannel.receive()
                    val currentAfterInputHook = afterInputHook
                    if (currentAfterInputHook != null) {
                        currentAfterInputHook(command, parameterModes)
                    }
                    val indexes = getParameterIndexes09(currentIndex, parameterModes, currentState, 1..1, currentBase)
                    currentState[indexes[0]] = inputInt
                    currentIndex += 2
                }
                4L -> { // Ouput
                    val indexes = getParameterIndexes09(currentIndex, parameterModes, currentState, 1..1, currentBase)
                    val outputInt = currentState.getOrDefault(indexes[0], 0L)
                    outputChannel.send(outputInt)
                    currentIndex += 2
                }
                5L -> { // Jump if true
                    val indexes = getParameterIndexes09(currentIndex, parameterModes, currentState, 1..2, currentBase)
                    if (currentState.getOrDefault(indexes[0], 0L) != 0L)
                        currentIndex = currentState.getOrDefault(indexes[1], 0L)
                    else
                        currentIndex += 3
                }
                6L -> { // Jump if false
                    val indexes = getParameterIndexes09(currentIndex, parameterModes, currentState, 1..2, currentBase)
                    if (currentState.getOrDefault(indexes[0], 0L) == 0L)
                        currentIndex = currentState.getOrDefault(indexes[1], 0L)
                    else
                        currentIndex += 3
                }
                7L -> { // Less than
                    val indexes = getParameterIndexes09(currentIndex, parameterModes, currentState, 1..3, currentBase)
                    currentState[indexes[2]] = if (currentState.getOrDefault(indexes[0], 0L) < currentState.getOrDefault(indexes[1], 0L)) 1L else 0L
                    currentIndex += 4
                }
                8L -> { // Equals
                    val indexes = getParameterIndexes09(currentIndex, parameterModes, currentState, 1..3, currentBase)
                    currentState[indexes[2]] = if (currentState.getOrDefault(indexes[0], 0L) == currentState.getOrDefault(indexes[1], 0L)) 1L else 0L
                    currentIndex += 4
                }
                9L -> { // Add relative base
                    val indexes = getParameterIndexes09(currentIndex, parameterModes, currentState, 1..1, currentBase)
                    val incr = currentState.getOrDefault(indexes[0], 0L)
                    currentBase += incr
                    currentIndex += 2
                }
                99L -> return
                else -> throw IllegalArgumentException("currentIndex=$currentIndex command=$command")
            }
        }
    }

}
