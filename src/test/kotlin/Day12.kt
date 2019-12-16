import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.*
import kotlin.math.abs

/*
--- Day 12: The N-Body Problem ---

The space near Jupiter is not a very safe place;
you need to be careful of a big distracting red spot, extreme radiation,
and a whole lot of moons swirling around.
You decide to start by tracking the four largest moons: Io, Europa, Ganymede, and Callisto.

After a brief scan, you calculate the position of each moon (your puzzle input).
You just need to simulate their motion so you can avoid them.

Each moon has a 3-dimensional position (x, y, and z) and a 3-dimensional velocity.
The position of each moon is given in your scan; the x, y, and z velocity of each moon starts at 0.

Simulate the motion of the moons in time steps.
Within each time step, first update the velocity of every moon by applying gravity.
Then, once all moons' velocities have been updated, update the position of every moon by applying velocity.
Time progresses by one step once all of the positions are updated.

To apply gravity, consider every pair of moons.
On each axis (x, y, and z), the velocity of each moon changes by exactly +1 or -1 to pull the moons together.
For example, if Ganymede has an x position of 3, and Callisto has a x position of 5,
then Ganymede's x velocity changes by +1 (because 5 > 3)
and Callisto's x velocity changes by -1 (because 3 < 5).
However, if the positions on a given axis are the same,
the velocity on that axis does not change for that pair of moons.

Once all gravity has been applied, apply velocity:
simply add the velocity of each moon to its own position.
For example, if Europa has a position of x=1, y=2, z=3 and a velocity of x=-2, y=0,z=3,
then its new position would be x=-1, y=2, z=6.
This process does not modify the velocity of any moon.

For example, suppose your scan reveals the following positions:

<x=-1, y=0, z=2>
<x=2, y=-10, z=-7>
<x=4, y=-8, z=8>
<x=3, y=5, z=-1>

Simulating the motion of these moons would produce the following:

After 0 steps:
pos=<x=-1, y=  0, z= 2>, vel=<x= 0, y= 0, z= 0>
pos=<x= 2, y=-10, z=-7>, vel=<x= 0, y= 0, z= 0>
pos=<x= 4, y= -8, z= 8>, vel=<x= 0, y= 0, z= 0>
pos=<x= 3, y=  5, z=-1>, vel=<x= 0, y= 0, z= 0>

After 1 step:
pos=<x= 2, y=-1, z= 1>, vel=<x= 3, y=-1, z=-1>
pos=<x= 3, y=-7, z=-4>, vel=<x= 1, y= 3, z= 3>
pos=<x= 1, y=-7, z= 5>, vel=<x=-3, y= 1, z=-3>
pos=<x= 2, y= 2, z= 0>, vel=<x=-1, y=-3, z= 1>

After 2 steps:
pos=<x= 5, y=-3, z=-1>, vel=<x= 3, y=-2, z=-2>
pos=<x= 1, y=-2, z= 2>, vel=<x=-2, y= 5, z= 6>
pos=<x= 1, y=-4, z=-1>, vel=<x= 0, y= 3, z=-6>
pos=<x= 1, y=-4, z= 2>, vel=<x=-1, y=-6, z= 2>

After 3 steps:
pos=<x= 5, y=-6, z=-1>, vel=<x= 0, y=-3, z= 0>
pos=<x= 0, y= 0, z= 6>, vel=<x=-1, y= 2, z= 4>
pos=<x= 2, y= 1, z=-5>, vel=<x= 1, y= 5, z=-4>
pos=<x= 1, y=-8, z= 2>, vel=<x= 0, y=-4, z= 0>

After 4 steps:
pos=<x= 2, y=-8, z= 0>, vel=<x=-3, y=-2, z= 1>
pos=<x= 2, y= 1, z= 7>, vel=<x= 2, y= 1, z= 1>
pos=<x= 2, y= 3, z=-6>, vel=<x= 0, y= 2, z=-1>
pos=<x= 2, y=-9, z= 1>, vel=<x= 1, y=-1, z=-1>

After 5 steps:
pos=<x=-1, y=-9, z= 2>, vel=<x=-3, y=-1, z= 2>
pos=<x= 4, y= 1, z= 5>, vel=<x= 2, y= 0, z=-2>
pos=<x= 2, y= 2, z=-4>, vel=<x= 0, y=-1, z= 2>
pos=<x= 3, y=-7, z=-1>, vel=<x= 1, y= 2, z=-2>

After 6 steps:
pos=<x=-1, y=-7, z= 3>, vel=<x= 0, y= 2, z= 1>
pos=<x= 3, y= 0, z= 0>, vel=<x=-1, y=-1, z=-5>
pos=<x= 3, y=-2, z= 1>, vel=<x= 1, y=-4, z= 5>
pos=<x= 3, y=-4, z=-2>, vel=<x= 0, y= 3, z=-1>

After 7 steps:
pos=<x= 2, y=-2, z= 1>, vel=<x= 3, y= 5, z=-2>
pos=<x= 1, y=-4, z=-4>, vel=<x=-2, y=-4, z=-4>
pos=<x= 3, y=-7, z= 5>, vel=<x= 0, y=-5, z= 4>
pos=<x= 2, y= 0, z= 0>, vel=<x=-1, y= 4, z= 2>

After 8 steps:
pos=<x= 5, y= 2, z=-2>, vel=<x= 3, y= 4, z=-3>
pos=<x= 2, y=-7, z=-5>, vel=<x= 1, y=-3, z=-1>
pos=<x= 0, y=-9, z= 6>, vel=<x=-3, y=-2, z= 1>
pos=<x= 1, y= 1, z= 3>, vel=<x=-1, y= 1, z= 3>

After 9 steps:
pos=<x= 5, y= 3, z=-4>, vel=<x= 0, y= 1, z=-2>
pos=<x= 2, y=-9, z=-3>, vel=<x= 0, y=-2, z= 2>
pos=<x= 0, y=-8, z= 4>, vel=<x= 0, y= 1, z=-2>
pos=<x= 1, y= 1, z= 5>, vel=<x= 0, y= 0, z= 2>

After 10 steps:
pos=<x= 2, y= 1, z=-3>, vel=<x=-3, y=-2, z= 1>
pos=<x= 1, y=-8, z= 0>, vel=<x=-1, y= 1, z= 3>
pos=<x= 3, y=-6, z= 1>, vel=<x= 3, y= 2, z=-3>
pos=<x= 2, y= 0, z= 4>, vel=<x= 1, y=-1, z=-1>

Then, it might help to calculate the total energy in the system.
The total energy for a single moon is its potential energy multiplied by its kinetic energy.
A moon's potential energy is the sum of the absolute values of its x, y, and z position coordinates.
A moon's kinetic energy is the sum of the absolute values of its velocity coordinates.
Below, each line shows the calculations for a moon's potential energy (pot), kinetic energy (kin), and total energy:

Energy after 10 steps:
pot: 2 + 1 + 3 =  6;   kin: 3 + 2 + 1 = 6;   total:  6 * 6 = 36
pot: 1 + 8 + 0 =  9;   kin: 1 + 1 + 3 = 5;   total:  9 * 5 = 45
pot: 3 + 6 + 1 = 10;   kin: 3 + 2 + 3 = 8;   total: 10 * 8 = 80
pot: 2 + 0 + 4 =  6;   kin: 1 + 1 + 1 = 3;   total:  6 * 3 = 18

Sum of total energy: 36 + 45 + 80 + 18 = 179

In the above example, adding together the total energy for all moons after 10 steps
produces the total energy in the system, 179.

Here's a second example:

<x=-8, y=-10, z=0>
<x=5, y=5, z=10>
<x=2, y=-7, z=3>
<x=9, y=-8, z=-3>

Every ten steps of simulation for 100 steps produces:

After 0 steps:
pos=<x= -8, y=-10, z=  0>, vel=<x=  0, y=  0, z=  0>
pos=<x=  5, y=  5, z= 10>, vel=<x=  0, y=  0, z=  0>
pos=<x=  2, y= -7, z=  3>, vel=<x=  0, y=  0, z=  0>
pos=<x=  9, y= -8, z= -3>, vel=<x=  0, y=  0, z=  0>

After 10 steps:
pos=<x= -9, y=-10, z=  1>, vel=<x= -2, y= -2, z= -1>
pos=<x=  4, y= 10, z=  9>, vel=<x= -3, y=  7, z= -2>
pos=<x=  8, y=-10, z= -3>, vel=<x=  5, y= -1, z= -2>
pos=<x=  5, y=-10, z=  3>, vel=<x=  0, y= -4, z=  5>

After 20 steps:
pos=<x=-10, y=  3, z= -4>, vel=<x= -5, y=  2, z=  0>
pos=<x=  5, y=-25, z=  6>, vel=<x=  1, y=  1, z= -4>
pos=<x= 13, y=  1, z=  1>, vel=<x=  5, y= -2, z=  2>
pos=<x=  0, y=  1, z=  7>, vel=<x= -1, y= -1, z=  2>

After 30 steps:
pos=<x= 15, y= -6, z= -9>, vel=<x= -5, y=  4, z=  0>
pos=<x= -4, y=-11, z=  3>, vel=<x= -3, y=-10, z=  0>
pos=<x=  0, y= -1, z= 11>, vel=<x=  7, y=  4, z=  3>
pos=<x= -3, y= -2, z=  5>, vel=<x=  1, y=  2, z= -3>

After 40 steps:
pos=<x= 14, y=-12, z= -4>, vel=<x= 11, y=  3, z=  0>
pos=<x= -1, y= 18, z=  8>, vel=<x= -5, y=  2, z=  3>
pos=<x= -5, y=-14, z=  8>, vel=<x=  1, y= -2, z=  0>
pos=<x=  0, y=-12, z= -2>, vel=<x= -7, y= -3, z= -3>

After 50 steps:
pos=<x=-23, y=  4, z=  1>, vel=<x= -7, y= -1, z=  2>
pos=<x= 20, y=-31, z= 13>, vel=<x=  5, y=  3, z=  4>
pos=<x= -4, y=  6, z=  1>, vel=<x= -1, y=  1, z= -3>
pos=<x= 15, y=  1, z= -5>, vel=<x=  3, y= -3, z= -3>

After 60 steps:
pos=<x= 36, y=-10, z=  6>, vel=<x=  5, y=  0, z=  3>
pos=<x=-18, y= 10, z=  9>, vel=<x= -3, y= -7, z=  5>
pos=<x=  8, y=-12, z= -3>, vel=<x= -2, y=  1, z= -7>
pos=<x=-18, y= -8, z= -2>, vel=<x=  0, y=  6, z= -1>

After 70 steps:
pos=<x=-33, y= -6, z=  5>, vel=<x= -5, y= -4, z=  7>
pos=<x= 13, y= -9, z=  2>, vel=<x= -2, y= 11, z=  3>
pos=<x= 11, y= -8, z=  2>, vel=<x=  8, y= -6, z= -7>
pos=<x= 17, y=  3, z=  1>, vel=<x= -1, y= -1, z= -3>

After 80 steps:
pos=<x= 30, y= -8, z=  3>, vel=<x=  3, y=  3, z=  0>
pos=<x= -2, y= -4, z=  0>, vel=<x=  4, y=-13, z=  2>
pos=<x=-18, y= -7, z= 15>, vel=<x= -8, y=  2, z= -2>
pos=<x= -2, y= -1, z= -8>, vel=<x=  1, y=  8, z=  0>

After 90 steps:
pos=<x=-25, y= -1, z=  4>, vel=<x=  1, y= -3, z=  4>
pos=<x=  2, y= -9, z=  0>, vel=<x= -3, y= 13, z= -1>
pos=<x= 32, y= -8, z= 14>, vel=<x=  5, y= -4, z=  6>
pos=<x= -1, y= -2, z= -8>, vel=<x= -3, y= -6, z= -9>

After 100 steps:
pos=<x=  8, y=-12, z= -9>, vel=<x= -7, y=  3, z=  0>
pos=<x= 13, y= 16, z= -3>, vel=<x=  3, y=-11, z= -5>
pos=<x=-29, y=-11, z= -1>, vel=<x= -3, y=  7, z=  4>
pos=<x= 16, y=-13, z= 23>, vel=<x=  7, y=  1, z=  1>

Energy after 100 steps:
pot:  8 + 12 +  9 = 29;   kin: 7 +  3 + 0 = 10;   total: 29 * 10 = 290
pot: 13 + 16 +  3 = 32;   kin: 3 + 11 + 5 = 19;   total: 32 * 19 = 608
pot: 29 + 11 +  1 = 41;   kin: 3 +  7 + 4 = 14;   total: 41 * 14 = 574
pot: 16 + 13 + 23 = 52;   kin: 7 +  1 + 1 =  9;   total: 52 *  9 = 468

Sum of total energy: 290 + 608 + 574 + 468 = 1940

What is the total energy in the system after simulating the moons given in your scan for 1000 steps?

--- Part Two ---

All this drifting around in space makes you wonder about the nature of the universe.
Does history really repeat itself? You're curious whether the moons will ever return to a previous state.

Determine the number of steps that must occur before all of the moons' positions
and velocities exactly match a previous point in time.

For example, the first example above takes 2772 steps before they exactly match a previous point in time;
it eventually returns to the initial state:

After 0 steps:
pos=<x= -1, y=  0, z=  2>, vel=<x=  0, y=  0, z=  0>
pos=<x=  2, y=-10, z= -7>, vel=<x=  0, y=  0, z=  0>
pos=<x=  4, y= -8, z=  8>, vel=<x=  0, y=  0, z=  0>
pos=<x=  3, y=  5, z= -1>, vel=<x=  0, y=  0, z=  0>

After 2770 steps:
pos=<x=  2, y= -1, z=  1>, vel=<x= -3, y=  2, z=  2>
pos=<x=  3, y= -7, z= -4>, vel=<x=  2, y= -5, z= -6>
pos=<x=  1, y= -7, z=  5>, vel=<x=  0, y= -3, z=  6>
pos=<x=  2, y=  2, z=  0>, vel=<x=  1, y=  6, z= -2>

After 2771 steps:
pos=<x= -1, y=  0, z=  2>, vel=<x= -3, y=  1, z=  1>
pos=<x=  2, y=-10, z= -7>, vel=<x= -1, y= -3, z= -3>
pos=<x=  4, y= -8, z=  8>, vel=<x=  3, y= -1, z=  3>
pos=<x=  3, y=  5, z= -1>, vel=<x=  1, y=  3, z= -1>

After 2772 steps:
pos=<x= -1, y=  0, z=  2>, vel=<x=  0, y=  0, z=  0>
pos=<x=  2, y=-10, z= -7>, vel=<x=  0, y=  0, z=  0>
pos=<x=  4, y= -8, z=  8>, vel=<x=  0, y=  0, z=  0>
pos=<x=  3, y=  5, z= -1>, vel=<x=  0, y=  0, z=  0>

Of course, the universe might last for a very long time before repeating.
Here's a copy of the second example from above:

<x=-8, y=-10, z=0>
<x=5, y=5, z=10>
<x=2, y=-7, z=3>
<x=9, y=-8, z=-3>

This set of initial positions takes 4686774924 steps before it repeats a previous state!
Clearly, you might need to find a more efficient way to simulate the universe.

How many steps does it take to reach the first state that exactly matches a previous state?

 */

fun List<Moon>.energy() = map { it.energy() }.sum()

fun List<Moon>.steps(n: Int, debug: Boolean = false) = repeat(n) {
    applyGravity()
    applyVelocity()
    if (debug) {
        println("$it:")
        println(this)
    }
}

fun List<Moon>.applyVelocity() = forEach { moon ->
    moon.applyVelocity()
}

fun List<Moon>.applyGravity() =
    forEach { moon ->
        moon.pos.forEachIndexed { i, coord ->
            val greaterCount = filter {
                it.pos[i] > coord // self must not be ignored explicitly because coord is equal
            }.count()
            val lowerCount = filter {
                it.pos[i] < coord
            }.count()
            moon.vel[i] += greaterCount - lowerCount
        }
    }

data class Moon(val pos: MutableList<Int>, val vel: MutableList<Int>) {
    constructor(x: Int, y: Int, z: Int, dx: Int = 0, dy: Int = 0, dz: Int = 0):
            this(mutableListOf(x, y, z), mutableListOf(dx, dy, dz))

    fun applyVelocity() {
        vel.forEachIndexed { i, coordVel->
            pos[i] += coordVel
        }
    }

    fun energy() = potentialEnergy() * kineticEnergy()

    fun potentialEnergy() = pos.map { abs(it) }.sum()
    fun kineticEnergy() = vel.map { abs(it) }.sum()
}

fun findAllLoopsAllMoons(moons: List<Moon>): List<Int> = moons.mapIndexed { moonId, _ ->
    findAllLoops(moons, moonId)
}.flatten()

fun findAllLoops(moons: List<Moon>, moonId: Int): List<Int> =
    moons[moonId].pos.mapIndexed { coordId, coord ->
        val testMoons = moons.map { it.copy() }
        val result = findLoop(testMoons, moonId, coordId)
        println("moon=$moonId coord=$coordId loop=$result")
        result
    }


fun findLoop(moons: List<Moon>, moonId: Int, coordId: Int): Int {
    val values = mutableListOf<Int>()
    val valuesSet = mutableSetOf<Int>()
    while(true) {
        val velocity = moons[moonId].pos[coordId]
        val numberAlreadyFound = valuesSet.contains(velocity)
        values.add(velocity)
        valuesSet.add(velocity)
        if (numberAlreadyFound && values.size % 2 == 0) { // Compare list of already found numbers only when this number already found to speed this up
            // check for loop
            val loopSize = values.size / 2
            val firstHalf = values.take(loopSize)
            val secondHalf = values.drop(loopSize)
            if (firstHalf == secondHalf) return loopSize // loop found
        }
        moons.applyGravity()
        moons.applyVelocity()
    }
}

class Day12Spec : Spek({

    describe("part 1") {
        describe("move one step first gravity then velocity") {
            given("four moons") {
                val moons = listOf(
                    Moon(x= -1, y=  0, z=  2),
                    Moon(x=  2, y=-10, z= -7),
                    Moon(x=  4, y= -8, z=  8),
                    Moon(x=  3, y=  5, z= -1)
                )
                it("should apply gravity") {
                    moons.applyGravity()
                    moons `should equal` listOf(
                        Moon(x= -1, y=  0, z=  2, dx=  3, dy= -1, dz= -1),
                        Moon(x=  2, y=-10, z= -7, dx=  1, dy=  3, dz=  3),
                        Moon(x=  4, y= -8, z=  8, dx= -3, dy=  1, dz= -3),
                        Moon(x=  3, y=  5, z= -1, dx= -1, dy= -3, dz=  1)
                    )
                }
                it("should apply velocity") {
                    moons.applyVelocity()
                    moons `should equal` listOf(
                        Moon(x=  2, y= -1, z=  1, dx=  3, dy= -1, dz= -1),
                        Moon(x=  3, y= -7, z= -4, dx=  1, dy=  3, dz=  3),
                        Moon(x=  1, y= -7, z=  5, dx= -3, dy=  1, dz= -3),
                        Moon(x=  2, y=  2, z=  0, dx= -1, dy= -3, dz=  1)
                    )
                }

            }
        }
        describe("execute steps") {
            given("four moons") {
                val moons = listOf(
                    Moon(x= -1, y=  0, z=  2),
                    Moon(x=  2, y=-10, z= -7),
                    Moon(x=  4, y= -8, z=  8),
                    Moon(x=  3, y=  5, z= -1)
                )
                on("execute 10 steps") {
                    moons.steps(10)
                    it("should have calculated the right position and velocity") {
                        moons `should equal` listOf(
                            Moon(x=  2, y=  1, z= -3, dx= -3, dy= -2, dz=  1),
                            Moon(x=  1, y= -8, z=  0, dx= -1, dy=  1, dz=  3),
                            Moon(x=  3, y= -6, z=  1, dx=  3, dy=  2, dz= -3),
                            Moon(x=  2, y=  0, z=  4, dx=  1, dy= -1, dz= -1)
                        )
                    }
                }
                on("calculate energy") {
                    val energy = moons.energy()
                    it("should have the right result") {
                        energy `should equal` 179
                    }
                }
            }
        }
        describe("exercise") {
            given("exercise input") {
                val moons = listOf(
                    Moon(x=  4, y=  1, z=  1),
                    Moon(x= 11, y=-18, z= -1),
                    Moon(x= -2, y=-10, z= -4),
                    Moon(x= -7, y= -2, z= 14)
                )
                it("should calculate the energy after executing 1000 steps") {
                    moons.steps(1000)
                    val energy = moons.energy()
                    energy `should equal` 9493
                }
            }
        }
    }
    describe("part 2") {
        describe("least common multiple (lcm)") {
            given("two numbers") {
                val a = 4
                val b = 6
                it("should calculate 12 as the lcm") {
                    lcm(a, b) `should equal` 12
                }
            }
            given("a list of numbers") {
                val a = 4
                val b = 6
                val numbers = listOf(4, 6, 15)
                it("should calculate the correct lcm") {
                    lcm(numbers) `should equal` 60
                }
            }
            given("large numbers") {
                val numbers = listOf(5898L, 4702L, 2028L, 4702L, 5898L, 4702L, 2028L, 4702L)
                it("should calculate the correct lcm") {
                    lcm(numbers) `should equal` 4686774924L
                }
            }
        }
        describe("find loop") {
            given("four moons") {
                val startMoons = listOf(
                    Moon(x = -1, y = 0, z = 2),
                    Moon(x = 2, y = -10, z = -7),
                    Moon(x = 4, y = -8, z = 8),
                    Moon(x = 3, y = 5, z = -1)
                )
                val moons = startMoons.map { it.copy() }
                on("execute 2772 steps") {
                    moons.steps(2772, debug = false)
                    it("should have returned to the same state") {
                        moons `should equal` listOf(
                            Moon(x = -1, y = 0, z = 2),
                            Moon(x = 2, y = -10, z = -7),
                            Moon(x = 4, y = -8, z = 8),
                            Moon(x = 3, y = 5, z = -1)
                        )
                    }
                }
                it("should find the loop for the first moon at x") {
                    val moons = startMoons.map { it.copy() }
                    val loopX = findLoop(moons, 0, 0)
                    loopX `should equal` 6
                }
                it("should find all loops") {
                    val loops = findAllLoopsAllMoons(moons) // Not all moons are connected, therefore every moon has to be checked
                    val shortestLoop = lcm(loops)
                    shortestLoop `should equal` 2772
                }
            }
            given("example with longer loop") {
                val startMoons = listOf(
                    Moon(x=-8, y=-10, z=0),
                    Moon(x=5, y=5, z=10),
                    Moon(x=2, y=-7, z=3),
                    Moon(x=9, y=-8, z=-3)
                )
                val moons = startMoons.map { it.copy() }
                it("should find all loops") {
                    val loops = findAllLoops(moons, 0).map { it.toLong() } // Use long because numbers are getting big
                    // All moons are connected, only one moon has to be traced
                    val shortestLoop = lcm(loops)
                    shortestLoop `should equal` 4686774924L
                }
            }
        }
        describe("exercise") {
            given("exercise input") {
                val moons = listOf(
                    Moon(x=  4, y=  1, z=  1),
                    Moon(x= 11, y=-18, z= -1),
                    Moon(x= -2, y=-10, z= -4),
                    Moon(x= -7, y= -2, z= 14)
                )
                xit("should find the loop") {// This takes more than 10 minutes to calculate on a powerful laptop
                    // solution idea: every coordinate loops independently and all moons are connected and fallow the same loops
                    // the combination of these coordinate loops repeat at the least common multiple (lcm) of all loop lengths
                    val loops = findAllLoops(moons, 0).map { it.toLong() } // Use long because numbers are getting big
                        // All moons are connected, only one moon has to be traced
                    val shortestLoop = lcm(loops)
                    shortestLoop `should equal` 326365108375488L
                }
            }
        }

    }

})
