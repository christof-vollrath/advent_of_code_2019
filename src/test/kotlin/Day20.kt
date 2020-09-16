import org.amshove.kluent.`should contain`
import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

/*
--- Day 20: Donut Maze ---
You notice a strange pattern on the surface of Pluto and land nearby to get a closer look.
Upon closer inspection, you realize you've come across one of the famous space-warping mazes
of the long-lost Pluto civilization!

Because there isn't much space on Pluto, the civilization that used to live here thrived
by inventing a method for folding spacetime.
Although the technology is no longer understood,
mazes like this one provide a small glimpse into the daily life of an ancient Pluto citizen.

This maze is shaped like a donut. Portals along the inner and outer edge of the donut can instantly teleport
you from one side to the other. For example:

         A
         A
  #######.#########
  #######.........#
  #######.#######.#
  #######.#######.#
  #######.#######.#
  #####  B    ###.#
BC...##  C    ###.#
  ##.##       ###.#
  ##...DE  F  ###.#
  #####    G  ###.#
  #########.#####.#
DE..#######...###.#
  #.#########.###.#
FG..#########.....#
  ###########.#####
             Z
             Z

This map of the maze shows solid walls (#) and open passages (.).
Every maze on Pluto has a start (the open tile next to AA) and an end (the open tile next to ZZ).
Mazes on Pluto also have portals;
this maze has three pairs of portals: BC, DE, and FG.
When on an open tile next to one of these labels,
a single step can take you to the other tile with the same label.
(You can only walk on . tiles; labels and empty space are not traversable.)

One path through the maze doesn't require any portals.
Starting at AA, you could go down 1, right 8, down 12, left 4, and down 1 to reach ZZ, a total of 26 steps.

However, there is a shorter path:
You could walk from AA to the inner BC portal (4 steps), warp to the outer BC portal (1 step),
walk to the inner DE (6 steps), warp to the outer DE (1 step), walk to the outer FG (4 steps),
warp to the inner FG (1 step), and finally walk to ZZ (6 steps). In total, this is only 23 steps.

Here is a larger example:

                   A
                   A
  #################.#############
  #.#...#...................#.#.#
  #.#.#.###.###.###.#########.#.#
  #.#.#.......#...#.....#.#.#...#
  #.#########.###.#####.#.#.###.#
  #.............#.#.....#.......#
  ###.###########.###.#####.#.#.#
  #.....#        A   C    #.#.#.#
  #######        S   P    #####.#
  #.#...#                 #......VT
  #.#.#.#                 #.#####
  #...#.#               YN....#.#
  #.###.#                 #####.#
DI....#.#                 #.....#
  #####.#                 #.###.#
ZZ......#               QG....#..AS
  ###.###                 #######
JO..#.#.#                 #.....#
  #.#.#.#                 ###.#.#
  #...#..DI             BU....#..LF
  #####.#                 #.#####
YN......#               VT..#....QG
  #.###.#                 #.###.#
  #.#...#                 #.....#
  ###.###    J L     J    #.#.###
  #.....#    O F     P    #.#...#
  #.###.#####.#.#####.#####.###.#
  #...#.#.#...#.....#.....#.#...#
  #.#####.###.###.#.#.#########.#
  #...#.#.....#...#.#.#.#.....#.#
  #.###.#####.###.###.#.#.#######
  #.#.........#...#.............#
  #########.###.###.#############
           B   J   C
           U   P   P

Here, AA has no direct path to ZZ, but it does connect to AS and CP.
By passing through AS, QG, BU, and JO, you can reach ZZ in 58 steps.

In your maze, how many steps does it take to get from the open tile marked AA to the open tile marked ZZ?

 */

class Day20Spec : Spek({

    describe("part 1") {

        val simpleMazeString = """
                     A
                     A
              #######.#########
              #######.........#
              #######.#######.#
              #######.#######.#
              #######.#######.#
              #####  B    ###.#
            BC...##  C    ###.#
              ##.##       ###.#
              ##...DE  F  ###.#
              #####    G  ###.#
              #########.#####.#
            DE..#######...###.#
              #.#########.###.#
            FG..#########.....#
              ###########.#####
                         Z
                         Z 
        """.trimIndent()

        describe("parse maze to array") {
            it("should parse the maze to an array of chars") {
                val mazeArray = parseMazeToArray(simpleMazeString)
                mazeArray.size `should equal` 19
                mazeArray[0].size `should equal` 10
                mazeArray[2][2] `should equal` '#'
                mazeArray[2][9] `should equal` '.'
                mazeArray[7][9] `should equal` 'B'
            }
        }

        val simpleMazeArray = parseMazeToArray(simpleMazeString)

        describe("find portals") {
            it("should find portals for the simple example") {
                val portals = findPortals(simpleMazeArray)
                portals `should contain` Portal("AA", Coord2(9, 2))
                portals `should contain` Portal("FG", Coord2(11, 12))
                portals `should contain` Portal("BC", Coord2(2, 8))
            }
        }
        describe("find crossings") {
            it("should find crossings for the simple example") {
                val crossing = findCrossings(simpleMazeArray)
                crossing `should contain` Crossing(Coord2(9, 3))
            }
        }
    }
})

val neighborOffsets = listOf(Coord2(-1, 0), Coord2(1, 0), Coord2(0, -1), Coord2(0, 1))

fun findPortals(simpleMazeArray: List<List<Char>>): Set<Portal> = simpleMazeArray.coord2s().filter { simpleMazeArray[it] == '.' }
    .flatMap { coord2 ->
        neighborOffsets.map { neighborOffset ->
            val neighborCoord2 = coord2 + neighborOffset
            val c1 = simpleMazeArray.getOrElse(neighborCoord2) { ' ' }
            if (c1.isLetter()) {
                val c2 = simpleMazeArray.getOrElse(neighborCoord2 + neighborOffset) { ' ' }
                val portalName = if (neighborOffset.x > 0 || neighborOffset.y > 0) "$c1$c2"
                else  "$c2$c1" // reading reversed
                Portal(portalName, coord2)
            } else null
        }
}.filterNotNull().toSet()

fun findCrossings(simpleMazeArray: List<List<Char>>): Set<Crossing> = simpleMazeArray.coord2s().filter { simpleMazeArray[it] == '.' }
    .map { coord2 ->
        val connected = neighborOffsets.mapNotNull { neighborOffset ->
            val neighborCoord2 = coord2 + neighborOffset
            val c = simpleMazeArray.getOrElse(neighborCoord2) { ' ' }
            if (c == '.') neighborCoord2 else null
        }
        if (connected.size >= 3) Crossing(coord2) else null
    }.filterNotNull().toSet()

private fun <E> List<List<E>>.coord2s(): List<Coord2> = mapIndexed { y, row ->
    row.mapIndexed { x, _ -> Coord2(x, y )}
}.flatten()

private operator fun <E> List<List<E>>.get(coord2: Coord2): E  = get(coord2.y).get(coord2.x)
private fun <E> List<List<E>>.getOrElse(coord2: Coord2, default: (Int) -> E): E  = getOrElse(coord2.y, { emptyList() }).getOrElse(coord2.x, default)

data class Portal(val name: String, val coord: Coord2)
data class Crossing(val coord2: Coord2)

fun parseMazeToArray(simpleMazeString: String): List<List<Char>> = simpleMazeString.split("\n").map { it.toList() }
