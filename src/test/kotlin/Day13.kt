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
            val screen = GameScreen()
            on("execute int codes") {
                val output = intCodes.executeExtendedIntCodes09(emptyList())
                screen.draw(output)
                println(screen)
                it("should have found the right number of blocks") {
                    val nrOfBlocks = screen.screen.flatten().filter { it == 'x'}.count()
                    nrOfBlocks `should equal` 452
                }
            }
        }
    }
    // https://unix.stackexchange.com/questions/88972/how-to-keep-the-terminal-cursor-fixed-at-the-top
})

class GameScreen {
    val screenWidth = 80
    val screenHeight = 40
    val screen = MutableList(screenHeight) { MutableList(screenWidth) { ' '} }
    operator fun set(coord: Coord2, c: Char) {
        screen[coord.y][coord.x] = c
    }
    operator fun get(coord: Coord2) = screen[coord.y][coord.x]

    fun draw(outputInstructions: List<Long>) = outputInstructions.chunked(3).forEach { outputInstruction ->
        draw(Coord2(outputInstruction[0].toInt(), outputInstruction[1].toInt()), outputInstruction[2].toInt())
    }

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

    override fun toString(): String = screen.map { row -> row.joinToString("") }.joinToString("\n")
}
