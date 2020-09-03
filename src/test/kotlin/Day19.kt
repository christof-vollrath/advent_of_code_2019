import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

/*
--- Day 19: Tractor Beam ---

Unsure of the state of Santa's ship, you borrowed the tractor beam technology from Triton. Time to test it out.

When you're safely away from anything else, you activate the tractor beam, but nothing happens.
It's hard to tell whether it's working if there's nothing to use it on.
Fortunately, your ship's drone system can be configured to deploy a drone to specific coordinates
and then check whether it's being pulled.

There's even an Intcode program (your puzzle input) that gives you access to the drone system.

The program uses two input instructions to request the X and Y position to which the drone should be deployed.
Negative numbers are invalid and will confuse the drone; all numbers should be zero or positive.

Then, the program will output whether the drone is stationary (0) or being pulled by something (1).
For example, the coordinate X=0, Y=0 is directly in front of the tractor beam emitter,
so the drone control program will always report 1 at that location.

To better understand the tractor beam, it is important to get a good picture of the beam itself.
For example, suppose you scan the 10x10 grid of points closest to the emitter:

       X
  0->      9
 0#.........
 |.#........
 v..##......
  ...###....
  ....###...
Y .....####.
  ......####
  ......####
  .......###
 9........##

In this example, the number of points affected by the tractor beam in the 10x10 area closest to the emitter is 27.

However, you'll need to scan a larger area to understand the shape of the beam.
How many points are affected by the tractor beam in the 50x50 area closest to the emitter?
(For each of X and Y, this will be 0 through 49.)

--- Part Two ---

You aren't sure how large Santa's ship is.
You aren't even sure if you'll need to use this thing on Santa's ship, but it doesn't hurt to be prepared.
You figure Santa's ship might fit in a 100x100 square.

The beam gets wider as it travels away from the emitter;
you'll need to be a minimum distance away to fit a square of that size into the beam fully.
(Don't rotate the square; it should be aligned to the same axes as the drone grid.)

For example, suppose you have the following tractor beam readings:

#.......................................
.#......................................
..##....................................
...###..................................
....###.................................
.....####...............................
......#####.............................
......######............................
.......#######..........................
........########........................
.........#########......................
..........#########.....................
...........##########...................
...........############.................
............############................
.............#############..............
..............##############............
...............###############..........
................###############.........
................#################.......
.................########OOOOOOOOOO.....
..................#######OOOOOOOOOO#....
...................######OOOOOOOOOO###..
....................#####OOOOOOOOOO#####
.....................####OOOOOOOOOO#####
.....................####OOOOOOOOOO#####
......................###OOOOOOOOOO#####
.......................##OOOOOOOOOO#####
........................#OOOOOOOOOO#####
.........................OOOOOOOOOO#####
..........................##############
..........................##############
...........................#############
............................############
.............................###########

In this example, the 10x10 square closest to the emitter that fits entirely within the tractor beam has been marked O.
Within it, the point closest to the emitter (the only highlighted O) is at X=25, Y=20.

Find the 100x100 square closest to the emitter that fits entirely within the tractor beam;
within that square, find the point closest to the emitter.

What value do you get if you take that point's X coordinate, multiply it by 10000, then add the point's Y coordinate?
(In the example above, this would be 250020.)
 */

interface GridInterface {
    operator fun get(x: Int, y: Int): Int
    fun toList(xSize: Int, ySize: Int): List<List<Int>> =
        (0 until ySize).map { y ->
            (0 until xSize).map { x ->
                this[x, y]
            }

        }
}
data class TrackerGrid(val intCodes: List<Long>) : GridInterface {
    override operator fun get(x: Int, y: Int) = deployDrone(intCodes, x, y)
}
data class ExampleGrid(val grid: List<List<Int>>) : GridInterface {
    constructor(gridString: String) :
        this(gridString.split("\n").map { row ->
            row.map {
                if (it in setOf('#', 'O')) 1 else 0
            }
        })
    override operator fun get(x: Int, y: Int) = try {
        grid[y][x]
    } catch(e: IndexOutOfBoundsException) {
        0
    }
}

fun deployDrone(intCodes: List<Long>, x: Int, y: Int): Int = intCodes.executeExtendedIntCodes09(listOf(x.toLong(), y.toLong())).first().toInt()

class Day19Spec : Spek({

    val intCodesString = readResource("day19Input.txt")!!
    val intCodes = parseIntCodes09(intCodesString)

    describe("part 1") {
        it("calculate the correct value for the coordinate X=0, Y=0 running the drone program") {
            val result = deployDrone(intCodes, 0, 0)
            result `should equal` 1
        }
        given("create the grid in the area 50x50") {
            val grid = TrackerGrid(intCodes)
            val gridString = grid.toList(50, 50).joinToString("\n") { row ->
                row.map {
                    if (it == 1) '#' else '.'
                }.joinToString("")
            }
            it("should print the grid") {
                println(gridString)
            }
            it("should calculate points affected by the tractor beam") {
                val count = grid.toList(50, 50).map { row ->
                    row.filter { it == 1}.count()
                }.sum()
                count `should equal` 231
            }
        }
    }

    describe("part two") {
        describe("find the square in the example") {
            val exampleGrid = ExampleGrid(examplePart2GridString)
            it("should have parsed the correct grid") {
                exampleGrid[0, 0] `should equal` 1
                exampleGrid[25, 20] `should equal` 1 // coord of the square
            }
            it("should find possible upper left corners of a square") {
                val candidates = findSquareCandidates(exampleGrid, 10).take(2).take(2).toList()
                candidates[0] `should equal` Coord2(11, 12)
                candidates[1] `should equal` Coord2(13, 13)
            }
            it("should find the square") {
                val squareCoord = findSquare(exampleGrid, 10)
                squareCoord `should equal` Coord2(25, 20)
                val result = calculateFormula(squareCoord)
                result `should equal` 250020
            }
        }
        describe("find the square in the exercise") {
            val trackerGrid = TrackerGrid(intCodes)
            it("should find the square") {
                val squareCoord = findSquare(trackerGrid, 100)
                squareCoord `should equal` Coord2(921, 745)
                val result = calculateFormula(squareCoord)
                result `should equal` 9210745
            }
        }

    }
})

fun calculateFormula(squareCoord: Coord2) = squareCoord.x * 10_000 + squareCoord.y

const val UPPER_BOUND = 10_000 // Upper bound for search because some rows have no cell reached by tracker beam

fun findSquareCandidates(grid: GridInterface, squareSize: Int): Sequence<Coord2> =
    sequence {
        var y = 0
        while(true) {
            var left: Int? = null
            for (x in 0..UPPER_BOUND) {
                if (grid[x, y] == 1) {
                    left = x
                    break
                }
            }
            yield(if (left == null) null
            else {
                var right: Int? = null
                for (x in left..UPPER_BOUND) {
                    if (grid[x, y] != 1) {
                        right = x - 1
                        break
                    }
                }
                if (right == null) null
                else {
                    val currentSize = right - left + 1
                    if (currentSize >= squareSize) Coord2(right - squareSize + 1, y)
                    else null
                }
            })
            y++
        }
    }
    .filterNotNull()

fun findSquare(grid: GridInterface, size: Int): Coord2 {
    val candidates = findSquareCandidates(grid, size) // upper left corners
    return candidates.first { candidate ->
        grid[candidate.x, candidate.y + size - 1] == 1 // lower left should also be reached by the tracker
    }
}

val examplePart2GridString = """
#.......................................
.#......................................
..##....................................
...###..................................
....###.................................
.....####...............................
......#####.............................
......######............................
.......#######..........................
........########........................
.........#########......................
..........#########.....................
...........##########...................
...........############.................
............############................
.............#############..............
..............##############............
...............###############..........
................###############.........
................#################.......
.................########OOOOOOOOOO.....
..................#######OOOOOOOOOO#....
...................######OOOOOOOOOO###..
....................#####OOOOOOOOOO#####
.....................####OOOOOOOOOO#####
.....................####OOOOOOOOOO#####
......................###OOOOOOOOOO#####
.......................##OOOOOOOOOO#####
........................#OOOOOOOOOO#####
.........................OOOOOOOOOO#####
..........................##############
..........................##############
...........................#############
............................############
.............................########### 
""".trimIndent()
