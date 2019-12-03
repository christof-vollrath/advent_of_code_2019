import org.amshove.kluent.`should be greater than`
import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.data_driven.data
import org.jetbrains.spek.data_driven.on as onData
import java.lang.Math.*

/*
--- Day 3: Crossed Wires ---

The gravity assist was successful, and you're well on your way to the Venus refuelling station.
During the rush back on Earth, the fuel management system wasn't completely installed,
so that's next on the priority list.

Opening the front panel reveals a jumble of wires. Specifically,
two wires are connected to a central port and extend outward on a grid.
You trace the path each wire takes as it leaves the central port, one wire per line of text (your puzzle input).

The wires twist and turn, but the two wires occasionally cross paths.
To fix the circuit, you need to find the intersection point closest to the central port.
Because the wires are on a grid, use the Manhattan distance for this measurement.
While the wires do technically cross right at the central port where they both start, this point does not count,
nor does a wire count as crossing with itself.

For example, if the first wire's path is R8,U5,L5,D3, then starting from the central port (o),
it goes right 8, up 5, left 5, and finally down 3:

...........
...........
...........
....+----+.
....|....|.
....|....|.
....|....|.
.........|.
.o-------+.
...........

Then, if the second wire's path is U7,R6,D4,L4, it goes up 7, right 6, down 4, and left 4:

...........
.+-----+...
.|.....|...
.|..+--X-+.
.|..|..|.|.
.|.-X--+.|.
.|..|....|.
.|.......|.
.o-------+.
...........

These wires cross at two locations (marked X),
but the lower-left one is closer to the central port: its distance is 3 + 3 = 6.

Here are a few more examples:

R75,D30,R83,U83,L12,D49,R71,U7,L72
U62,R66,U55,R34,D71,R55,D58,R83 = distance 159
R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51
U98,R91,D20,R16,D67,R40,U7,R15,U6,R7 = distance 135

What is the Manhattan distance from the central port to the closest intersection?

--- Part Two ---

It turns out that this circuit is very timing-sensitive; you actually need to minimize the signal delay.

To do this, calculate the number of steps each wire takes to reach each intersection;
choose the intersection where the sum of both wires' steps is lowest.
If a wire visits a position on the grid multiple times,
use the steps value from the first time it visits that position when calculating the total value of a specific intersection.

The number of steps a wire takes is the total number of grid squares the wire has entered to get to that location,
including the intersection being considered.

Again consider the example from above:

...........
.+-----+...
.|.....|...
.|..+--X-+.
.|..|..|.|.
.|.-X--+.|.
.|..|....|.
.|.......|.
.o-------+.
...........

In the above example, the intersection closest to the central port is reached after 8+5+5+2 = 20 steps by the first wire
and 7+6+4+3 = 20 steps by the second wire for a total of 20+20 = 40 steps.

However, the top-right intersection is better: the first wire takes only 8+5+2 = 15
and the second wire takes only 7+6+2 = 15, a total of 15+15 = 30 steps.

Here are the best steps for the extra examples from above:

R75,D30,R83,U83,L12,D49,R71,U7,L72
U62,R66,U55,R34,D71,R55,D58,R83 = 610 steps
R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51
U98,R91,D20,R16,D67,R40,U7,R15,U6,R7 = 410 steps

What is the fewest combined steps the wires must take to reach an intersection?

 */

fun Set<Coord2>.findClosest(): Coord2? = minBy { it manhattanDistance Coord2(0, 0)}

fun List<List<String>>.fallowWires(): Set<Coord2> {
    val grid = WireGrid()
    return mapIndexed { i, wireInstructions ->
        wireInstructions.fallowWire(grid, i)
    }.flatten().toSet()
}

data class GridPoint(var c: Char, val i: Int? = null)
class WireGrid {
    var maxX = 0
    var minX = 0
    var maxY = 0
    var minY = 0
    val size
        get() = max(maxX - minX, maxY - minY)

    val charMap = mutableMapOf<Coord2, GridPoint>()
    operator fun set(coord: Coord2, value: GridPoint) {
        if (coord.x > maxX) maxX = coord.x
        if (coord.x < minX) minX = coord.x
        if (coord.y > maxY) maxY = coord.y
        if (coord.y < minY) minY = coord.y
        charMap[coord] = value
    }
    operator fun get(coord2: Coord2) = charMap.get(coord2)

    fun print() {
        for (y in minY .. maxY) {
            for (x in minX .. maxX)
                print(get(Coord2(x, y))?.c ?: '.')
            println()
        }
        println()
    }
}

fun List<String>.fallowWire(grid: WireGrid, iteration: Int): Set<Coord2> {
    println(this)
    var pos = Coord2(0, 0)
    grid[pos] = GridPoint('o')
    val result = sequence {
        forEach { instruction ->
            val (direction, steps) = instruction.parseWireInstruction()
            for (i in 1 .. steps) {
                pos = pos + direction.direction
                val existingGridPoint = grid[pos]
                if (existingGridPoint == null) {
                    val gridChar = if (i == steps) '+'
                    else if (direction in setOf(WireDirection.Up, WireDirection.Down)) '|'
                    else '-'
                    grid[pos] = GridPoint(gridChar, iteration)
                } else {
                    if (existingGridPoint.i != iteration) yield(pos) // Only when not crossing itself
                    existingGridPoint.c = 'x'
                }
            }
        }
    }.toSet()
    if (grid.size <= 500) grid.print()
    return result
}

fun String.parseWireInstruction(): Pair<WireDirection, Int> {
    val direction = when(val c = get(0)) {
        'U' -> WireDirection.Up
        'D' -> WireDirection.Down
        'L' -> WireDirection.Left
        'R' -> WireDirection.Right
        else -> throw IllegalArgumentException("Unexpected char for direction $c")
    }
    val steps = drop(1).toInt()
    return direction to steps
}

enum class WireDirection(val direction: Coord2) {
    Up(Coord2(0, 1)),
    Down(Coord2(0, -1)),
    Left(Coord2(-1 ,0)),
    Right(Coord2(1, 0))
}

fun String.parseWires() = split("\n").map { it.parseWire() }
fun String.parseWire(): List<String> = split(",").map { it.trim() }

data class Coord2(val x: Int, val y: Int) {
    infix fun manhattanDistance(other: Coord2): Int = abs(x - other.x) + abs(y - other.y)
    operator fun plus(direction: Coord2) = Coord2(x + direction.x, y + direction.y)
}

class Day03Spec : Spek({

    describe("part 1") {
        given("example input") {
            val inputStrings = "R8,U5,L5,D3\nU7,R6,D4,L4"
            on("parse and fallow wire") {
                val input = inputStrings.parseWires()
                val intersections = input.fallowWires()
                it("should have the right crosses") {
                    intersections `should equal` setOf(Coord2(3, 3), Coord2(6, 5))
                }
                val result = intersections.findClosest()!!
                val resultDistance = Coord2(0, 0) manhattanDistance result
                it("should have calculated the correct distance") {
                    resultDistance `should equal` 6
                }
            }
        }
        given("more examples") {
            val testData = arrayOf(
                data("R75,D30,R83,U83,L12,D49,R71,U7,L72\nU62,R66,U55,R34,D71,R55,D58,R83", 159),
                data("R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51\nU98,R91,D20,R16,D67,R40,U7,R15,U6,R7", 135)
            )
            onData("input %s", with = *testData) { inputStrings, expected ->
                val input = inputStrings.parseWires()
                val intersections = input.fallowWires()
                val result = intersections.findClosest()!!
                val resultDistance = Coord2(0, 0) manhattanDistance result
                it("should have calculated the correct distance") {
                    resultDistance `should equal` expected
                }
            }

        }
        given("exercise input") {
            val inputStrings = readResource("day03Input.txt")!!
            on("parse and fallow wire") {
                val input = inputStrings.parseWires()
                val intersections = input.fallowWires()
                val result = intersections.findClosest()!!
                val resultDistance = Coord2(0, 0) manhattanDistance result
                it("should have calculated the correct distance") {
                    resultDistance `should equal` 627
                }
            }
        }
    }
    describe("part 2") {
        given("example input") {
            val inputStrings = "R8,U5,L5,D3\nU7,R6,D4,L4"
            on("parse and fallow wire") {
                val input = inputStrings.parseWires()
                val intersectionsWithDistances = input.fallowWiresWithDistances()
                it("should have the right crosses") {
                    intersectionsWithDistances `should equal` setOf(Coord2(3, 3) to 40, Coord2(6, 5) to 30)
                }
                val result = intersections.findClosest()!!
                val resultDistance = Coord2(0, 0) manhattanDistance result
                it("should have calculated the correct distance") {
                    resultDistance `should equal` 6
                }
            }
        }
    }

})
