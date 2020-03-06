import org.amshove.kluent.`should be greater than`
import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.xit
import org.jetbrains.spek.data_driven.data
import kotlin.math.PI
import org.jetbrains.spek.data_driven.on as onData


/*
--- Day 18: Many-Worlds Interpretation ---

As you approach Neptune, a planetary security system detects you and activates a giant tractor beam on Triton!
You have no choice but to land.

A scan of the local area reveals only one interesting feature: a massive underground vault.
You generate a map of the tunnels (your puzzle input). The tunnels are too narrow to move diagonally.

Only one entrance (marked @) is present among the open passages (marked .) and stone walls (#),
but you also detect an assortment of keys (shown as lowercase letters) and doors (shown as uppercase letters).
Keys of a given letter open the door of the same letter: a opens A, b opens B, and so on.
You aren't sure which key you need to disable the tractor beam, so you'll need to collect all of them.

For example, suppose you have the following map:

#########
#b.A.@.a#
#########

Starting from the entrance (@), you can only access a large door (A) and a key (a).
Moving toward the door doesn't help you, but you can move 2 steps to collect the key, unlocking A in the process:

#########
#b.....@#
#########

Then, you can move 6 steps to collect the only other key, b:

#########
#@......#
#########

So, collecting every key took a total of 8 steps.

Here is a larger example:

########################
#f.D.E.e.C.b.A.@.a.B.c.#
######################.#
#d.....................#
########################

The only reasonable move is to take key a and unlock door A:

########################
#f.D.E.e.C.b.....@.B.c.#
######################.#
#d.....................#
########################

Then, do the same with key b:

########################
#f.D.E.e.C.@.........c.#
######################.#
#d.....................#
########################

...and the same with key c:

########################
#f.D.E.e.............@.#
######################.#
#d.....................#
########################

Now, you have a choice between keys d and e.
While key e is closer, collecting it now would be slower in the long run than collecting key d first,
so that's the best choice:

########################
#f...E.e...............#
######################.#
#@.....................#
########################

Finally, collect key e to unlock door E, then collect key f, taking a grand total of 86 steps.

Here are a few more examples:

########################
#...............b.C.D.f#
#.######################
#.....@.a.B.c.d.A.e.F.g#
########################

Shortest path is 132 steps: b, a, c, d, f, e, g

#################
#i.G..c...e..H.p#
########.########
#j.A..b...f..D.o#
########@########
#k.E..a...g..B.n#
########.########
#l.F..d...h..C.m#
#################

Shortest paths are 136 steps;
one is: a, f, b, j, g, n, h, d, l, o, e, p, c, i, k, m

########################
#@..............ac.GI.b#
###d#e#f################
###A#B#C################
###g#h#i################
########################

Shortest paths are 81 steps; one is: a, c, f, i, d, g, b, e, h

How many steps is the shortest path that collects all of the keys?

--- Part Two ---

You arrive at the vault only to discover that there is not one vault, but four - each with its own entrance.

On your map, find the area in the middle that looks like this:

...
.@.
...

Update your map to instead use the correct data:

@#@
###
@#@

This change will split your map into four separate sections, each with its own entrance:

#######       #######
#a.#Cd#       #a.#Cd#
##...##       ##@#@##
##.@.##  -->  #######
##...##       ##@#@##
#cB#Ab#       #cB#Ab#
#######       #######

Because some of the keys are for doors in other vaults,
it would take much too long to collect all of the keys by yourself.
Instead, you deploy four remote-controlled robots. Each starts at one of the entrances (@).

Your goal is still to collect all of the keys in the fewest steps, but now,
each robot has its own position and can move independently.
You can only remotely control a single robot at a time.
Collecting a key instantly unlocks any corresponding doors, regardless of the vault in which the key or door is found.

For example, in the map above, the top-left robot first collects key a, unlocking door A in the bottom-right vault:

#######
#@.#Cd#
##.#@##
#######
##@#@##
#cB#.b#
#######

Then, the bottom-right robot collects key b, unlocking door B in the bottom-left vault:

#######
#@.#Cd#
##.#@##
#######
##@#.##
#c.#.@#
#######

Then, the bottom-left robot collects key c:

#######
#@.#.d#
##.#@##
#######
##.#.##
#@.#.@#
#######

Finally, the top-right robot collects key d:

#######
#@.#.@#
##.#.##
#######
##.#.##
#@.#.@#
#######

In this example, it only took 8 steps to collect all of the keys.

Sometimes, multiple robots might have keys available, or a robot might have to wait for multiple keys to be collected:

###############
#d.ABC.#.....a#
######@#@######
###############
######@#@######
#b.....#.....c#
###############

First, the top-right, bottom-left, and bottom-right robots take turns collecting
keys a, b, and c, a total of 6 + 6 + 6 = 18 steps.
Then, the top-left robot can access key d, spending another 6 steps;
collecting all of the keys here takes a minimum of 24 steps.

Here's a more complex example:

#############
#DcBa.#.GhKl#
#.###@#@#I###
#e#d#####j#k#
###C#@#@###J#
#fEbA.#.FgHi#
#############

Top-left robot collects key a.
Bottom-left robot collects key b.
Top-left robot collects key c.
Bottom-left robot collects key d.
Top-left robot collects key e.
Bottom-left robot collects key f.
Bottom-right robot collects key g.
Top-right robot collects key h.
Bottom-right robot collects key i.
Top-right robot collects key j.
Bottom-right robot collects key k.
Top-right robot collects key l.

In the above example, the fewest steps to collect all of the keys is 32.

Here's an example with more choices:

#############
#g#f.D#..h#l#
#F###e#E###.#
#dCba@#@BcIJ#
#############
#nK.L@#@G...#
#M###N#H###.#
#o#m..#i#jk.#
#############

One solution with the fewest steps is:

Top-left robot collects key e.
Top-right robot collects key h.
Bottom-right robot collects key i.
Top-left robot collects key a.
Top-left robot collects key b.
Top-right robot collects key c.
Top-left robot collects key d.
Top-left robot collects key f.
Top-left robot collects key g.
Bottom-right robot collects key k.
Bottom-right robot collects key j.
Top-right robot collects key l.
Bottom-left robot collects key n.
Bottom-left robot collects key m.
Bottom-left robot collects key o.

This example requires at least 72 steps to collect all keys.

After updating your map and using the remote-controlled robots,
what is the fewest steps necessary to collect all of the keys?

 */

class Day18Spec : Spek({

    describe("part 1") {
        describe("parse a simple triton map") {
            val input = """
                #.bB@
                #
            """.trimIndent()
            val tritonMap = input.parseTritonMap()
            it("should be parsed correctly") {
                tritonMap `should equal` listOf(
                    listOf(
                        Wall(0, 0), Empty(1, 0), Key('b', 2, 0), Door('B', 3, 0), Entrance(4, 0)
                    ),
                    listOf(
                        Wall(0, 1)
                    )
                )
            }
        }
        describe("find points of interest without intersections") {
            val input = """
                #########
                #b.A.@.a#
                #########
            """.trimIndent()
            val tritonMap = input.parseTritonMap()
            val pois = tritonMap.findPois()
            it("should have found all points of interests") {
                pois `should equal` setOf(
                    Key('b', 1, 1), Door('A', 3, 1), Entrance(5, 1), Key('a', 7, 1)
                )
            }
        }
        describe("find points of interest with an intersection") {
            val input = """
                #########
                #b.A.@.a#
                ####.####
                #.......#
                #########
            """.trimIndent()
            val tritonMap = input.parseTritonMap()
            val pois = tritonMap.findPois()
            it("should have found all points of interests") {
                pois `should equal` setOf(
                    Key('b', 1, 1), Door('A', 3, 1), Intersection(4, 1), Entrance(5, 1), Key('a', 7, 1), Intersection(4, 3), DeadEnd(x=1, y=3), DeadEnd(x=7, y=3)
                )
            }
        }
        describe("find connections with intersections") {
            val input = """
                #########
                #b.A.@..#
                ###.###.#
                #....B#a#
                #########
            """.trimIndent()
            val tritonMapWithoutIntersections = input.parseTritonMap()
            val pois = tritonMapWithoutIntersections.findPois()
            val tritonMap = tritonMapWithoutIntersections.replaceIntersections(pois)
            val connections = findConnections(tritonMap, pois)
            it("should have found all connections") {
                connections `should equal` mapOf(
                    Coord2(1, 1) to setOf(PoiConnection(Coord2(3, 1), 2)),
                    Coord2(3, 1) to setOf(PoiConnection(Coord2(1, 1), 2), PoiConnection(Coord2(5, 1), 2), PoiConnection(Coord2(3, 3), 2)),
                    Coord2(5, 1) to setOf(PoiConnection(Coord2(3, 1), 2), PoiConnection(Coord2(7, 3), 4)),
                    Coord2(3, 3) to setOf(PoiConnection(Coord2(3, 1), 2), PoiConnection(Coord2(1, 3), 2), PoiConnection(Coord2(5, 3), 2)),
                    Coord2(1, 3) to setOf(PoiConnection(Coord2(3, 3), 2)),
                    Coord2(5, 3) to setOf(PoiConnection(Coord2(3, 3), 2)),
                    Coord2(7, 3) to setOf(PoiConnection(Coord2(5, 1), 4))
                )
            }
        }
        describe("find shortest steps") {
            val testData = arrayOf(
                data("""
                    #########
                    #b.A.@.a#
                    #########
                """.trimIndent(), 8),
                data("""
                    ########################
                    #f.D.E.e.C.b.A.@.a.B.c.#
                    ######################.#
                    #d.....................#
                    ########################
                """.trimIndent(), 86),
                data("""
                    ########################
                    #...............b.C.D.f#
                    #.######################
                    #.....@.a.B.c.d.A.e.F.g#
                    ########################
                """.trimIndent(), 132),
                data("""
                    #################
                    #i.G..c...e..H.p#
                    ########.########
                    #j.A..b...f..D.o#
                    ########@########
                    #k.E..a...g..B.n#
                    ########.########
                    #l.F..d...h..C.m#
                    #################
                """.trimIndent(), 136),
                data("""
                    ########################
                    #@..............ac.GI.b#
                    ###d#e#f################
                    ###A#B#C################
                    ###g#h#i################
                    ########################
                """.trimIndent(), 81)
            )
            onData("triton map %s ", with = *testData) { input, expected ->
                val paths = findShortestPath(input).findBestSolution()
                val length = paths.flatten().sumBy { it.dist }
                it("should have found the shortest path") {
                    length `should equal` expected
                }
            }
        }
        given("exercise") {
            val input = readResource("day18Input.txt")!!
            it("should find the shortest path") {
                val paths = findShortestPath(input).findBestSolution()
                val length = paths.flatten().sumBy { it.dist }
                length `should equal` 4762
            }
        }
    }
    describe("part 2") {
        describe("find shortest steps with four entrances") {
            val testData = arrayOf(/*
                data("""
                    #######
                    #a.#Cd#
                    ##@#@##
                    #######
                    ##@#@##
                    #cB#Ab#
                    #######
                """.trimIndent(), 8),
                data("""
                    ###############
                    #d.ABC.#.....a#
                    ######@#@######
                    ###############
                    ######@#@######
                    #b.....#.....c#
                    ###############
                """.trimIndent(), 24),
                data("""
                    #############
                    #DcBa.#.GhKl#
                    #.###@#@#I###
                    #e#######j###
                    #############
                    ###d#######k#
                    ###C#@#@###J#
                    #fEbA.#.FgHi#
                    #############
                """.trimIndent(), 32), // Example modified so that it can be quatered
                data("""
                    #############
                    #g#f.D#..h#l#
                    #F###e#E###.#
                    #dCba@#@BcIJ#
                    #############
                    #nK.L@#@G...#
                    #M###N#H###.#
                    #o#m..#i#jk.#
                    #############
                """.trimIndent(), 72),*/
                data("""
                    #############
                    #.....#######
                    #.###.#######
                    #..aC@#@b...#
                    #############
                    #...c@#@d...#
                    #############
                    #############
                    #############
                """.trimIndent(), 5)
            )
            onData("triton map %s ", with = *testData) { input, expected ->
                val paths = findShortestPath(input).findBestSolution()
                val length = paths.flatten().sumBy { it.dist }
                it("should have found the shortest path") {
                    length `should equal` expected
                }
                val quaterPaths = findShortestPathQuatered(input)
                val quaterLength = quaterPaths.flatten().sumBy { it.dist }
                it("should have found the same path as the sum of quater solutions") {
                    quaterLength `should equal` expected
                }
            }
        }
        describe("replace entrance with for entrances") {
            given("a map with a single entrance") {
                val singleEntranceMap = """
                    #######
                    #a.#Cd#
                    ##...##
                    ##.@.##
                    ##...##
                    #cB#Ab#
                    #######
                """.trimIndent()
                val expected = """
                    #######
                    #a.#Cd#
                    ##@#@##
                    #######
                    ##@#@##
                    #cB#Ab#
                    #######
                """.trimIndent()
                it("should be replaced to a map with four entrances") {
                    singleEntranceMap.replaceEntranceWithFour() `should equal` expected
                }
            }
        }
        describe("quater triton map") {
            given("a map to be quatered") {
                val map = """
                    #######
                    #a.#Cd#
                    ##@#@##
                    #######
                    ##@#@##
                    #cB#Ab#
                    #######
                """.trimIndent()
                it("should be quatered into four maps") {
                    map.quater() `should equal` listOf(
                        """
                            ####
                            #a.#
                            ##@#
                            ####
                        """.trimIndent(),
                        """
                            ####
                            #Cd#
                            #@##
                            ####
                        """.trimIndent(),
                        """
                            ####
                            ##@#
                            #cB#
                            ####
                        """.trimIndent(),
                        """
                            ####
                            #@##
                            #Ab#
                            ####
                        """.trimIndent()
                    )
                }

            }
        }
        describe("finde shortest path when entrances have to be replaced") {
            val input = """
                    #############
                    #g#f.D#..h#l#
                    #F###e#E###.#
                    #dCba...BcIJ#
                    #####.@.#####
                    #nK.L...G...#
                    #M###N#H###.#
                    #o#m..#i#jk.#
                    #############
            """.trimIndent()
            it("should find the shortest path") {
                val modifiedInput = input.replaceEntranceWithFour()
                println("modifiedInput=\n$modifiedInput")
                val paths = findShortestPath(modifiedInput).findBestSolution()
                val length = paths.flatten().sumBy { it.dist }
                length `should equal` 72
            }
        }
        describe("handle dead ends") {
            given("a simple map with a dead end") {
                val input = """
                    #############
                    #@.a........#
                    #############
                """.trimIndent()
                it("should find the dead end") {
                    val tritonMap = input.parseTritonMap()
                    val pois = tritonMap.findPois()
                    pois `should equal` setOf(
                        Entrance(1, 1), Key('a', 3, 1), DeadEnd(11, 1)
                    )
                }
                it("should remove the dead end from pois") {
                    val tritonMapWithoutIntersections = input.parseTritonMap()
                    val poisWithDeadEnds = tritonMapWithoutIntersections.findPois()
                    val tritonMap = tritonMapWithoutIntersections.replaceIntersections(poisWithDeadEnds)
                    val connectionsWithDeadEnds = findConnections(tritonMap, poisWithDeadEnds)
                    val (pois, connections) = removeDeadEnds(poisWithDeadEnds, connectionsWithDeadEnds)
                    pois `should equal` setOf(
                        Entrance(1, 1), Key('a', 3, 1)
                    )
                    connections `should equal` mapOf(
                        Coord2(1, 1) to setOf(PoiConnection(Coord2(3, 1), 2)),
                        Coord2(3, 1) to setOf(PoiConnection(Coord2(1, 1), 2))
                    )
                }
            }
            given("a map with many dead ends") {
                val input = """
                    #############
                    #@.a........#
                    #########.#.#
                    #.###.#.#.#.#
                    #.....A...#.#
                    #############
                """.trimIndent()
                it("should remove the dead end from pois") {
                    val tritonMapWithoutIntersections = input.parseTritonMap()
                    val poisWithDeadEnds = tritonMapWithoutIntersections.findPois()
                    val tritonMap = tritonMapWithoutIntersections.replaceIntersections(poisWithDeadEnds)
                    val connectionsWithDeadEnds = findConnections(tritonMap, poisWithDeadEnds)
                    val (pois, connections) = removeDeadEnds(poisWithDeadEnds, connectionsWithDeadEnds)
                    pois `should equal` setOf(
                        Entrance(1, 1), Key('a', 3, 1), Intersection(9, 1), Door('A', 6, 4), Intersection(7, 4)
                    )
                    connections `should equal` mapOf(
                        Coord2(1, 1) to setOf(PoiConnection(Coord2(3, 1), 2)),
                        Coord2(3, 1) to setOf(PoiConnection(Coord2(1, 1), 2), PoiConnection(Coord2(9, 1), 6)),
                        Coord2(9, 1) to setOf(PoiConnection(Coord2(3, 1), 6), PoiConnection(Coord2(7, 4), 5)),
                        Coord2(6, 4) to setOf(PoiConnection(Coord2(7, 4), 1)),
                        Coord2(7, 4) to setOf(PoiConnection(Coord2(6, 4), 1), PoiConnection(Coord2(9, 1), 5))
                    )
                }
            }
        }
        describe("combine lists") {
            given("a simle lists") {
                val list1 = listOf("a", "b")
                it("should combine elements with empty") {
                    combine(list1) `should equal` listOf(
                        listOf("a"),
                        listOf("b")
                    )
                }
            }
            given("three lists") {
                val list1 = listOf("a", "b")
                val list2 = listOf("1", "2", "3")
                val list3 = listOf(".")

                it("should find all combinations of elements") {
                    combine(list1, list2, list3) `should equal` listOf(
                        listOf("a", "1", "."),
                        listOf("a", "2", "."),
                        listOf("a", "3", "."),
                        listOf("b", "1", "."),
                        listOf("b", "2", "."),
                        listOf("b", "3", ".")
                    )
                }
            }
            given("more lists") {
                val list1 = List(5) { it }
                val list2 = List(10) { it }
                val list3 = List(40) { it }
                val list4 = List(20) { it }
                it("should combine all lists") {
                    combine(list1, list2, list3, list4).size `should equal` 5 * 10 * 40 * 20
                }
            }
        }
        given("exercise") {
            val input = readResource("day18Input.txt")!!
            val modifiedInput = input.replaceEntranceWithFour()
            val input4 = modifiedInput.quater()
            it("should be splittet into quarters") {
                input4.size `should equal` 4
            }
            it("should have found solutions for every quater") {
                val solutions = input4.map { findShortestPath(it, true) }
                solutions.forEach {
                    it.size `should be greater than` 0
                }
                println(solutions.map { it.size })
            }

            it("should find the shortest path") {
                println("modifiedInput=\n$modifiedInput")
                val paths = findShortestPathQuatered(modifiedInput)
                val length = paths.flatten().sumBy { it.dist }
                length `should equal` 1876
            }
        }
    }
})

fun <T> combine(vararg lists: List<T>): List<List<T>> = combine(lists.toList())

fun <T> combine(lists: List<List<T>>): List<List<T>> {
    if (lists.isEmpty()) throw error("nothing to combine")
    else if (lists.size == 1) return lists[0].map { listOf(it) }
    else {
        val restCombined = combine(lists.drop(1))
        return lists[0].flatMap {element ->
            restCombined.map {rest ->
                listOf(element) + rest
            }
        }
    }
}

fun removeDeadEnds(poisWithDeadEnds: Set<Poi>, connectionsWithDeadEnds: Map<Coord2, Set<PoiConnection>>): Pair<Set<Poi>, Map<Coord2, Set<PoiConnection>>> {
    val poiMap = poisWithDeadEnds.map { it.coord to it }.toMap()
    val deadEnds = poisWithDeadEnds.filter { it is DeadEnd }.map { it.coord }.toMutableSet()
    val connections = connectionsWithDeadEnds.toMutableMap()
    var moreDeadEndsFound: Boolean
    do {
        deadEnds.forEach {
            connections.remove(it)
        }
        moreDeadEndsFound = false
        connections.forEach { (coord, connectedTo) ->
            val poi = poiMap[coord]
            if (poi is Intersection) {
                if (connectedTo.filter { ! deadEnds.contains(it.coord) }.size <= 1) { // Intersection with only one way not leading to a dead end is itself a dead end
                    deadEnds.add(coord)
                    moreDeadEndsFound = true
                }
            }
         }
    } while(moreDeadEndsFound)
    val pois = poisWithDeadEnds.filter { ! deadEnds.contains(it.coord) }.toSet()
    val connectionsWithoutDeadEnd = connections.map {  (coord, connectedTo) ->
        val connectedToWithoutDeadEnd = connectedTo.filter { ! deadEnds.contains(it.coord) }.toSet()
        coord to connectedToWithoutDeadEnd
    }.toMap()
    return pois to connectionsWithoutDeadEnd
}

fun String.replaceEntranceWithFour(): String {
    val splitedString = parseTritonMapToChars()
    val entrancePosition = lines().mapIndexed { y, line ->
        line.mapIndexed { x, c ->
            if (c == '@') Coord2(x, y) else null
        }
    }.flatten().filterNotNull().first()
    val multiEntranceMap = """
        @#@
        ###
        @#@        
    """.trimIndent().parseTritonMapToChars()
    return splitedString.mapIndexed { y, row ->
        row.mapIndexed { x, c ->
            if (x >= entrancePosition.x - 1 && x <= entrancePosition.x + 1 &&
                y >= entrancePosition.y - 1 && y <= entrancePosition.y + 1)
                multiEntranceMap[y - (entrancePosition.y - 1)][x - (entrancePosition.x - 1)]
            else c
        }
    }.map { it.joinToString("") }.joinToString("\n")
}

fun String.quater(): List<String> {
    fun List<List<Char>>.subMap(startX: Int, startY: Int, width: Int, height: Int) = drop(startY).take(height).map { lines ->
        lines.drop(startX).take(width)
    }
    val map = parseTritonMapToChars()
    val height = map.size
    val width = map[0].size // Assuming every line has same size
    val quaterHeight = height / 2 + 1
    val quaterWidth = width / 2 + 1
    return sequence {
        yield(map.subMap(0, 0, quaterWidth, quaterHeight))
        yield(map.subMap(quaterWidth - 1, 0, quaterWidth, quaterHeight))
        yield(map.subMap(0, quaterHeight - 1, quaterWidth, quaterHeight))
        yield(map.subMap(quaterWidth - 1, quaterHeight - 1, quaterWidth, quaterHeight))
    }.map { quateredMap ->
        quateredMap.map { it.joinToString("") }.joinToString("\n")
    }.toList()
}

fun findShortestPath(input: String, ignoreUnknownKeys: Boolean = false): Collection<TritonSearchState> {
    val tritonMapData = input.parseTritonMapData()
    val solutions = findShortestSteps(tritonMapData.tritonMap, tritonMapData.pois, tritonMapData.connections, ignoreUnknownKeys)
    return solutions
}

fun String.parseTritonMapData(): TritonMapData {
    val tritonMapWithoutIntersections = parseTritonMap()
    val pois = tritonMapWithoutIntersections.findPois()
    val tritonMap = tritonMapWithoutIntersections.replaceIntersections(pois)
    val connections = findConnections(tritonMap, pois)
    val (poisWithoutDeadEnds, connectionsWithoutDeadEnds) = removeDeadEnds(pois, connections)
    println("pois reduced from ${pois.size} to ${poisWithoutDeadEnds.size} by removing dead ends")
    val tritonMapData = TritonMapData(tritonMap, poisWithoutDeadEnds, connectionsWithoutDeadEnds)
    return tritonMapData
}

data class TritonMapData(val tritonMap: List<List<TritonCoord>>, val pois: Set<Poi>, val connections: Map<Coord2, Set<PoiConnection>>)

fun findShortestSteps(tritonMap: List<List<TritonCoord>>, pois: Set<Poi>, connections: Map<Coord2, Set<PoiConnection>>, ignoreUnknownKeys: Boolean): List<TritonSearchState> {
    val entrances = pois.filterIsInstance<Entrance>()
    val allKeys = pois.filterIsInstance<Key>().toSet()
    val entranceCoords = entrances.map { it.coord }
    val tritonSearchEntries = entrances.map { TritonSearchRoute(it, emptyList())}
    val visitedRoutes = mutableMapOf((entranceCoords to emptySet<Key>()) to TritonSearchState(tritonSearchEntries, emptySet()))
    var currentRoutes = mutableMapOf<Pair<List<Coord2>,Set<Key>>,TritonSearchState>()
    currentRoutes.putAll(visitedRoutes)
    val solutionMap = mutableMapOf<List<Coord2>,TritonSearchState>()
    while(currentRoutes.isNotEmpty()) {
        val currentSolutions = currentRoutes.values.filter { it.keys == allKeys }
        currentSolutions.forEach { currentSolution ->
            val currentPosition = currentSolution.routes.map { it.position.coord }
            val foundSolution = solutionMap[currentPosition]
            if (foundSolution == null || foundSolution.length > currentSolution.length)
                solutionMap[currentPosition] = currentSolution
        }
        println("visitedRoutes=${visitedRoutes.size} currentRoutes=${currentRoutes.size} solutionMap=${solutionMap.size} keys=${visitedRoutes.values.maxBy { it.keys.size }?.keys?.size }")
        val nextCurrentRoutes = mutableMapOf<Pair<List<Coord2>,Set<Key>>,TritonSearchState>()
        (currentRoutes - currentSolutions).values.forEach { tritonSearchState -> // Don't investigate more into solutions
            val routes = tritonSearchState.routes
            routes.forEachIndexed { i, route ->
                val nextConnections = connections[route.position.coord] ?: emptySet()
                nextConnections.forEach { nextConnection ->
                    val nextPosition = tritonMap.getOrNull(nextConnection.coord)
                    if (nextPosition == null || nextPosition !is Poi) error("Connection pointing to wrong position $nextPosition")
                    if (nextPosition !is Door || nextPosition.matchingKey(tritonSearchState.keys) || (ignoreUnknownKeys && ! nextPosition.matchingKey(allKeys))) {
                        val nextPath = route.path + nextConnection
                        val nextKeys = if (nextPosition is Key) tritonSearchState.keys + nextPosition
                        else tritonSearchState.keys
                        val nextRoute = TritonSearchRoute(nextPosition, nextPath)
                        val nextRoutes = routes.mapIndexed { i2, route ->
                            if (i == i2) nextRoute
                            else route
                        }
                        val nextPositionsAndKeys = routes.mapIndexed { i2, route ->
                            if (i == i2) nextPosition.coord
                            else route.position.coord
                        }  to nextKeys
                        val nextTritonSearchState = TritonSearchState(nextRoutes, nextKeys)
                        val visitedRoute = visitedRoutes[nextPositionsAndKeys]
                        if (visitedRoute == null) {
                            visitedRoutes[nextPositionsAndKeys] = nextTritonSearchState
                            nextCurrentRoutes[nextPositionsAndKeys] = nextTritonSearchState
                        }
                    }
                }
            }
        }
        currentRoutes = nextCurrentRoutes
    }
    return solutionMap.values.toList()
}


fun findShortestPathQuatered(input: String): List<List<PoiConnection>> {
    val quaters = input.quater()
    val tritonMapDatas = quaters.map { it.parseTritonMapData() }
    val quatersSolutions = tritonMapDatas.map { findShortestSteps(it.tritonMap, it.pois, it.connections, ignoreUnknownKeys = true) }
    println(quatersSolutions.map { it.size })
    println(quatersSolutions.map { it.map { it.routes}})
    val combinedSolutions = combine(quatersSolutions)
    val sortedSolutions = combinedSolutions.sortedBy { solution ->
        solution.sumBy { quaterSolution -> quaterSolution.length }
    }
    return sortedSolutions.asSequence().filter { tritonSearchStates ->
        val pois = tritonMapDatas.map { it.pois }
        val paths = tritonSearchStates.map { it.routes.first().path }
        checkPathInQuateredMap(pois, paths)
    }.first().map { it.routes.first().path }
}

fun checkPathInQuateredMap(pois: List<Set<Poi>>, paths: List<List<PoiConnection>>): Boolean {
    fun solutionFound(positions: List<Int>): Boolean {
        positions.forEachIndexed { index, pos ->
            if (paths[index].size - 1 != pos) return false
        }
        return true
    }
    val poiMaps = pois.map {poi ->
        poi.map { it.coord to it }.toMap()
    }
    val currentPositions = List(paths.size) { 0 }.toMutableList()
    val keys = mutableSetOf<Key>()
    while (!solutionFound(currentPositions)) {
        currentPositions.forEachIndexed { index, position ->
            val path = paths[index]
            val coord = path[position].coord
            val poiMap = poiMaps[index]
            val poi = poiMap[coord]!!
            if (poi is Key) keys.add(poi)
        }
        var moved = false
        currentPositions.forEachIndexed { index, position ->
            val path = paths[index]
            val coord = path[position].coord
            val poiMap = poiMaps[index]
            val poi = poiMap[coord]!!
            if (poi !is Door || poi.matchingKey(keys))
                if (position < path.size - 1) {
                    currentPositions[index] = position + 1
                    moved = true
                }
        }
        if (! moved) return false
    }
    return true
}

fun Collection<TritonSearchState>.findBestSolution() = if (isNotEmpty()) {
    val solution = sortedBy { it.length }.first()
    solution.routes.map { it.path }
} else error("no solution found")

data class TritonSearchRoute(val position: Poi, val path: List<PoiConnection>) {
    val length: Int
        get() = path.sumBy { it.dist }
}
data class TritonSearchState(val routes: List<TritonSearchRoute>, val keys: Set<Key>, val length: Int = routes.sumBy { it.length })

private fun findConnections(map: List<List<TritonCoord>>, pois: Set<Poi>): Map<Coord2, Set<PoiConnection>> {
    fun follow(from: TritonCoord, current: TritonCoord, length: Int): PoiConnection? {
        return if (current is Poi) PoiConnection(current.coord, length)
        else {
            val next = current.neighbors(map).filter { it != from }
            when {
                next.isEmpty() -> null
                next.size == 1 -> follow(current, next.first(), length + 1)
                else -> error("$current should be a Point of Interest")
            }
        }
    }

    return pois.map { poi ->
        val connections = poi.neighbors(map).mapNotNull { neighbor ->
            follow(poi, neighbor, 1)
        }.toSet()
        poi.coord to connections
    }.toMap()
}

data class PoiConnection(val coord: Coord2, val dist: Int)

fun List<List<TritonCoord>>.findPois(): Set<Poi> =
    flatMap { rows ->
        rows.mapNotNull { tritonCoord ->
            if (tritonCoord is Poi) tritonCoord
            else if (tritonCoord is Empty) {
                if (tritonCoord.countEmpty(this) >= 3) Intersection(tritonCoord.x, tritonCoord.y)
                else if (tritonCoord.countEmpty(this) == 1) DeadEnd(tritonCoord.x, tritonCoord.y)
                else null
            } else null
        }
    }.toSet()


fun List<List<TritonCoord>>.replaceIntersections(pois: Set<Poi>): List<List<TritonCoord>> {
    val map = pois.map { it.coord to it }.toMap()
    return map { row ->
        row.map {tritonCoord ->
            val fromMap = map[tritonCoord.coord]
            fromMap ?: tritonCoord
        }
    }
}

fun String.parseTritonMapToChars(): List<List<Char>> = lines().map { line ->
    line.map { c -> c }
}

/** parseTritonMap doesn't distinguish intersections, after parse empty places must be identified as intersections */
private fun String.parseTritonMap(): List<List<TritonCoord>> = parseTritonMapToChars().mapIndexed { y, row ->
    row.mapIndexed { x, c ->
        when(c) {
            '#' -> Wall(x, y)
            '.' -> Empty(x, y)
            '@' -> Entrance(x, y)
            else -> {
                when {
                    c.isUpperCase() -> Door(c, x, y)
                    c.isLowerCase() -> Key(c, x, y)
                    else -> error("Illegal char %c in triton tunnel map")
                }
            }
        }
    }
}

sealed class TritonCoord(val coord: Coord2) {
    fun neighbors(map: List<List<TritonCoord>>) = sequence {
        listOf(Coord2(-1, 0), Coord2(1, 0), Coord2(0, -1), Coord2(0, 1)).forEach { diff ->
            val neighbor = map.getOrNull(coord + diff)
            if (neighbor != null && neighbor !is Wall)
                    yield(neighbor!!)
        }
    }.toSet()
    fun countEmpty(map: List<List<TritonCoord>>) = neighbors(map).size
}

data class Wall(val x: Int, val y: Int) : TritonCoord(Coord2(x, y))
data class Empty(val x: Int, val y: Int) : TritonCoord(Coord2(x, y))

sealed class Poi(coord: Coord2) : TritonCoord(coord)
data class Entrance(val x: Int, val y: Int) : Poi(Coord2(x, y))
data class Key(val c: Char, val x: Int, val y: Int) : Poi(Coord2(x, y))
data class Door(val c: Char, val x: Int, val y: Int) : Poi(Coord2(x, y)) {
    fun matchingKey(keys: Set<Key>) = keys.any { it.c == c.toLowerCase() }
}
data class Intersection(val x: Int, val y: Int) : Poi(Coord2(x, y))
data class DeadEnd(val x: Int, val y: Int) : Poi(Coord2(x, y))
