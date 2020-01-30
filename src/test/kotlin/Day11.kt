import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/*
--- Day 11: Space Police ---

On the way to Jupiter, you're pulled over by the Space Police.

"Attention, unmarked spacecraft! You are in violation of Space Law!
All spacecraft must have a clearly visible registration identifier!
You have 24 hours to comply or be sent to Space Jail!"

Not wanting to be sent to Space Jail, you radio back to the Elves on Earth for help.
Although it takes almost three hours for their reply signal to reach you, they send instructions for how
to power up the emergency hull painting robot and even provide a small Intcode program (your puzzle input)
that will cause it to paint your ship appropriately.

There's just one problem: you don't have an emergency hull painting robot.

You'll need to build a new emergency hull painting robot.
The robot needs to be able to move around on the grid of square panels on the side of your ship,
detect the color of its current panel, and paint its current panel black or white.
(All of the panels are currently black.)

The Intcode program will serve as the brain of the robot.
The program uses input instructions to access the robot's camera:
provide 0 if the robot is over a black panel or 1 if the robot is over a white panel.
Then, the program will output two values:

First, it will output a value indicating the color to paint the panel the robot is over:
0 means to paint the panel black, and 1 means to paint the panel white.
Second, it will output a value indicating the direction the robot should turn: 0 means it should turn left 90 degrees,
and 1 means it should turn right 90 degrees.
After the robot turns, it should always move forward exactly one panel. The robot starts facing up.

The robot will continue running for a while like this and halt when it is finished drawing.
Do not restart the Intcode computer inside the robot during this process.

For example, suppose the robot is about to start running. Drawing black panels as ., white panels as #,
and the robot pointing the direction it is facing (< ^ > v),
the initial state and region near the robot looks like this:

.....
.....
..^..
.....
.....

The panel under the robot (not visible here because a ^ is shown instead) is also black,
and so any input instructions at this point should be provided 0.
Suppose the robot eventually outputs 1 (paint white) and then 0 (turn left).
After taking these actions and moving forward one panel, the region now looks like this:

.....
.....
.<#..
.....
.....

Input instructions should still be provided 0.
Next, the robot might output 0 (paint black) and then 0 (turn left):

.....
.....
..#..
.v...
.....

After more outputs (1,0, 1,0):

.....
.....
..^..
.##..
.....

The robot is now back where it started, but because it is now on a white panel, input instructions should be provided 1.
After several more outputs (0,1, 1,0, 1,0), the area looks like this:

.....
..<#.
...#.
.##..
.....

Before you deploy the robot, you should probably have an estimate of the area it will cover:
specifically, you need to know the number of panels it paints at least once, regardless of color.
In the example above, the robot painted 6 panels at least once.
(It painted its starting panel twice, but that panel is still only counted once;
it also never painted the panel it ended on.)

Build a new emergency hull painting robot and run the Intcode program on it.
How many panels does it paint at least once?

--- Part Two ---

You're not sure what it's trying to paint, but it's definitely not a registration identifier.
The Space Police are getting impatient.

Checking your external ship cameras again, you notice a white panel marked
"emergency hull painting robot starting panel".
The rest of the panels are still black, but it looks like the robot was expecting to start on a white panel,
not a black one.

Based on the Space Law Space Brochure that the Space Police attached to one of your windows,
a valid registration identifier is always eight capital letters.
After starting the robot on a single white panel instead, what registration identifier does it paint on your hull?

 */

class PaintAreaWithRobot(val paintArea: PaintArea, val paintRobot: PaintRobot) {
    init {
        paintRobot.paintArea = paintArea
    }
    override fun toString(): String = paintArea.area.mapIndexed { y, row ->
        row.mapIndexed { x, cell ->
            if (x == paintRobot.robotPos.x && y == paintRobot.robotPos.y)
                when(paintRobot.robotDirection) {
                    RobotDirection.UP -> '^'
                    RobotDirection.RIGHT -> '>'
                    RobotDirection.DOWN -> 'v'
                    RobotDirection.LEFT -> '<'
                }
            else paintArea.area[y][x]
        }.joinToString("")
    }.joinToString("\n")
}

class PaintArea(val width: Int, val height: Int) {
    operator fun set(pos: Coord2, value: Char) {
        area[pos.y][pos.x] = value
    }
    operator fun get(pos: Coord2) = area[pos.y][pos.x]

    val area = MutableList(height) { MutableList(width) { '.'} }
}

class PaintRobot(x: Int, y: Int) {
    lateinit var paintArea: PaintArea
    var robotState = RobotState.PAINT
    var robotPos: Coord2
    var robotDirection = RobotDirection.UP

    init {
        robotPos = Coord2(x, y)
    }
    fun action(action: Int) {
        robotState = when (robotState) {
            RobotState.PAINT -> {
                paint(action)
                RobotState.MOVE
            }
            RobotState.MOVE -> {
                move(action)
                RobotState.PAINT
            }
        }
    }

    fun paint(action: Int) {
        when (action) {
            0 -> paintArea[robotPos] = 'X'
            1 -> paintArea[robotPos] = '#'
            else -> throw IllegalArgumentException("Unknown action $action for paint")
        }
    }
    fun move(action: Int) {
        val turnDirection: RobotTurnDirection
        when (action) {
            0 -> turnDirection = RobotTurnDirection.LEFT
            1 -> turnDirection = RobotTurnDirection.RIGHT
            else -> throw IllegalArgumentException("Unknown action $action for move")
        }
        robotDirection = robotDirection.turn(turnDirection)
        robotPos = robotPos.move(robotDirection)
    }

    fun sense(): Int =
        with(paintArea[robotPos]) {
            when(this) {
                '.', 'X' -> 0
                '#' -> 1
                else -> throw IllegalStateException("Illegal value $this at $robotPos")
            }
        }
}

enum class RobotDirection { UP, LEFT, RIGHT, DOWN;
    fun turn(turnDirection: RobotTurnDirection) =
        when (this) {
            RobotDirection.UP ->
                when (turnDirection) {
                    RobotTurnDirection.LEFT -> RobotDirection.LEFT
                    RobotTurnDirection.RIGHT -> RobotDirection.RIGHT
                }
            RobotDirection.RIGHT ->
                when (turnDirection) {
                    RobotTurnDirection.LEFT -> RobotDirection.UP
                    RobotTurnDirection.RIGHT -> RobotDirection.DOWN
                }
            RobotDirection.DOWN ->
                when (turnDirection) {
                    RobotTurnDirection.LEFT -> RobotDirection.RIGHT
                    RobotTurnDirection.RIGHT -> RobotDirection.LEFT
                }
            RobotDirection.LEFT ->
                when (turnDirection) {
                    RobotTurnDirection.LEFT -> RobotDirection.DOWN
                    RobotTurnDirection.RIGHT -> RobotDirection.UP
                }
        }
    }

enum class RobotTurnDirection { LEFT, RIGHT }
enum class RobotState { PAINT, MOVE }

fun Coord2.move(direction: RobotDirection) =
    when (direction) {
        RobotDirection.UP -> Coord2(x, y - 1)
        RobotDirection.RIGHT -> Coord2(x + 1, y)
        RobotDirection.DOWN -> Coord2(x, y + 1)
        RobotDirection.LEFT -> Coord2(x - 1, y)
    }


suspend fun List<Long>.executeExtendedIntCodes09Async(inputChannel: Channel<Long>, outputChannel: Channel<Long>, id: Int = 1) { // Even more intcodes and unlimited memory
    val currentState = mutableMapOf<Long, Long>() // Use map to emulate virtual infinite memory
    forEachIndexed { index, value -> currentState[index.toLong()] = value  }
    var currentIndex = 0L
    var currentBase = 0L
    while(true) {
        val commandWithParameterModes = currentState.getOrDefault(currentIndex, 0L)
        val (command, parameterModes) = commandWithParameterModes.toCommand09()
        // Commands are small enough to fit into an int
        println("curentIndex=$currentIndex commandWithParameterModes=$commandWithParameterModes command=$command")
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

fun runComputerConnectedToRobot(intCodes: List<Long>, robot: PaintRobot) {
    runBlocking {
        val inputChannel = Channel<Long>()
        val outputChannel = Channel<Long>()
        val computer = launch {
            intCodes.executeExtendedIntCodes09Async(inputChannel, outputChannel)
        }
        while (computer.isActive) {
            inputChannel.send(robot.sense().toLong())
            val paintAction = outputChannel.receive()
            robot.action(paintAction.toInt())
            val moveAction = outputChannel.receive()
            robot.action(moveAction.toInt())
        }
    }
}

class Day11Spec : Spek({

    describe("part 1") {
        describe("print paint area with robot") {
            val area = PaintArea(5, 5)
            val robot = PaintRobot(5 / 2, 5 / 2)
            val areaWithRobot = PaintAreaWithRobot(area, robot)
            it("should paint the inital area with robot") {
                areaWithRobot.toString() `should equal` """
                    .....
                    .....
                    ..^..
                    .....
                    .....
                """.trimIndent()
            }
        }
        describe("robot moves and paints") {
            val area = PaintArea(5, 5)
            val robot = PaintRobot(5 / 2, 5 / 2)
            val areaWithRobot = PaintAreaWithRobot(area, robot)
            robot.action(1)
            robot.action(0)
            it("should paint the inital area with robot") {
                areaWithRobot.toString() `should equal` """
                    .....
                    .....
                    .<#..
                    .....
                    .....
                """.trimIndent()
            }
        }
        describe("robot senses, moves and paints") {
            val area = PaintArea(5, 5)
            val robot = PaintRobot(5 / 2, 5 / 2)
            val areaWithRobot = PaintAreaWithRobot(area, robot)
            it("should sense black") {
                robot.sense() `should equal` 0
            }
            listOf(1,0, 0,0, 1,0, 1,0, 0,1, 1,0, 1,0).forEach { robot.action(it) }
            it("should paint the inital area with robot") {
                areaWithRobot.toString() `should equal` """
                    .....
                    ..<#.
                    .XX#.
                    .##..
                    .....
                """.trimIndent()
            }
            describe("exercise") {
                val areaSize = 151
                val area = PaintArea(areaSize, areaSize)
                val robot = PaintRobot(areaSize / 2, areaSize / 2)
                val areaWithRobot = PaintAreaWithRobot(area, robot)

                val intCodesString = readResource("day11Input.txt")!!
                val intCodes = parseIntCodes09(intCodesString)
                runComputerConnectedToRobot(intCodes, robot)
                println(areaWithRobot.toString())
                val paintedSpots = area.area.flatMap { row ->
                    row.filter { it == 'X' || it == '#'}
                }
                it("should have painted the right spots") {
                    paintedSpots.size `should equal` 1967
                }
            }
        }
    }
    describe("part 2") {
        describe("exercise") {
            val areaSize = 151
            val area = PaintArea(areaSize, areaSize)
            val robot = PaintRobot(areaSize / 2, areaSize / 2)
            val areaWithRobot = PaintAreaWithRobot(area, robot)
            area.area[areaSize / 2][areaSize / 2] = '#' // Start robot on a white position
            val intCodesString = readResource("day11Input.txt")!!
            val intCodes = parseIntCodes09(intCodesString)
            runComputerConnectedToRobot(intCodes, robot)
            println(areaWithRobot.toString().map {
                when(it) {
                    '#' -> ' '
                    '\n' -> '\n'
                    else -> 'O'
                } // Improve printing
            }.joinToString(""))
            val paintedSpots = area.area.flatMap { row ->
                row.filter { it == 'X' || it == '#'}
            }
            it("should have painted the right spots") {
                paintedSpots.size `should equal` 249
            }
        }
    }
})


