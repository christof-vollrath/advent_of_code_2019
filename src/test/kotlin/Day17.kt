import org.amshove.kluent.`should be greater than`
import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.data_driven.data
import org.jetbrains.spek.data_driven.on as onData

/*
--- Day 17: Set and Forget ---

An early warning system detects an incoming solar flare and automatically activates the ship's electromagnetic shield.
Unfortunately, this has cut off the Wi-Fi for many small robots that, unaware of the impending danger,
are now trapped on exterior scaffolding on the unsafe side of the shield.
To rescue them, you'll have to act quickly!

The only tools at your disposal are some wired cameras and a small vacuum robot currently asleep at its charging station.
The video quality is poor, but the vacuum robot has a needlessly bright LED that makes it easy to spot
no matter where it is.

An Intcode program, the Aft Scaffolding Control and Information Interface (ASCII, your puzzle input),
provides access to the cameras and the vacuum robot.
Currently, because the vacuum robot is asleep, you can only access the cameras.

Running the ASCII program on your Intcode computer will provide the current view of the scaffolds.
This is output, purely coincidentally, as ASCII code: 35 means #, 46 means .,
10 starts a new line of output below the current one, and so on. (Within a line, characters are drawn left-to-right.)

In the camera output, # represents a scaffold and . represents open space.
The vacuum robot is visible as ^, v, <, or > depending on whether it is facing up, down, left, or right respectively.
When drawn like this, the vacuum robot is always on a scaffold;
if the vacuum robot ever walks off of a scaffold and begins tumbling through space uncontrollably,
it will instead be visible as X.

In general, the scaffold forms a path, but it sometimes loops back onto itself.
 For example, suppose you can see the following view from the cameras:

..#..........
..#..........
#######...###
#.#...#...#.#
#############
..#...#...#..
..#####...^..

Here, the vacuum robot, ^ is facing up and sitting at one end of the scaffold near the bottom-right of the image.
The scaffold continues up, loops across itself several times, and ends at the top-left of the image.

The first step is to calibrate the cameras by getting the alignment parameters of some well-defined points.
Locate all scaffold intersections; for each, its alignment parameter is the distance between its left edge and
the left edge of the view multiplied by the distance between its top edge and the top edge of the view.

Here, the intersections from the above image are marked O:

..#..........
..#..........
##O####...###
#.#...#...#.#
##O###O###O##
..#...#...#..
..#####...^..

For these intersections:

The top-left intersection is 2 units from the left of the image and 2 units from the top of the image,
so its alignment parameter is 2 * 2 = 4.
The bottom-left intersection is 2 units from the left and 4 units from the top, so its alignment parameter is 2 * 4 = 8.
The bottom-middle intersection is 6 from the left and 4 from the top, so its alignment parameter is 24.
The bottom-right intersection's alignment parameter is 40.
To calibrate the cameras, you need the sum of the alignment parameters. In the above example, this is 76.

Run your ASCII program. What is the sum of the alignment parameters for the scaffold intersections?

--- Part Two ---

Now for the tricky part: notifying all the other robots about the solar flare.
The vacuum robot can do this automatically if it gets into range of a robot.
However, you can't see the other robots on the camera, so you need to be thorough instead:
you need to make the vacuum robot visit every part of the scaffold at least once.

The vacuum robot normally wanders randomly, but there isn't time for that today.
Instead, you can override its movement logic with new rules.

Force the vacuum robot to wake up by changing the value in your ASCII program at address 0 from 1 to 2.
When you do this, you will be automatically prompted for the new movement rules that the vacuum robot should use.
The ASCII program will use input instructions to receive them, but they need to be provided as ASCII code;
end each line of logic with a single newline, ASCII code 10.

First, you will be prompted for the main movement routine.
The main routine may only call the movement functions: A, B, or C.
Supply the movement functions to use as ASCII text, separating them with commas (,, ASCII code 44),
and ending the list with a newline (ASCII code 10).
For example, to call A twice, then alternate between B and C three times,
provide the string A,A,B,C,B,C,B,C and then a newline.

Then, you will be prompted for each movement function.
Movement functions may use L to turn left, R to turn right, or a number to move forward that many units.
Movement functions may not call other movement functions.
Again, separate the actions with commas and end the list with a newline.
For example, to move forward 10 units, turn left, move forward 8 units, turn right, and finally move forward 6 units,
provide the string 10,L,8,R,6 and then a newline.

Finally, you will be asked whether you want to see a continuous video feed; provide either y or n and a newline.
Enabling the continuous video feed can help you see what's going on,
but it also requires a significant amount of processing power, and may even cause your Intcode computer to overheat.

Due to the limited amount of memory in the vacuum robot,
the ASCII definitions of the main routine and the movement functions may each contain at most 20 characters, not counting the newline.

For example, consider the following camera feed:

#######...#####
#.....#...#...#
#.....#...#...#
......#...#...#
......#...###.#
......#.....#.#
^########...#.#
......#.#...#.#
......#########
........#...#..
....#########..
....#...#......
....#...#......
....#...#......
....#####......

In order for the vacuum robot to visit every part of the scaffold at least once, one path it could take is:

R,8,R,8,R,4,R,4,R,8,L,6,L,2,R,4,R,4,R,8,R,8,R,8,L,6,L,2
Without the memory limit, you could just supply this whole string to function A and have the main routine call A once.
However, you'll need to split it into smaller parts.

One approach is:

Main routine: A,B,C,B,A,C
(ASCII input: 65, 44, 66, 44, 67, 44, 66, 44, 65, 44, 67, 10)
Function A:   R,8,R,8
(ASCII input: 82, 44, 56, 44, 82, 44, 56, 10)
Function B:   R,4,R,4,R,8
(ASCII input: 82, 44, 52, 44, 82, 44, 52, 44, 82, 44, 56, 10)
Function C:   L,6,L,2
(ASCII input: 76, 44, 54, 44, 76, 44, 50, 10)
Visually, this would break the desired path into the following parts:

A,        B,            C,        B,            A,        C
R,8,R,8,  R,4,R,4,R,8,  L,6,L,2,  R,4,R,4,R,8,  R,8,R,8,  L,6,L,2

CCCCCCA...BBBBB
C.....A...B...B
C.....A...B...B
......A...B...B
......A...CCC.B
......A.....C.B
^AAAAAAAA...C.B
......A.A...C.B
......AAAAAA#AB
........A...C..
....BBBB#BBBB..
....B...A......
....B...A......
....B...A......
....BBBBA......

Of course, the scaffolding outside your ship is much more complex.

As the vacuum robot finds other robots and notifies them of the impending solar flare,
it also can't help but leave them squeaky clean, collecting any space dust it finds.
Once it finishes the programmed set of movements, assuming it hasn't drifted off into space,
the cleaning robot will return to its docking station and report the amount of space dust it collected as a large,
non-ASCII value in a single output instruction.

After visiting every part of the scaffold at least once, how much dust does the vacuum robot report it has collected?

 */

class Day17Spec : Spek({

    describe("part 1") {
        describe("find intersections") {
            val input = """
                ..#..........
                ..#..........
                #######...###
                #.#...#...#.#
                #############
                ..#...#...#..
                ..#####...^..
            """.trimIndent()
            val intersections = input.findIntersections()
            it("should have found all intersecions") {
                intersections `should equal` setOf(
                    Coord2(2, 2), Coord2(2, 4), Coord2(6, 4), Coord2(10, 4)
                )
            }
            it("should calculate the callibration for each camera") {
                intersections.calibrate() `should equal` setOf(4, 8, 24, 40)
            }
            it("should calculate the callibration sum") {
                intersections.calibrate().sum() `should equal` 76
            }
        }
        given("int codes") {
            val intCodesString = readResource("day17Input.txt")!!
            val intCodes = parseIntCodes09(intCodesString)
            val scaffold = intCodes.executeExtendedIntCodes09(intCodes).map { it.toChar() }.joinToString("")
            it("should print the scaffold") {
                println("scaffold=$scaffold")
                scaffold.length `should be greater than` 0
            }
            it("should calculate the calibration sum") {
                scaffold.findIntersections().calibrate().sum() `should equal` 4372
            }
        }
    }
    describe("part 2") {
        describe("how to split a path") {
            describe("generate all sub paths") {
                val testData = arrayOf(
                    data(listOf<RobotCommand>(RobotRotate(RobotTurnDirection.RIGHT), RobotRotate(RobotTurnDirection.LEFT)),
                        setOf<List<RobotCommand>>(
                            listOf(RobotRotate(RobotTurnDirection.RIGHT), RobotRotate(RobotTurnDirection.LEFT))
                        )
                    ),
                    data(listOf(RobotRotate(RobotTurnDirection.RIGHT), RobotRotate(RobotTurnDirection.LEFT), RobotMove(1)),
                        setOf(
                            listOf(RobotRotate(RobotTurnDirection.RIGHT), RobotRotate(RobotTurnDirection.LEFT)),
                            listOf(RobotRotate(RobotTurnDirection.RIGHT), RobotRotate(RobotTurnDirection.LEFT), RobotMove(1)),
                            listOf(RobotRotate(RobotTurnDirection.LEFT), RobotMove(1))
                        )
                    ),
                    data(listOf(RobotRotate(RobotTurnDirection.RIGHT), RobotMove(1), RobotRotate(RobotTurnDirection.LEFT), RobotMove(2)),
                        setOf(
                            listOf(RobotRotate(RobotTurnDirection.RIGHT), RobotMove(1)),
                            listOf(RobotRotate(RobotTurnDirection.RIGHT), RobotMove(1), RobotRotate(RobotTurnDirection.LEFT)),
                            listOf(RobotMove(1), RobotRotate(RobotTurnDirection.LEFT)),
                            listOf(RobotMove(1), RobotRotate(RobotTurnDirection.LEFT), RobotMove(2)),
                            listOf(RobotRotate(RobotTurnDirection.LEFT), RobotMove(2))
                        )
                    )
                )
                onData("list=%s ", with = *testData) { input: List<RobotCommand>, expected: Set<List<RobotCommand>> ->
                    it("should split command list") {
                        input.subLists(4, 6) `should equal` expected
                    }
                }
            }
        }
        given("example feed") {
            val input = """
                #######...#####
                #.....#...#...#
                #.....#...#...#
                ......#...#...#
                ......#...###.#
                ......#.....#.#
                ^########...#.#
                ......#.#...#.#
                ......#########
                ........#...#..
                ....#########..
                ....#...#......
                ....#...#......
                ....#...#......
                ....#####......
            """.trimIndent()
            val scaffold = input.parseScaffold()

            val startPosition = scaffold.findStartPosition()!!
            it("should find start position") {
                startPosition `should equal` Coord2(0, 6)
            }
            val direction = scaffold[0, 6].parseDirection()
            it("should parse direction") {
                direction `should equal` RobotDirection.UP
            }
            val startRotation = determineRotation(scaffold, direction, startPosition)
            it("should determine rotation") {
                startRotation `should equal` RobotTurnDirection.RIGHT
            }

            describe("robot") {
                val robot = CleaningRobot(scaffold, startPosition, direction)

                on("turning the robot") {
                    robot.turn(startRotation!!)
                    it("should turn robot") {
                        robot.direction `should equal` RobotDirection.RIGHT
                    }
                }
                on("moving the robot") {
                    val result = robot.move()
                    it("should move 8 steps") {
                        result `should equal` 8
                        robot.position  `should equal` Coord2(8, 6)
                    }
                }
                on("turning the robot again") {
                    val rotation = determineRotation(robot)
                    it("should determine rotation") {
                        rotation `should equal` RobotTurnDirection.RIGHT
                    }
                    robot.turn(rotation!!)
                    it("should turn robot again") {
                        robot.direction `should equal` RobotDirection.DOWN
                    }
                }
                on("moving the robot again") {
                    val result = robot.move()
                    it("should move 8 steps again") {
                        result `should equal` 8
                        robot.position  `should equal` Coord2(8, 14)
                    }
                }
            }
            describe("find path") {
                val robot = CleaningRobot(scaffold, startPosition, direction)
                val path = robot.findPath()
                it("should find the correct path") {
                    path `should equal` listOf(
                        // R,8,R,8,R,4,R,4,R,8,L,6,L,2,R,4,R,4,R,8,R,8,R,8,L,6,L,2
                        RobotRotate(RobotTurnDirection.RIGHT),
                        RobotMove(8),
                        RobotRotate(RobotTurnDirection.RIGHT),
                        RobotMove(8),
                        RobotRotate(RobotTurnDirection.RIGHT),
                        RobotMove(4),
                        RobotRotate(RobotTurnDirection.RIGHT),
                        RobotMove(4),
                        RobotRotate(RobotTurnDirection.RIGHT),
                        RobotMove(8),
                        RobotRotate(RobotTurnDirection.LEFT),
                        RobotMove(6),
                        RobotRotate(RobotTurnDirection.LEFT),
                        RobotMove(2),
                        RobotRotate(RobotTurnDirection.RIGHT),
                        RobotMove(4),
                        RobotRotate(RobotTurnDirection.RIGHT),
                        RobotMove(4),
                        RobotRotate(RobotTurnDirection.RIGHT),
                        RobotMove(8),
                        RobotRotate(RobotTurnDirection.RIGHT),
                        RobotMove(8),
                        RobotRotate(RobotTurnDirection.RIGHT),
                        RobotMove(8),
                        RobotRotate(RobotTurnDirection.LEFT),
                        RobotMove(6),
                        RobotRotate(RobotTurnDirection.LEFT),
                        RobotMove(2)
                    )
                }
                describe("split path") {
                    TODO()
                }
            }
        }
    }
})

fun List<RobotCommand>.subLists(minLength: Int, maxLength: Int): Set<List<RobotCommand>> =
    sequence {
        val list = this@subLists
        for (i in list.size downTo 1) {
            val subList = list.take(i)
            val len = subList.map { it.length }.sum()
            if (len < minLength) break // too small
            yield(subList.subListsFromStart(minLength, maxLength))
        }
    }.flatten().toSet()

fun List<RobotCommand>.subListsFromStart(minLength: Int, maxLength: Int): Set<List<RobotCommand>> =
    sequence {
        val list = this@subListsFromStart
        for (i in (list.size - 1) downTo 0) {
            val subList = list.drop(i)
            val len = subList.map { it.length }.sum()
            if (len < minLength) continue // too small
            if (len > maxLength) break // too big
            yield(subList)
        }
    }.toSet()

data class CleaningRobot(val scaffold: List<List<Char>>, var position: Coord2, var direction: RobotDirection) {
    fun turn(rotation: RobotTurnDirection) {
        direction = direction.turn(rotation)
    }

    fun move(): Int {
        var steps = 0
        while(true) {
            val nextPosition = position.move(direction)
            if (scaffold[nextPosition] != '#') break
            position = nextPosition
            steps++
        }
        return steps
    }

    fun findPath(): List<RobotCommand> =
        sequence {
            val startPosition = scaffold.findStartPosition()!!
            val direction = scaffold[startPosition].parseDirection()
            val startRotation = determineRotation(scaffold, direction, startPosition)!!
            val robot = CleaningRobot(scaffold, startPosition, direction)
            robot.turn(startRotation)
            yield(RobotRotate(startRotation))
            while(true) {
                val steps = robot.move()
                yield(RobotMove(steps))
                val rotation = determineRotation(robot)
                if (rotation == null) break
                robot.turn(rotation)
                yield(RobotRotate(rotation))
            }
        }.toList()
}

sealed class RobotCommand {
    abstract val length: Int
}
data class RobotRotate(val turnDirection: RobotTurnDirection) : RobotCommand() {
    override val length = 1 + 1 // Including separating comma
}
data class RobotMove(val steps: Int) : RobotCommand() {
    override val length = steps.toString().length + 1
}

val rotationMap = mapOf(
    RobotDirection.LEFT to listOf(
        Coord2(0, -1) to RobotTurnDirection.RIGHT,
        Coord2(0, 1) to RobotTurnDirection.LEFT
    ),
    RobotDirection.RIGHT to listOf(
        Coord2(0, -1) to RobotTurnDirection.LEFT,
        Coord2(0, 1) to RobotTurnDirection.RIGHT
    ),
    RobotDirection.UP to listOf(
        Coord2(-1, 0) to RobotTurnDirection.LEFT,
        Coord2(1, 0) to RobotTurnDirection.RIGHT
    ),
    RobotDirection.DOWN to listOf(
        Coord2(-1, 0) to RobotTurnDirection.RIGHT,
        Coord2(1, 0) to RobotTurnDirection.LEFT
    )
)

fun determineRotation(robot: CleaningRobot) = determineRotation(robot.scaffold, robot.direction, robot.position)

fun determineRotation(scaffold: List<List<Char>>, direction: RobotDirection, position: Coord2): RobotTurnDirection? {
    val checkList = rotationMap[direction]!!
    val rotation = checkList.find { (offset, turn) ->
        val checkPos = position + offset
        scaffold[checkPos] == '#'
    }
    return rotation?.second
}

fun Char.parseDirection() = when(this) {
    '<' -> RobotDirection.LEFT
    '>' -> RobotDirection.RIGHT
    '^' -> RobotDirection.UP
    'v' -> RobotDirection.DOWN
    else -> error("Illegal char=$this for direction")
}

fun List<List<Char>>.findStartPosition() = mapIndexedNotNull { y, row ->
    row.mapIndexedNotNull { x, c ->
        if (c in setOf('^', 'v', '<', '>')) { Coord2(x, y) }
        else null
    }
}.flatten().firstOrNull()

fun Set<Coord2>.calibrate() = map { it.x * it.y}.toSet()

fun String.findIntersections(): Set<Coord2> = parseScaffold().findIntersections()

fun String.parseScaffold() = lines().map { it.toList() }

operator fun List<List<Char>>.get(x: Int, y: Int): Char {
    return if ( !(0 <= y && y < size)) '.'
    else {
        val row = get(y)
        if ( ! (0 <= x && x < row.size)) '.'
        else row.get(x)
    }
}

operator fun List<List<Char>>.get(coord: Coord2) = get(coord.x, coord.y)

fun List<List<Char>>.findIntersections(): Set<Coord2> = mapIndexedNotNull { y, row ->
    row.mapIndexedNotNull { x, c ->
        if (this[x, y] == '#' &&
            this[x-1, y] == '#' &&
            this[x+1, y] == '#' &&
            this[x, y-1] == '#' &&
            this[x, y+1] == '#'
        ) Coord2(x, y)
        else null
    }
}.flatten().toSet()


