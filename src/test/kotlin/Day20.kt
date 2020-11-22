import org.amshove.kluent.`should be null`
import org.amshove.kluent.`should contain`
import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.lang.IllegalStateException

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

--- Part Two ---

Strangely, the exit isn't open when you reach it.
Then, you remember: the ancient Plutonians were famous for building recursive spaces.

The marked connections in the maze aren't portals:
they physically connect to a larger or smaller copy of the maze.
Specifically, the labeled tiles around the inside edge actually connect to a smaller copy of the same maze,
and the smaller copy's inner labeled tiles connect to yet a smaller copy, and so on.

When you enter the maze, you are at the outermost level;
when at the outermost level, only the outer labels AA and ZZ function (as the start and end, respectively);
all other outer labeled tiles are effectively walls.
At any other level, AA and ZZ count as walls, but the other outer labeled tiles bring you one level outward.

Your goal is to find a path through the maze that brings you back to ZZ at the outermost level of the maze.

In the first example above, the shortest path is now the loop around the right side.
If the starting level is 0, then taking the previously-shortest path would pass through BC (to level 1),
DE (to level 2), and FG (back to level 1).
Because this is not the outermost level, ZZ is a wall, and the only option is to go back around to BC,
which would only send you even deeper into the recursive maze.

In the second example above, there is no path that brings you to ZZ at the outermost level.

Here is a more interesting example:

             Z L X W       C
             Z P Q B       K
  ###########.#.#.#.#######.###############
  #...#.......#.#.......#.#.......#.#.#...#
  ###.#.#.#.#.#.#.#.###.#.#.#######.#.#.###
  #.#...#.#.#...#.#.#...#...#...#.#.......#
  #.###.#######.###.###.#.###.###.#.#######
  #...#.......#.#...#...#.............#...#
  #.#########.#######.#.#######.#######.###
  #...#.#    F       R I       Z    #.#.#.#
  #.###.#    D       E C       H    #.#.#.#
  #.#...#                           #...#.#
  #.###.#                           #.###.#
  #.#....OA                       WB..#.#..ZH
  #.###.#                           #.#.#.#
CJ......#                           #.....#
  #######                           #######
  #.#....CK                         #......IC
  #.###.#                           #.###.#
  #.....#                           #...#.#
  ###.###                           #.#.#.#
XF....#.#                         RF..#.#.#
  #####.#                           #######
  #......CJ                       NM..#...#
  ###.#.#                           #.###.#
RE....#.#                           #......RF
  ###.###        X   X       L      #.#.#.#
  #.....#        F   Q       P      #.#.#.#
  ###.###########.###.#######.#########.###
  #.....#...#.....#.......#...#.....#.#...#
  #####.#.###.#######.#######.###.###.#.#.#
  #.......#.......#.#.#.#.#...#...#...#.#.#
  #####.###.#####.#.#.#.#.###.###.#.###.###
  #.......#.....#.#...#...............#...#
  #############.#.#.###.###################
               A O F   N
               A A D   M

One shortest path through the maze is the following:

Walk from AA to XF (16 steps)
Recurse into level 1 through XF (1 step)
Walk from XF to CK (10 steps)
Recurse into level 2 through CK (1 step)
Walk from CK to ZH (14 steps)
Recurse into level 3 through ZH (1 step)
Walk from ZH to WB (10 steps)
Recurse into level 4 through WB (1 step)
Walk from WB to IC (10 steps)
Recurse into level 5 through IC (1 step)
Walk from IC to RF (10 steps)
Recurse into level 6 through RF (1 step)
Walk from RF to NM (8 steps)
Recurse into level 7 through NM (1 step)
Walk from NM to LP (12 steps)
Recurse into level 8 through LP (1 step)
Walk from LP to FD (24 steps)
Recurse into level 9 through FD (1 step)
Walk from FD to XQ (8 steps)
Recurse into level 10 through XQ (1 step)
Walk from XQ to WB (4 steps)
Return to level 9 through WB (1 step)
Walk from WB to ZH (10 steps)
Return to level 8 through ZH (1 step)
Walk from ZH to CK (14 steps)
Return to level 7 through CK (1 step)
Walk from CK to XF (10 steps)
Return to level 6 through XF (1 step)
Walk from XF to OA (14 steps)
Return to level 5 through OA (1 step)
Walk from OA to CJ (8 steps)
Return to level 4 through CJ (1 step)
Walk from CJ to RE (8 steps)
Return to level 3 through RE (1 step)
Walk from RE to IC (4 steps)
Recurse into level 4 through IC (1 step)
Walk from IC to RF (10 steps)
Recurse into level 5 through RF (1 step)
Walk from RF to NM (8 steps)
Recurse into level 6 through NM (1 step)
Walk from NM to LP (12 steps)
Recurse into level 7 through LP (1 step)
Walk from LP to FD (24 steps)
Recurse into level 8 through FD (1 step)
Walk from FD to XQ (8 steps)
Recurse into level 9 through XQ (1 step)
Walk from XQ to WB (4 steps)
Return to level 8 through WB (1 step)
Walk from WB to ZH (10 steps)
Return to level 7 through ZH (1 step)
Walk from ZH to CK (14 steps)
Return to level 6 through CK (1 step)
Walk from CK to XF (10 steps)
Return to level 5 through XF (1 step)
Walk from XF to OA (14 steps)
Return to level 4 through OA (1 step)
Walk from OA to CJ (8 steps)
Return to level 3 through CJ (1 step)
Walk from CJ to RE (8 steps)
Return to level 2 through RE (1 step)
Walk from RE to XQ (14 steps)
Return to level 1 through XQ (1 step)
Walk from XQ to FD (8 steps)
Return to level 0 through FD (1 step)
Walk from FD to ZZ (18 steps)

This path takes a total of 396 steps to move from AA at the outermost layer to ZZ at the outermost layer.

In your maze, when accounting for recursion,
how many steps does it take to get from the open tile marked AA to the open tile marked ZZ, both at the outermost layer?

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
                val mazeArray = Maze(simpleMazeString).mazeArray
                mazeArray.size `should equal` 19
                mazeArray[0].size `should equal` 10
                mazeArray[2][2] `should equal` '#'
                mazeArray[2][9] `should equal` '.'
                mazeArray[7][9] `should equal` 'B'
            }
        }
        val simpleMaze = Maze(simpleMazeString)

        describe("find portals") {
            it("should find portals for the simple example") {
                val portals = simpleMaze.portals
                portals `should contain` Portal("AA", Coord2(9, 2))
                portals `should contain` Portal("FG", Coord2(11, 12))
                portals `should contain` Portal("BC", Coord2(2, 8))
            }
        }
        describe("find crossings") {
            it("should find crossings for the simple example") {
                val crossing = simpleMaze.crossings
                crossing `should contain` Crossing(Coord2(9, 3))
                crossing.size `should equal` 2
            }
        }
        describe("find connected portal") {
            it("should find no connected portal for the start portal") {
                val verySimpleMazeString = """
                             A
                             A
                      #######.#########
                      #######.#########
                      #      B        #
                      #      B        #
                      #               #
                      #      B        #
                      #      B        #
                      #######.#########
                      #######.#########
                             Z
                             Z 
                """.trimIndent()
                val verySimpleMaze = Maze(verySimpleMazeString)
                println("portals=${verySimpleMaze.portals}")
                val portalAA = verySimpleMaze.pointsByCoord[Coord2(7, 2)]
                verySimpleMaze.portalTo[portalAA].`should be null`()
                val portalBB1 = verySimpleMaze.pointsByCoord[Coord2(7, 3)]
                portalBB1 `should equal` Portal("BB", Coord2(7, 3))
                val portalBB2 = verySimpleMaze.portalTo[portalBB1]
                portalBB2 `should equal` Portal("BB", Coord2(7, 9))
            }

        }

        describe("fallow path in maze") {
            it("should fallow a very short path") {
                val path = simpleMaze.fallowPath(from = Coord2(9, 2), current = Coord2(9, 3), currentLength = 1)!!
                path.first `should equal` Crossing(Coord2(9, 3))
                path.second `should equal` 1
            }
            it("should fallow a longer path") {
                val path = simpleMaze.fallowPath(from = Coord2(9, 3), current = Coord2(10, 3), currentLength = 2)!!
                path.first `should equal` Crossing(Coord2(13, 15))
                path.second `should equal` 25
            }
            it("should find all paths from a crossing") {
                val paths = simpleMaze.fallowPaths(Coord2(9, 3))
                paths `should contain` (Portal("AA", Coord2(9, 2)) to 1)
                paths `should contain` (Portal("BC", Coord2(9, 6)) to 3)
                paths `should contain` (Crossing(Coord2(13, 15)) to 24)
            }
        }

        describe("find all connections") {
            it("should find all connections for the simple maze") {
                val connections = simpleMaze.directConnections
                connections[Coord2(9, 2)] `should equal` setOf(
                        Crossing(Coord2(9, 3)) to 1
                )
                connections[Coord2(9, 3)] `should equal` setOf(
                        Portal("AA", Coord2(9, 2)) to 1,
                        Portal("BC", Coord2(9, 6)) to 3,
                        Crossing(Coord2(13, 15)) to 24
                )
            }
        }

        describe("find start") {
            it("should find the start") {
                val verySimpleMazeString = """
                             A
                             A
                      #######.#########
                      #######.....#####
                      ###########.#####
                      ###########.#####
                                 Z
                                 Z 
                """.trimIndent()
                val verySimpleMaze = Maze(verySimpleMazeString)
                verySimpleMaze.start `should equal` Portal("AA", Coord2(7, 2))
            }
        }

        describe("find all shortest path from a point") {
            it("should find the (shortest) path when there is only one path") {
                val verySimpleMazeString = """
                             A
                             A
                      #######.#########
                      #######.....#####
                      ###########.#####
                      ###########.#####
                                 Z
                                 Z 
                """.trimIndent()
                val verySimpleMaze = Maze(verySimpleMazeString)
                val shortestPaths = findAllShortestPathInMaze(verySimpleMaze.start!!, verySimpleMaze)
                shortestPaths `should equal` mapOf(
                        Coord2(7, 2) to Path(0, emptyList()),
                        Coord2(11, 5) to Path(7, listOf(Portal("ZZ", Coord2(11, 5)) to 7))
                )
            }
            it("should use a portal") {
                val verySimpleMazeString = """
                             A
                             A
                      #######.#########
                      #######.#########
                      #      B        #
                      #      B        #
                      #               #
                      #      B        #
                      #      B        #
                      #######.#########
                      #######.#########
                             Z
                             Z 
                """.trimIndent()
                val verySimpleMaze = Maze(verySimpleMazeString)
                val shortestPaths = findAllShortestPathInMaze(verySimpleMaze.start!!, verySimpleMaze)
                shortestPaths `should equal` mapOf(
                        Coord2(7, 2) to Path(0, emptyList()),
                        Coord2(7, 3) to Path(1, listOf(
                                Portal("BB", Coord2(7, 3)) to 1
                        )),
                        Coord2(7, 9) to Path(2, listOf(
                                Portal("BB", Coord2(7, 3)) to 1,
                                Portal("BB", Coord2(7, 9)) to 1,
                        )),
                        Coord2(7, 10) to Path(3, listOf(
                                Portal("BB", Coord2(7, 3)) to 1,
                                Portal("BB", Coord2(7, 9)) to 1,
                                Portal("ZZ", Coord2(7, 10)) to 1,
                        )),
                )
            }
            it("should find all (shortest) pathes when there are two portals and a crossing") {
                val verySimpleMazeString = """
                             A
                             A
                      #######.#########
                      #######.....#####
                      ###########......BB
                      ###########.#####
                                 Z
                                 Z 
                """.trimIndent()
                val verySimpleMaze = Maze(verySimpleMazeString)
                val shortestPaths = findAllShortestPathInMaze(verySimpleMaze.start!!, verySimpleMaze)
                shortestPaths `should equal` mapOf(
                        Coord2(7, 2) to Path(0, emptyList()),
                        Coord2(11, 5) to Path(7, listOf(
                                Crossing(Coord2(11, 4)) to 6,
                                Portal("ZZ", Coord2(11, 5)) to 1)
                        ),
                        Coord2(11, 4) to Path(6, listOf(Crossing(Coord2(11, 4)) to 6)),
                        Coord2(16, 4) to Path(11, listOf(
                                Crossing(Coord2(11, 4)) to 6,
                                Portal("BB", Coord2(16, 4)) to 5)
                        ),
                )
            }
            it("should find the (shortest) path with a dead end") {
                val verySimpleMazeString = """
                             A
                             A
                      #######.#########
                      #####.......#####
                      ###########.#####
                      ###########.#####
                                 Z
                                 Z 
                """.trimIndent()
                val verySimpleMaze = Maze(verySimpleMazeString)
                val shortestPaths = findAllShortestPathInMaze(verySimpleMaze.start!!, verySimpleMaze)
                shortestPaths `should equal` mapOf(
                        Coord2(7, 2) to Path(0, emptyList()),
                        Coord2(7, 3) to Path(1, listOf(Crossing(Coord2(7, 3)) to 1)),
                        Coord2(11, 5) to Path(7, listOf(Crossing(Coord2(x = 7, y = 3)) to 1, Portal("ZZ", Coord2(11, 5)) to 6))
                )
            }
            it("should find shortest paths for the simple maze") {
                val shortestPaths = findAllShortestPathInMaze(simpleMaze.start!!, simpleMaze)
                val shortestPathFromStartToEnd = shortestPaths.getValue(simpleMaze.end!!.coord2)
                println(shortestPathFromStartToEnd)
                shortestPathFromStartToEnd.length `should equal` 23
                shortestPathFromStartToEnd `should equal` Path(length = 23, points = listOf(
                        Crossing(coord2 = Coord2(x = 9, y = 3)) to 1,
                        Portal(name = "BC", coord2 = Coord2(x = 9, y = 6)) to 3,
                        Portal(name = "BC", coord2 = Coord2(x = 2, y = 8)) to 1,
                        Portal(name = "DE", coord2 = Coord2(x = 6, y = 10)) to 6,
                        Portal(name = "DE", coord2 = Coord2(x = 2, y = 13)) to 1,
                        Portal(name = "FG", coord2 = Coord2(x = 2, y = 15)) to 4,
                        Portal(name = "FG", coord2 = Coord2(x = 11, y = 12)) to 1,
                        Crossing(coord2 = Coord2(x = 13, y = 15)) to 5,
                        Portal(name = "ZZ", coord2 = Coord2(x = 13, y = 16)) to 1
                ))
            }
            it("should find shortest pathes for the bigger maze") {
                val biggerMazeString = """
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
                    
                """.trimIndent()
                val biggerMaze = Maze(biggerMazeString)
                val shortestPaths = findAllShortestPathInMaze(biggerMaze.start!!, biggerMaze)
                val shortestPathFromStartToEnd = shortestPaths.getValue(biggerMaze.end!!.coord2)
                println(shortestPathFromStartToEnd)
                shortestPathFromStartToEnd.length `should equal` 58
            }
        }
        describe("find the shortestPath in the exercise") {
            val input = readResource("day20Input.txt")!!
            val maze = Maze(input)
            it("should find the shortes path from start to end") {
                val shortestPaths = findAllShortestPathInMaze(maze.start!!, maze)
                val shortestPathFromStartToEnd = shortestPaths.getValue(maze.end!!.coord2)
                shortestPathFromStartToEnd.length `should equal` 588
            }
        }
    }
    describe("part 2") {

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
        val simpleMaze = Maze(simpleMazeString)

        it("should find path when there is only one") {
            val verySimpleMazeString = """
                             A
                             A
                      #######.#########
                      #######.....#####
                      ###########.#####
                      ###########.#####
                                 Z
                                 Z 
                """.trimIndent()
            val verySimpleMaze = Maze(verySimpleMazeString)
            val paths = findAllPathsInMaze(verySimpleMaze.start!!, verySimpleMaze.end!!, verySimpleMaze)
            paths `should equal` setOf(
                    Path(7, listOf(Portal("ZZ", Coord2(11, 5)) to 7))
            )
        }

        it("should find all paths for the simple maze from start to end") {
            val paths = findAllPathsInMaze(simpleMaze.start!!, simpleMaze.end!!, simpleMaze)
            println(paths)
            paths.size `should equal` 2
            paths `should equal` setOf(
                    Path(length = 23, points = listOf(
                            Crossing(coord2 = Coord2(x = 9, y = 3)) to 1,
                            Portal(name = "BC", coord2 = Coord2(x = 9, y = 6)) to 3,
                            Portal(name = "BC", coord2 = Coord2(x = 2, y = 8)) to 1,
                            Portal(name = "DE", coord2 = Coord2(x = 6, y = 10)) to 6,
                            Portal(name = "DE", coord2 = Coord2(x = 2, y = 13)) to 1,
                            Portal(name = "FG", coord2 = Coord2(x = 2, y = 15)) to 4,
                            Portal(name = "FG", coord2 = Coord2(x = 11, y = 12)) to 1,
                            Crossing(coord2 = Coord2(x = 13, y = 15)) to 5,
                            Portal(name = "ZZ", coord2 = Coord2(x = 13, y = 16)) to 1
                    )),
                    Path(length = 26, points = listOf(
                            Crossing(coord2 = Coord2(x = 9, y = 3)) to 1,
                            Crossing(coord2 = Coord2(x = 13, y = 15)) to 24,
                            Portal(name = "ZZ", coord2 = Coord2(x = 13, y = 16)) to 1
                    )),
            )
        }
        describe("find the shortestPath in the recursive exercise") {
            val input = readResource("day20Input.txt")!!
            val maze = Maze(input)
            it("should find the all path from start to end") {
                val paths = findAllPathsInMaze(maze.start!!, maze.end!!, maze)
                paths.size `should equal` 4
            }
        }
    }

})

fun findAllShortestPathInMaze(from: Portal, maze: Maze): Map<Coord2, Path> {
    val mazeConnections = maze.directConnectionsWithPortals
    var discovered = mapOf(from.coord2 to Path(0, emptyList()))
    do {
        val newDiscovered = discovered.flatMap { (discoverdCoord2, discoveredPath) ->
            val nextConnections = mazeConnections[discoverdCoord2]
            if (nextConnections != null)
                nextConnections.mapNotNull { (toMazePoint, length) ->
                    val totalLength = discoveredPath.length + length
                    val nextPath = Path(totalLength, discoveredPath.points + listOf(toMazePoint to length))
                    val alreadyDiscoveredPath = discovered[toMazePoint.coord2]
                    if (alreadyDiscoveredPath == null) {
                        toMazePoint.coord2 to nextPath
                    } else { // Check if the newly detected path is shorter
                        if (alreadyDiscoveredPath.length > totalLength) {
                            toMazePoint.coord2 to nextPath
                        } else null
                    }
                }
            else emptyList()
        }.toMap()
        discovered = discovered + newDiscovered
    } while (newDiscovered.isNotEmpty())
    return discovered
}

fun findAllPathsInMaze(from: Portal, to: Portal, maze: Maze): Set<Path> {
    val mazeConnections = maze.directConnectionsWithPortals
    val discovered = mutableMapOf(from.coord2 to mutableSetOf(Path(0, emptyList())))
    val discoveredTo = mutableSetOf<Path>()
    while (true) {
        var newPathFound = false
        discovered.toList().flatMap { (discoverdCoord2, discoveredPaths) ->
            discoveredPaths.map { discoveredPath ->
                val nextConnections = mazeConnections[discoverdCoord2]
                if (nextConnections != null)
                    nextConnections.map { (toMazePoint, length) ->
                        if (! discoveredPath.points.map { it.first}.contains(toMazePoint)) {
                            val totalLength = discoveredPath.length + length
                            val nextPath = Path(totalLength, discoveredPath.points + listOf(toMazePoint to length))
                            val alreadyDiscoveredPaths = discovered[toMazePoint.coord2]
                            if (alreadyDiscoveredPaths == null) {
                                newPathFound = true
                                discovered[toMazePoint.coord2] = mutableSetOf(nextPath)
                            } else { // Check if a new path has been found
                                if (!alreadyDiscoveredPaths.contains(nextPath)) {
                                    newPathFound = true
                                    alreadyDiscoveredPaths += nextPath
                                }
                            }
                            if (toMazePoint == to) discoveredTo += nextPath
                        }
                    }
            }
        }
        if (! newPathFound) return discoveredTo
    }
}

private fun <E> List<List<E>>.coord2s(): List<Coord2> = mapIndexed { y, row ->
    row.mapIndexed { x, _ -> Coord2(x, y )}
}.flatten()

private operator fun <E> List<List<E>>.get(coord2: Coord2): E  = get(coord2.y)[coord2.x]
private fun <E> List<List<E>>.getOrElse(coord2: Coord2, default: (Int) -> E): E  = getOrElse(coord2.y) { emptyList() }.getOrElse(coord2.x, default)
private fun Coord2.passableNeighbors(mazeArray: List<List<Char>>) = neighbors().filter { neighborCoord2 ->
    val c = mazeArray.getOrElse(neighborCoord2) { ' ' }
    c == '.'
}

interface MazePoint { val coord2: Coord2 }
data class Portal(val name: String, override val coord2: Coord2): MazePoint
data class Crossing(override val coord2: Coord2): MazePoint

data class Path(val length: Int, val points: List<Pair<MazePoint, Int>>)

class Maze(private val mazeString: String) {
    val mazeArray: List<List<Char>> by lazy { mazeString.split("\n").map { it.toList() } }
    val portals: Set<Portal> by lazy {
        mazeArray.coord2s().filter { mazeArray[it] == '.' }
            .flatMap { coord2 ->
                Coord2.neighborOffsets.map { neighborOffset ->
                    val neighborCoord2 = coord2 + neighborOffset
                    val c1 = mazeArray.getOrElse(neighborCoord2) { ' ' }
                    if (c1.isLetter()) {
                        val c2 = mazeArray.getOrElse(neighborCoord2 + neighborOffset) { ' ' }
                        val portalName = if (neighborOffset.x > 0 || neighborOffset.y > 0) "$c1$c2"
                        else  "$c2$c1" // reading reversed
                        Portal(portalName, coord2)
                    } else null
                }
            }.filterNotNull().toSet()
    }
    val portalTo: Map<Portal, Portal> by lazy {
        portals.filter { it.name != "AA" && it.name != "ZZ" }
            .toList()
            .groupBy { it.name }
            .flatMap {(_, portals) ->
                val portal1 = portals.getOrNull(0)
                val portal2 = portals.getOrNull(1)
                if (portal1 != null && portal2 != null)
                    listOf(portal1 to portal2, portal2 to portal1) // Connect portals
                else emptyList()
            }
            .toMap()
    }
    val crossings: Set<Crossing> by lazy {
        mazeArray.coord2s().filter { mazeArray[it] == '.' }.mapNotNull { coord2 ->
            val connected = coord2.passableNeighbors(mazeArray)
            if (connected.size >= 3) Crossing(coord2) else null
        }.toSet()
    }
    val mazePoints by lazy {
        portals + crossings
    }
    val start: Portal? by lazy {
        portals.find { it.name == "AA" }
    }
    val end: Portal? by lazy {
        portals.find { it.name == "ZZ" }
    }
    val pointsByCoord: Map<Coord2, MazePoint> by lazy {
        (crossings + portals).map {
            it.coord2 to it
        }.toMap()
    }
    val directConnections: Map<Coord2, Set<Pair<MazePoint, Int>>> by lazy {
        mazePoints.map { mazePoint ->
            val mazePointPaths = fallowPaths(mazePoint.coord2)
            mazePoint.coord2 to mazePointPaths
        }.toMap()
    }
    val directConnectionsWithPortals: Map<Coord2, Set<Pair<MazePoint, Int>>> by lazy {
        directConnections.map { (coord, connections) ->
            val mazePoint = pointsByCoord[coord]
            val connectedPortal = if (mazePoint != null && mazePoint is Portal)
                    portalTo[mazePoint]
                else null
            val connectionsWithPortals = if (connectedPortal != null) {
                connections + setOf(connectedPortal to 1)
            } else connections
            coord to connectionsWithPortals
        }.toMap()
    }

    fun fallowPaths(from: Coord2): Set<Pair<MazePoint, Int>> {
        val connectedTos = from.passableNeighbors(mazeArray)
        return connectedTos.mapNotNull { connectedTo ->
            fallowPath(from, connectedTo, 1)
        }.toSet()
    }

    tailrec fun fallowPath(
        from: Coord2,
        current: Coord2,
        currentLength: Int
    ): Pair<MazePoint, Int>? {
        val connectedTo = pointsByCoord[current]
        return if (connectedTo != null) connectedTo to currentLength
        else {
            val next = current.passableNeighbors(mazeArray) - from
            when {
                next.isEmpty() -> null // Dead end
                next.size > 1 -> throw IllegalStateException("Only known maze points should have more than one connections current=$current $next")
                else -> fallowPath(current, next.first(), currentLength + 1)
            }
        }
    }
}