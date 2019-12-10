import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.data_driven.data
import org.jetbrains.spek.data_driven.on as onData
import kotlin.math.abs

/*
--- Day 10: Monitoring Station ---

You fly into the asteroid belt and reach the Ceres monitoring station. 
The Elves here have an emergency: they're having trouble tracking all of the asteroids
 and can't be sure they're safe.

The Elves would like to build a new monitoring station in a nearby area of space; 
they hand you a map of all of the asteroids in that region (your puzzle input).

The map indicates whether each position is empty (.) or contains an asteroid (#). 
The asteroids are much smaller than they appear on the map, 
and every asteroid is exactly in the center of its marked position. 
The asteroids can be described with X,Y coordinates where X is the distance from the left edge 
and Y is the distance from the top edge (so the top-left corner is 0,0 
and the position immediately to its right is 1,0).

Your job is to figure out which asteroid would be the best place to build a new monitoring station. 
A monitoring station can detect any asteroid to which it has direct line of sight 
- that is, there cannot be another asteroid exactly between them. 
This line of sight can be at any angle, not just lines aligned to the grid or diagonally. 
The best location is the asteroid that can detect the largest number of other asteroids.

For example, consider the following map:

.#..#
.....
#####
....#
...##

The best location for a new monitoring station on this map is the highlighted asteroid at 3,4 
because it can detect 8 asteroids, more than any other location. 
(The only asteroid it cannot detect is the one at 1,0; its view of this asteroid is blocked by the asteroid at 2,2.) 
All other asteroids are worse locations; they can detect 7 or fewer other asteroids. 
Here is the number of other asteroids a monitoring station on each asteroid could detect:

.7..7
.....
67775
....7
...87

Here is an asteroid (#) and some examples of the ways its line of sight might be blocked. 
If there were another asteroid at the location of a capital letter, the locations marked 
with the corresponding lowercase letter would be blocked and could not be detected:

#.........
...A......
...B..a...
.EDCG....a
..F.c.b...
.....c....
..efd.c.gb
.......c..
....f...c.
...e..d..c

Here are some larger examples:

Best is 5,8 with 33 other asteroids detected:

......#.#.
#..#.#....
..#######.
.#.#.###..
.#..#.....
..#....#.#
#..#....#.
.##.#..###
##...#..#.
.#....####

Best is 1,2 with 35 other asteroids detected:

#.#...#.#.
.###....#.
.#....#...
##.#.#.#.#
....#.#.#.
.##..###.#
..#...##..
..##....##
......#...
.####.###.

Best is 6,3 with 41 other asteroids detected:

.#..#..###
####.###.#
....###.#.
..###.##.#
##.##.#.#.
....###..#
..#.#..#.#
#..#.#.###
.##...##.#
.....#.#..

Best is 11,13 with 210 other asteroids detected:

.#..##.###...#######
##.############..##.
.#.######.########.#
.###.#######.####.#.
#####.##.#.##.###.##
..#####..#.#########
####################
#.####....###.#.#.##
##.#################
#####.##.###..####..
..######..##.#######
####.##.####...##..#
.#####..#.######.###
##...#.##########...
#.##########.#######
.####.#.###.###.#.##
....##.##.###..#####
.#.#.###########.###
#.#.#.#####.####.###
###.##.####.##.#..##

Find the best location for a new monitoring station. How many other asteroids can be detected from that location?
 */

typealias Asteroid = Coord2

class Day10Spec : Spek({

    describe("part 1") {
        describe("greatest common divisor, needed to calculate step size") {
            it("should calculate the gcd") {
                gcd(54, 24) `should equal` 6
            }
        }
        given("simple example") {
            val asteroidMapString = """
                .#..#
                .....
                #####
                ....#
                ...##
            """.trimIndent()
            val asteroids = parseAsteoridMap(asteroidMapString)
            it("should be parsed correctly") {
                asteroids `should equal` setOf(
                    Asteroid(1, 0), Asteroid(4, 0),
                    Asteroid(0, 2), Asteroid(1, 2), Asteroid(2, 2), Asteroid(3, 2), Asteroid(4, 2),
                    Asteroid(4, 3),
                    Asteroid(3, 4), Asteroid(4, 4)
                )
            }
            on("sort by distance") {
                val sorted = asteroids.sortedByDistanceTo(Asteroid(3, 4))
                sorted.take(3) `should equal` listOf(Asteroid(3, 4), Asteroid(4, 4), Asteroid(3, 2))
            }
            on("find asteroids hidden behind another") {
                val invisible = asteroids.hidden(start = Asteroid(3, 4), behind = Asteroid(2, 2))
                invisible `should equal` setOf(Asteroid(1, 0))
            }
            on("find all visible asteroids from the best") {
                val visible = asteroids.visible(from = Asteroid(3, 4))
                visible.size `should equal` 8
            }
            on("find all visible asteroids from the worst") {
                val visible = asteroids.visible(from = Asteroid(4, 2))
                visible.size `should equal` 5
            }
            on("find all visible asteroids from some example") {
                val visible = asteroids.visible(from = Asteroid(1, 0))
                visible.size `should equal` 7
            }
            on("find asteroid from most are visible") {
                val visible = asteroids.countVisible()
                val best = visible.maxBy { it.second }!!.first
                best `should equal` Asteroid(3, 4)
            }
        }
        describe("more examples") {
            val testData = arrayOf(
                data("""
                    ......#.#.
                    #..#.#....
                    ..#######.
                    .#.#.###..
                    .#..#.....
                    ..#....#.#
                    #..#....#.
                    .##.#..###
                    ##...#..#.
                    .#....#### 
                """.trimIndent(), Asteroid(5,8) to 33),
                data("""
                    #.#...#.#.
                    .###....#.
                    .#....#...
                    ##.#.#.#.#
                    ....#.#.#.
                    .##..###.#
                    ..#...##..
                    ..##....##
                    ......#...
                    .####.###.
                """.trimIndent(), Asteroid(1,2) to 35),
                data("""
                    .#..#..###
                    ####.###.#
                    ....###.#.
                    ..###.##.#
                    ##.##.#.#.
                    ....###..#
                    ..#.#..#.#
                    #..#.#.###
                    .##...##.#
                    .....#.#..
                """.trimIndent(), Asteroid(6,3) to 41),
                data("""
                    .#..##.###...#######
                    ##.############..##.
                    .#.######.########.#
                    .###.#######.####.#.
                    #####.##.#.##.###.##
                    ..#####..#.#########
                    ####################
                    #.####....###.#.#.##
                    ##.#################
                    #####.##.###..####..
                    ..######..##.#######
                    ####.##.####...##..#
                    .#####..#.######.###
                    ##...#.##########...
                    #.##########.#######
                    .####.#.###.###.#.##
                    ....##.##.###..#####
                    .#.#.###########.###
                    #.#.#.#####.####.###
                    ###.##.####.##.#..##
                """.trimIndent(), Asteroid(11,13) to 210)
            )
            onData("asteorids %s input %s", with = *testData) { asteroidMapString, expected ->
                it("should calculate $expected") {
                    val asteroids = parseAsteoridMap(asteroidMapString)
                    val visible = asteroids.countVisible()
                    val best = visible.maxBy { it.second }
                    best `should equal` expected
                }
            }
        }
        describe("exercise") {
            val asteroidMapString = readResource("day10Input.txt")!!
            val asteroids = parseAsteoridMap(asteroidMapString)
            val visible = asteroids.countVisible()
            val best = visible.maxBy { it.second }
            best `should equal` (Asteroid(x=23, y=19) to 278)
        }
    }
})


fun Set<Asteroid>.countVisible(): Set<Pair<Asteroid, Int>> = map { asteroid ->
    val nrVisible = visible(from = asteroid).count()
    asteroid to nrVisible
}.toSet()

fun Set<Asteroid>.visible(from: Asteroid): Set<Asteroid> {
    val invisible = mutableSetOf<Asteroid>()
    val candidates = filterNot { it == from }.sortedByDistanceTo(from)
    return candidates.mapNotNull {
            if (it in invisible) null
            else {
                val hidden = hidden(from, it)
                invisible.addAll(hidden)
                it
            }
        }.toSet()
}

fun Set<Asteroid>.hidden(start: Asteroid, behind: Asteroid): Set<Asteroid> {
    val maxX = maxBy { it.x }!!.x
    val minX = minBy { it.x }!!.x
    val maxY = maxBy { it.y }!!.y
    val minY = minBy { it.y }!!.y
    val dx1 = behind.x - start.x
    val dy1 = behind.y - start.y
    val gcd = gcd(abs(dx1), abs(dy1))
    val dx = dx1 / gcd
    val dy = dy1 / gcd
    var current = Asteroid(behind.x + dx, behind.y + dy)
    return sequence {
        while (current.x <= maxX && current.x >= minX && current.y <= maxY && current.y >= minY) {
            if (current in this@hidden && current != behind) yield(current)
            current = Asteroid(current.x + dx, current.y + dy)
        }
    }.toSet()
}

fun Collection<Asteroid>.sortedByDistanceTo(asteroid: Asteroid): List<Asteroid> = sortedBy { it manhattanDistance asteroid }

fun parseAsteoridMap(asteroidMapString: String): Set<Asteroid> =
    asteroidMapString.split("\n").mapIndexed { y, row ->
        row.mapIndexedNotNull() { x, cell->
            if (cell == '#') Asteroid(x, y)
            else null
        }
    }.flatten().toSet()
