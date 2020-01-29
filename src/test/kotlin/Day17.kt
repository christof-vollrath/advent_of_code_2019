import org.amshove.kluent.`should be greater than`
import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

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
                println(scaffold)
                scaffold.length `should be greater than` 0
            }
            it("should calculate the calibration sum") {
                scaffold.findIntersections().calibrate().sum() `should equal` 4372
            }
        }
    }
})

private fun Set<Coord2>.calibrate() = map { it.x * it.y}.toSet()

private fun String.findIntersections(): Set<Coord2> {
    val scaffold = lines().map { it.toList() }
    operator fun List<List<Char>>.get(x: Int, y: Int): Char {
        return if ( !(0 <= y && y < size)) '.'
        else {
            val row = get(y)
            if ( ! (0 <= x && x < row.size)) '.'
            else row.get(x)
        }
    }

    return scaffold.mapIndexedNotNull { y, row ->
        row.mapIndexedNotNull { x, c ->
            if (scaffold[x, y] == '#' &&
                scaffold[x-1, y] == '#' &&
                scaffold[x+1, y] == '#' &&
                scaffold[x, y-1] == '#' &&
                scaffold[x, y+1] == '#'
            ) Coord2(x, y)
            else null
        }
    }.flatten().toSet()
}

