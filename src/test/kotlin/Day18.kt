import org.amshove.kluent.`should be greater than`
import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.data_driven.data
import org.jetbrains.spek.data_driven.on
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
                    Key('b', 1, 1), Door('A', 3, 1), Intersection(4, 1), Entrance(5, 1), Key('a', 7, 1), Intersection(4, 3)
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
                    Coord2(3, 3) to setOf(PoiConnection(Coord2(3, 1), 2), PoiConnection(Coord2(5, 3), 2)),
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
                """.trimIndent(), 132)

            )
            onData("triton map %s ", with = *testData) { input, expected ->
                val tritonMapWithoutIntersections = input.parseTritonMap()
                val pois = tritonMapWithoutIntersections.findPois()
                val tritonMap = tritonMapWithoutIntersections.replaceIntersections(pois)
                val connections = findConnections(tritonMap, pois)
                val path = findShortestSteps(tritonMap, pois, connections)
                println("path=$path")
                val length = path.sumBy { it.dist }
                it("should have found the shortest path") {
                    length `should equal` expected
                }
            }
        }
    }
})

fun findShortestSteps(tritonMap: List<List<TritonCoord>>, pois: Set<Poi>, connections: Map<Coord2, Set<PoiConnection>>): List<PoiConnection> {
    val entrance = pois.filterIsInstance<Entrance>().first()
    val allKeys = pois.filterIsInstance<Key>().toSet()
    var visitedRoutes = setOf<TritonSearchState>(TritonSearchState(entrance, emptyList(), emptySet()))
    while(true) {
        val solution = visitedRoutes.find { it.keys == allKeys}
        if (solution != null) return solution.path
        visitedRoutes = visitedRoutes.flatMap { route ->
            val nextConnections = connections[route.position.coord] ?: emptySet()
            nextConnections.mapNotNull { nextConnection ->
                val nextPosition = tritonMap.getOrNull(nextConnection.coord)
                if (nextPosition == null || nextPosition !is Poi) error("Connection pointing to wrong position $nextPosition")
                if (nextPosition is Door && ! nextPosition.matchingKey(route.keys)) null
                else {
                    val nextPath = route.path + nextConnection
                    val nextKeys = if (nextPosition is Key) route.keys + nextPosition
                    else route.keys
                    TritonSearchState(nextPosition, nextPath, nextKeys)
                }
            }
        }.toSet()
    }
}

data class TritonSearchState(val position: Poi, val path: List<PoiConnection>, val keys: Set<Key>)

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

fun List<List<TritonCoord>>.findPois(): Set<Poi> = flatMap { rows ->
    rows.mapNotNull { tritonCoord ->
        if (tritonCoord is Poi) tritonCoord
        else if (tritonCoord is Empty && tritonCoord.countEmpty(this) >= 3) Intersection(tritonCoord.x, tritonCoord.y)
            else null
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

/** parseTritonMap doesn't distinguish intersections */
private fun String.parseTritonMap(): List<List<TritonCoord>> = lines().mapIndexed { y, line ->
    line.mapIndexed { x, c ->
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
