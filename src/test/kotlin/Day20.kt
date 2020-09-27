import org.amshove.kluent.`should contain`
import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
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
                crossing.size `should equal` 2
            }
        }
        val simpleMazePointByCoord2 = (findCrossings(simpleMazeArray) + findPortals(simpleMazeArray)).map {
            it.coord2 to it
        }.toMap()

        describe("fallow path in maze") {
            it("should fallow a very short path") {
                val path = fallowPath(from = Coord2(9, 2), current = Coord2(9, 3), currentLength = 1,
                    mazeArray = simpleMazeArray, mazePointByCoord2 = simpleMazePointByCoord2)
                path.first `should equal` Crossing(Coord2(9, 3))
                path.second `should equal` 1
            }
            it("should fallow a longer path") {
                val path = fallowPath(from = Coord2(9, 3), current = Coord2(10, 3), currentLength = 2,
                    mazeArray = simpleMazeArray, mazePointByCoord2 = simpleMazePointByCoord2)
                path.first `should equal` Crossing(Coord2(13, 15))
                path.second `should equal` 25
            }
            it("should find all paths from a crossing") {
                val paths = fallowPaths(from = Coord2(9, 3),
                    mazeArray = simpleMazeArray, mazePointByCoord2 = simpleMazePointByCoord2)
                paths `should contain` (Portal("AA", Coord2(9, 2)) to 1)
                paths `should contain` (Portal("BC", Coord2(9, 6)) to 3)
                paths `should contain` (Crossing(Coord2(13, 15)) to 24)
            }
        }

        describe("find all connections") {
            it("should find all connections for the simple maze") {
                val connections = findAllConnections(simpleMazeArray, simpleMazePointByCoord2)
                connections[Coord2(9,2)] `should equal` setOf(
                        Crossing(Coord2(9, 3)) to 1
                )
                connections[Coord2(9,3)] `should equal` setOf(
                        Portal("AA", Coord2(9, 2)) to 1,
                        Portal("BC", Coord2(9, 6)) to 3,
                        Crossing(Coord2(13, 15)) to 24
                )
            }
        }

        val simpleMazeConnections = findAllConnections(simpleMazeArray, simpleMazePointByCoord2)
/*
        describe("find all shortest path from a point") {
            it("should find all shortest path from a point where no crossings are passed") {
                val shortestPaths = findAllShortestPathInMaze(Coord2(9, 3), simpleMazeConnections)
                shortestPaths.size `should equal` 26
            }
        }
        describe("find shortest path") {
            it("should find the shortest path from AA to ZZ") {
                val shortestPathLength = findShortestPathInMaze(Coord2(9, 2), Coord2(13, 16), simpleMazeConnections)
                shortestPathLength `should equal` 26
            }
        }
 */
    }
})

fun findShortestPathInMaze(from: Coord2, to: Coord2, mazeConnections: Map<Coord2, Set<Pair<MazePoint, Int>>>) = findAllShortestPathInMaze(from, mazeConnections)[to]

fun findAllShortestPathInMaze(from: Coord2, mazeConnections: Map<Coord2, Set<Pair<MazePoint, Int>>>): Map<Coord2, Int> {
    var discovered = mapOf( from to 0)
    while (true) {
        var newPathFound = false
        discovered = discovered.flatMap { (discoverdCoord2, discoveredLength) ->
            val nextConnections = mazeConnections[discoverdCoord2]
            println("nextConnections=$nextConnections")
            val h = if (nextConnections != null)
                nextConnections.map { (toMazePoint, length) ->
                    val totalLength = discoveredLength + length
                    val alreadyDescoveredLength = discovered[toMazePoint.coord2]
                    if (alreadyDescoveredLength != null && alreadyDescoveredLength < totalLength) toMazePoint.coord2 to alreadyDescoveredLength
                    else {
                        newPathFound = true
                        toMazePoint.coord2 to totalLength
                    }
                }
            else listOf(discoverdCoord2 to discoveredLength)
            println("h=$h")
            h
        }.toMap()
        if (! newPathFound) return discovered
    }
}

fun findAllConnections(mazeArray: List<List<Char>>, mazePointByCoord2: Map<Coord2, MazePoint>): Map<Coord2, Set<Pair<MazePoint, Int>>> {
    val mazePoints = findPortals(mazeArray) + findCrossings(mazeArray)
    return mazePoints.map { mazePoint ->
        val mazePointPaths = fallowPaths(mazePoint.coord2, mazeArray, mazePointByCoord2)
        mazePoint.coord2 to mazePointPaths
    }.toMap()
}

fun fallowPaths(
    from: Coord2,
    mazeArray: List<List<Char>>,
    mazePointByCoord2: Map<Coord2, MazePoint>): Set<Pair<MazePoint, Int>> {
        val connectedTos = from.passableNeighbors(mazeArray)
        return connectedTos.map { connectedTo ->
            fallowPath(from, connectedTo, 1, mazeArray, mazePointByCoord2)
        }.toSet()
}

fun fallowPath(
    from: Coord2,
    current: Coord2,
    currentLength: Int,
    mazeArray: List<List<Char>>,
    mazePointByCoord2: Map<Coord2, MazePoint>
): Pair<MazePoint, Int> {
    val connectedTo = mazePointByCoord2[current]
    return if (connectedTo != null) connectedTo to currentLength
    else {
        val next = (current.passableNeighbors(mazeArray) - from).first()
        fallowPath(current, next, currentLength + 1, mazeArray, mazePointByCoord2)
    }
}

fun findPortals(simpleMazeArray: List<List<Char>>): Set<Portal> = simpleMazeArray.coord2s().filter { simpleMazeArray[it] == '.' }
    .flatMap { coord2 ->
        Coord2.neighborOffsets.map { neighborOffset ->
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

fun findCrossings(mazeArray: List<List<Char>>): Set<Crossing> = mazeArray.coord2s().filter { mazeArray[it] == '.' }
    .map { coord2 ->
        val connected = coord2.passableNeighbors(mazeArray)
        if (connected.size >= 3) Crossing(coord2) else null
    }.filterNotNull().toSet()

private fun <E> List<List<E>>.coord2s(): List<Coord2> = mapIndexed { y, row ->
    row.mapIndexed { x, _ -> Coord2(x, y )}
}.flatten()

private operator fun <E> List<List<E>>.get(coord2: Coord2): E  = get(coord2.y).get(coord2.x)
private fun <E> List<List<E>>.getOrElse(coord2: Coord2, default: (Int) -> E): E  = getOrElse(coord2.y, { emptyList() }).getOrElse(coord2.x, default)
private fun Coord2.passableNeighbors(mazeArray: List<List<Char>>) = neighbors().filter { neighborCoord2 ->
    val c = mazeArray.getOrElse(neighborCoord2) { ' ' }
    c == '.'
}

interface MazePoint { val coord2: Coord2 }
data class Portal(val name: String, override val coord2: Coord2): MazePoint
data class Crossing(override val coord2: Coord2): MazePoint

fun parseMazeToArray(simpleMazeString: String): List<List<Char>> = simpleMazeString.split("\n").map { it.toList() }
