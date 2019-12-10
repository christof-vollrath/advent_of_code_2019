import org.amshove.kluent.`should be greater than`
import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.data_driven.data
import org.jetbrains.spek.data_driven.on as onData
import kotlin.math.abs
import kotlin.math.atan

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

--- Part Two ---

Once you give them the coordinates,
the Elves quickly deploy an Instant Monitoring Station to the location and discover the worst:
there are simply too many asteroids.

The only solution is complete vaporization by giant laser.

Fortunately, in addition to an asteroid scanner,
the new monitoring station also comes equipped with a giant rotating laser perfect for vaporizing asteroids.
The laser starts by pointing up and always rotates clockwise, vaporizing any asteroid it hits.

If multiple asteroids are exactly in line with the station,
the laser only has enough power to vaporize one of them before continuing its rotation.
In other words, the same asteroids that can be detected can be vaporized,
but if vaporizing one asteroid makes another one detectable,
 the newly-detected asteroid won't be vaporized until the laser has returned
 to the same position by rotating a full 360 degrees.

For example, consider the following map, where the asteroid with the new monitoring station (and laser) is marked X:

.#....#####...#..
##...##.#####..##
##...#...#.#####.
..#.....X...###..
..#.#.....#....##

The first nine asteroids to get vaporized, in order, would be:

.#....###24...#..
##...##.13#67..9#
##...#...5.8####.
..#.....X...###..
..#.#.....#....##

Note that some asteroids (the ones behind the asteroids marked 1, 5, and 7)
won't have a chance to be vaporized until the next full rotation.
The laser continues rotating; the next nine to be vaporized are:

.#....###.....#..
##...##...#.....#
##...#......1234.
..#.....X...5##..
..#.9.....8....76

The next nine to be vaporized are then:

.8....###.....#..
56...9#...#.....#
34...7...........
..2.....X....##..
..1..............

Finally, the laser completes its first full rotation (1 through 3), a second rotation (4 through 8),
and vaporizes the last asteroid (9) partway through its third rotation:

......234.....6..
......1...5.....7
.................
........X....89..
.................

In the large example above (the one with the best monitoring station location at 11,13):

The 1st asteroid to be vaporized is at 11,12.
The 2nd asteroid to be vaporized is at 12,1.
The 3rd asteroid to be vaporized is at 12,2.
The 10th asteroid to be vaporized is at 12,8.
The 20th asteroid to be vaporized is at 16,0.
The 50th asteroid to be vaporized is at 16,9.
The 100th asteroid to be vaporized is at 10,16.
The 199th asteroid to be vaporized is at 9,6.
The 200th asteroid to be vaporized is at 8,2.
The 201st asteroid to be vaporized is at 10,9.
The 299th and final asteroid to be vaporized is at 11,1.

The Elves are placing bets on which will be the 200th asteroid to be vaporized.
Win the bet by determining which asteroid that will be;
what do you get if you multiply its X coordinate by 100 and then add its Y coordinate? (For example, 8,2 becomes 802.)

 */

typealias Asteroid = Coord2

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

fun Set<Asteroid>.visibleClockwise(from: Asteroid): List<Asteroid> =
    visible(from).sortedBy { atan((it.y - from.y).toDouble() / (it.x - from.x).toDouble()) }

fun parseAsteoridMap(asteroidMapString: String): Set<Asteroid> =
    asteroidMapString.split("\n").mapIndexed { y, row ->
        row.mapIndexedNotNull() { x, cell->
            if (cell == '#') Asteroid(x, y)
            else null
        }
    }.flatten().toSet()

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
    describe("part 2") {
        describe("first example") {
            val asteroidMapString = """
                .#....#####...#..
                ##...##.#####..##
                ##...#...#.#####.
                ..#.....#...###..
                ..#.#.....#....## 
            """.trimIndent()
            val asteroids = parseAsteoridMap(asteroidMapString)
            val visible = asteroids.visibleClockwise(from = Asteroid(8, 3))
            println(visible)

            it("should have found the first 9 asteroids to destroy") {
                visible.take(9) `should equal` listOf(
                    Asteroid(8, 1), Asteroid(9, 0), Asteroid(9, 1), Asteroid(10, 0),
                    Asteroid(9, 2), Asteroid(11, 1), Asteroid(12, 1), Asteroid(11, 2),
                    Asteroid(15, 1)
                )
            }
            it("should have found the circles to destroy all asteriods") {
                val circles = asteroids.getCircles()
                circles.map { it.size } `should equal` listOf(30, 5, 1)
            }
            describe("should select the asteroid destroyed as the n-th") {
                val testData = arrayOf(
                    data(1, Asteroid(8, 1)),
                    data(30, Asteroid(7, 0)),
                    data(31, Asteroid(8, 0)),
                    data(36, Asteroid(14, 3))
                )
                onData("nr %s ", with = *testData) { nr, expected ->
                    it("should calculate $expected") {
                        val circles = asteroids.getCircles()
                        val result = circles.getAsteroid(nr - 1)
                        result `should equal` expected
                    }
                }
            }
        }
        describe("exercise") {
            val asteroidMapString = readResource("day10Input.txt")!!
            val asteroids = parseAsteoridMap(asteroidMapString)
            val circles = asteroids.getCircles()
            val result = circles.getAsteroid(200 - 1)
            result.x * 100 + result.y `should be greater than` 406 // wrong results
            result.x * 100 + result.y `should be greater than` 708
        }
    }
})

private fun List<List<Asteroid>>.getAsteroid(nr: Int): Asteroid {
    var currOffset = 0
    var i = 0
    while(true) {
        val currentAsteroids = get(i)
        if (currentAsteroids.size <= nr - currOffset) currOffset += currentAsteroids.size
        else return currentAsteroids[nr - currOffset]
        i++
    }
}

fun Set<Asteroid>.getCircles(): List<List<Asteroid>> {
    val visible = countVisible()
    val laserPosition = visible.maxBy { it.second }!!.first
    val current = this.toMutableSet();
    return sequence {
        while(current.size > 1) { // Asteroid with laser remains
            val currentDestroyed = current.visibleClockwise(laserPosition)
            yield(currentDestroyed)
            current.removeAll(currentDestroyed)
        }
    }.toList()
}

