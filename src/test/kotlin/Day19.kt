import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.*
import java.lang.IllegalArgumentException

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

fun trackerGrid(intCodes: List<Long>, xSize: Int, ySize: Int): List<List<Int>> {
    val grid = (0..xSize-1).map { y ->
        (0..ySize-1).map { x ->
            deployDrone(intCodes, x, y)
        }
    }
    /*
    val gridString = grid.map { row ->
        row.map { if (it == 1) '#' else '.'}.joinToString("")
    }.joinToString("\n")
    println(gridString)
    */
    return grid
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
            val grid = trackerGrid(intCodes, 50, 50)
            val gridString = grid.map { row ->
                row.map {
                    if (it == 1) '#' else '.'
                }.joinToString("")
            }.joinToString("\n")
            it("should print the grid") {
                println(gridString)
            }
            it("should calculate points affected by the tractor beam") {
                val count = grid.map { row ->
                    row.filter { it == 1}.count()
                }.sum()
                count `should equal` 231
            }
        }
    }

    describe("part two") {
        describe("find the square in the example") {
            val exampleGrid = examplePart2GridString.split("\n").map { row ->
                row.map {
                    if (it in setOf('#', 'O')) 1 else 0
                }
            }
            it("should have parsed the correct grid") {
                exampleGrid[0][0] `should equal` 1
                exampleGrid[20][25] `should equal` 1 // coord of the square
            }
            describe("find possible upper left corners of a square") {
                val candidates = findSquareCandidates(exampleGrid, 10).take(2).toList()
                candidates[0] `should equal` Coord2(11, 12)
                candidates[1] `should equal` Coord2(13, 13)
            }
            describe("find square") {
                val squareCoord = findSquare(exampleGrid, 10)
                squareCoord `should equal` Coord2(25, 20)
            }
        }

        xdescribe("can we calculate a huge grid") {
            /*
            val hugeGridSize = 4000
            val hugeGrid = trackerGrid(intCodes, hugeGridSize, hugeGridSize)
            it("should have the right grid size") {
                hugeGrid.size `should equal` hugeGridSize
            }
            it("should find the square") {
                val squareCoord = findSquare(hugeGrid, 100)
                squareCoord `should equal` Coord2(25, 20)
            }
             */
        }
    }
})

fun findSquareCandidates(grid: List<List<Int>>, squareSize: Int): Sequence<Coord2> =
    sequence {
        for (y in 0..(grid.size-1)) {
            val row = grid[y]
            val right = row.mapIndexed { x, cell -> x to cell }.last { it.second == 1}.first
            var left: Int? = null
            for (x in 0..(row.size-1)) {
                if (row[x] == 1) {
                    left = x
                    break
                }
            }
            yield(if (left == null) null
            else {
                var right: Int? = null
                for (x in left..(row.size-1)) {
                    if (row[x] != 1) {
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
        }
    }
    .filterNotNull()


fun findSquare(grid: List<List<Int>>, size: Int): Coord2 {
    val candidates = findSquareCandidates(grid, size) // upper left corners
    return candidates.first { candidate ->
        println("x=${candidate.x} y=${candidate.y}")
        grid[candidate.y + size - 1][candidate.x] == 1 // lower left should also be reached by the tracker
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
