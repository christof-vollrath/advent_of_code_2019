import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.data_driven.data
import org.jetbrains.spek.data_driven.on as onData

/*
--- Day 15: Oxygen System ---

Out here in deep space, many things can go wrong. Fortunately, many of those things have indicator lights.
 Unfortunately, one of those lights is lit: the oxygen system for part of the ship has failed!

According to the readouts, the oxygen system must have failed days ago after a rupture in oxygen tank two;
that section of the ship was automatically sealed once oxygen levels went dangerously low.
 A single remotely-operated repair droid is your only option for fixing the oxygen system.

The Elves' care package included an Intcode program (your puzzle input)
that you can use to remotely control the repair droid.
By running that program, you can direct the repair droid to the oxygen system and fix the problem.

The remote control program executes the following steps in a loop forever:

Accept a movement command via an input instruction.
Send the movement command to the repair droid.
Wait for the repair droid to finish the movement operation.
Report on the status of the repair droid via an output instruction.

Only four movement commands are understood: north (1), south (2), west (3), and east (4).
Any other command is invalid.
The movements differ in direction, but not in distance:
in a long enough east-west hallway, a series of commands like 4,4,4,4,3,3,3,3
would leave the repair droid back where it started.

The repair droid can reply with any of the following status codes:

0: The repair droid hit a wall. Its position has not changed.
1: The repair droid has moved one step in the requested direction.
2: The repair droid has moved one step in the requested direction; its new position is the location of the oxygen system.

You don't know anything about the area around the repair droid, but you can figure it out by watching the status codes.

For example, we can draw the area using D for the droid, # for walls, . for locations the droid can traverse,
and empty space for unexplored locations. Then, the initial state looks like this:



   D


To make the droid go north, send it 1.
If it replies with 0, you know that location is a wall and that the droid didn't move:


   #
   D


To move east, send 4; a reply of 1 means the movement was successful:


   #
   .D


Then, perhaps attempts to move north (1), south (2), and east (4) are all met with replies of 0:


   ##
   .D#
    #

Now, you know the repair droid is in a dead end.
Backtrack with 3 (which you already know will get a reply of 1 because you already know that location is open):


   ##
   D.#
    #

Then, perhaps west (3) gets a reply of 0, south (2) gets a reply of 1, south again (2) gets a reply of 0,
and then west (3) gets a reply of 2:


   ##
  #..#
  D.#
   #

Now, because of the reply of 2, you know you've found the oxygen system!
In this example, it was only 2 moves away from the repair droid's starting position.

What is the fewest number of movement commands required to move the repair droid from its starting position
to the location of the oxygen system?

--- Part Two ---

You quickly repair the oxygen system; oxygen gradually fills the area.

Oxygen starts in the location containing the repaired oxygen system.
It takes one minute for oxygen to spread to all open locations
that are adjacent to a location that already contains oxygen.
Diagonal locations are not adjacent.

In the example above, suppose you've used the droid to explore the area fully and have the following map
(where locations that currently contain oxygen are marked O):

 ##
#..##
#.#..#
#.O.#
 ###

Initially, the only location which contains oxygen is the location of the repaired oxygen system.
However, after one minute, the oxygen spreads to all open (.) locations
that are adjacent to a location containing oxygen:

 ##
#..##
#.#..#
#OOO#
 ###

After a total of two minutes, the map looks like this:

 ##
#..##
#O#O.#
#OOO#
 ###

After a total of three minutes:

 ##
#O.##
#O#OO#
#OOO#
 ###

And finally, the whole region is full of oxygen after a total of four minutes:

 ##
#OO##
#O#OO#
#OOO#
 ###

So, in this example, all locations contain oxygen after 4 minutes.

Use the repair droid to get a complete map of the area.
How many minutes will it take to fill with oxygen?

 */

abstract class AbstractDroid(var pos: Coord2) {

    abstract fun move(direction: Direction): MoveResult
    fun unmove(direction: Direction) = move(direction.undo)
    fun move(directions: List<Direction>) = directions.forEach { move(it) }
    fun unmove(directions: List<Direction>) = directions.reversed().forEach { unmove(it) }

    fun findOxygen(): List<Direction> {
        val allPaths = mutableMapOf(pos to emptyList<Direction>())
        var workingPaths = mapOf(pos to emptyList<Direction>())
        while(true) {
            val newPaths = workingPaths.flatMap { posWithPath ->
                val (_, path) = posWithPath
                move(path)
                val moves = Direction.values()
                val resultPaths = moves.mapNotNull { move ->
                    val result = when(move(move)) {
                        MoveResult.MOVED_TO_OXYGEN -> return path + move // found oxygen
                        MoveResult.MOVED -> {
                            val nextPos = pos
                            unmove(move)
                            if (! allPaths.contains(nextPos)) {
                                nextPos to path + move
                            } else null // path already known
                        }
                        MoveResult.WALL -> null
                    }
                    result
                }
                unmove(path)
                resultPaths
            }
            if (newPaths.isEmpty()) error("Oxygen not found")
            else {
                allPaths.putAll(newPaths)
                workingPaths = newPaths.toMap()
            }
        }
    }

    fun draw(): Pair<String, Int> {
        findOxygen() // Start from Oxygen - droid will be moved to the oxygen generator
        val shipMap = mutableMapOf<Coord2, Char>()
        shipMap[pos] = ' '
        val allPaths = mutableMapOf(pos to emptyList<Direction>())
        var workingPaths = mapOf(pos to emptyList<Direction>())
        while(true) {
            val newPaths = workingPaths.flatMap { posWithPath ->
                val (_, path) = posWithPath
                move(path)
                val moves = Direction.values()
                val resultPaths = moves.mapNotNull { move ->
                    val result = when(val moveResult = move(move)) {
                        MoveResult.MOVED_TO_OXYGEN, MoveResult.MOVED -> {
                            val nextPos = pos
                            shipMap[nextPos] = if (moveResult == MoveResult.MOVED_TO_OXYGEN) 'O' else ' '
                            unmove(move)
                            if (! allPaths.contains(nextPos)) {
                                nextPos to path + move
                            } else null // path already known
                        }
                        MoveResult.WALL -> {
                            shipMap[moveCoord(move, pos)] = '#' // droid not moved, but wall is where the droid would have moved to
                            null
                        }
                    }
                    result
                }
                unmove(path)
                resultPaths
            }
            if (newPaths.isEmpty()) break // everything handled
            else {
                allPaths.putAll(newPaths)
                workingPaths = newPaths.toMap()
            }
        }
        val longestPath = allPaths.values.maxBy { it.size }!!.size
        val maxY = shipMap.keys.maxBy { it.y }!!.y
        val minY = shipMap.keys.minBy { it.y }!!.y
        val minX = shipMap.keys.minBy { it.x }!!.x
        val shipLists = (minY..maxY).map { y ->
            val maxX = shipMap.keys.filter{ it.y == y}.maxBy { it.x }!!.x // maxX per line to avoid filling the line with spaces
            (minX..maxX).map { x ->
                shipMap.getOrDefault(Coord2(x, y), ' ')
            }.joinToString("")
        }
        val mapString = shipLists.joinToString("\n")
        return mapString to longestPath
    }

}

class Droid(intCodes: List<Long>) : AbstractDroid(Coord2(0, 0)) {
    override fun move(direction: Direction): MoveResult =
        runBlocking {
            inputChannel.send(direction.id.toLong())
            val result = outputChannel.receive()
            val moveResult = MoveResult.fromId(result.toInt())!!
            if (moveResult in setOf(MoveResult.MOVED, MoveResult.MOVED_TO_OXYGEN)) {
                pos = moveCoord(direction, pos)
            }
            //println("move=$direction moveResult=$moveResult")
            moveResult
        }

    val inputChannel = Channel<Long>()
    val outputChannel = Channel<Long>()
    val processor = IntCodeProcessor(inputChannel, outputChannel, intCodes)

    init {
        GlobalScope.launch {
            processor.execute()
        }
    }
}

fun moveCoord(direction: Direction, coord: Coord2): Coord2 =
    when (direction) {
        Direction.NORTH -> Coord2(coord.x, coord.y - 1)
        Direction.SOUTH -> Coord2(coord.x, coord.y + 1)
        Direction.WEST -> Coord2(coord.x - 1, coord.y)
        Direction.EAST -> Coord2(coord.x + 1, coord.y)
    }

class DummyDroid(start: Coord2, val shipString: String) : AbstractDroid(start) {
    val ship = shipString.lines().map { it.toList() }

    override fun move(direction: Direction): MoveResult {
        val nextPos = when(direction) {
            Direction.NORTH -> Coord2(pos.x, pos.y - 1)
            Direction.SOUTH -> Coord2(pos.x, pos.y + 1)
            Direction.WEST -> Coord2(pos.x - 1, pos.y)
            Direction.EAST -> Coord2(pos.x + 1, pos.y)
        }
        return when(ship[nextPos.y][nextPos.x]) {
            '#' -> MoveResult.WALL
            ' ' -> {
                pos = nextPos
                MoveResult.MOVED
            }
            'O' -> {
                pos = nextPos
                MoveResult.MOVED_TO_OXYGEN
            }
            else -> error("Wrong char in map at ${nextPos.x},${nextPos.y}")
        }
    }
}


enum class Direction(val id: Int) {
    NORTH(1), SOUTH(2), WEST(3), EAST(4);

    val undo: Direction
        get() = when(this) {
            NORTH -> SOUTH
            SOUTH -> NORTH
            WEST -> EAST
            EAST -> WEST
        }
}

enum class MoveResult(val id: Int) {
    WALL(0), MOVED(1), MOVED_TO_OXYGEN(2);

    companion object {
        fun fromId(id: Int) = values().find { it.id == id }
    }
}

class Day15Spec : Spek({

    describe("part 1") {
        describe("dummy droid") {
            describe("moving the dummy") {
                val droid = DummyDroid(Coord2(2, 1),
                    """
                        ###############
                        #       # O   #
                        #######       #
                        ###############
                    """.trimIndent())
                it("should move to the west") {
                    val result = droid.move(Direction.WEST)
                    result `should equal` MoveResult.MOVED
                }
                it("should move back to the east") {
                    val result = droid.move(Direction.EAST)
                    result `should equal` MoveResult.MOVED
                }
                it("should find a wall") {
                    var result: MoveResult
                    do {
                        result = droid.move(Direction.EAST)
                    } while (result != MoveResult.WALL)
                    result `should equal` MoveResult.WALL
                }
            }
            describe("find oxygen") {
                it("should find the oxygen for direct neighbour") {
                    val droid = DummyDroid(Coord2(1, 1),
                        """
                            ####
                            # O#
                            ####
                        """.trimIndent())
                    val path = droid.findOxygen()
                    path.size `should equal` 1
                }
                it("should find the oxygen for a distant neighbour") {
                    val droid = DummyDroid(Coord2(2, 1),
                        """
                            ###############
                            #       # O   #
                            #######       #
                            ###############
                        """.trimIndent())
                    val path = droid.findOxygen()
                    println(path)
                    path.size `should equal` 10
                }
                it("should find the oxygen for a distant neighbour with many paths") {
                    val droid = DummyDroid(Coord2(2, 1),
                        """
                            ###############
                            #       # #  O#
                            # # # # # # ###
                            #     #       #
                            ###############
                        """.trimIndent())
                    val path = droid.findOxygen()
                    println(path)
                    path.size `should equal` 15
                }
            }

        }
        given("int codes for the droid") {
            val intCodesString = readResource("day15Input.txt")!!
            val intCodes = parseIntCodes09(intCodesString)
            describe("moving a droid") {
                val droid = Droid(intCodes)
                it("should move to the west") {
                    val result = droid.move(Direction.WEST)
                    result `should equal` MoveResult.MOVED
                }
                it("should move back to the east") {
                    val result = droid.move(Direction.EAST)
                    result `should equal` MoveResult.MOVED
                }
                it("should find a wall") {
                    var result: MoveResult
                    do {
                        result = droid.move(Direction.EAST)
                    } while (result != MoveResult.WALL)
                    result `should equal` MoveResult.WALL
                }
            }
            describe("exercise") {
                it("should find the oxygen") {
                    val droid = Droid(intCodes)
                    val path = droid.findOxygen()
                    println(path)
                    path.size `should equal` 282
                }
            }
        }
    }
    describe("part 2") {
        describe("draw maps and fill oxygen") {
            val testData = arrayOf(
                data(
                    DummyDroid(
                        Coord2(1, 1),
                        """
                         ##
                        # O#
                         ##
                    """.trimIndent()
                    ), 1
                ),
                data(
                    DummyDroid(
                        Coord2(2, 1),
                        """
                         ####### # ###
                        #       # #  O#
                        # # # # # # ##
                        #     #       #
                         ##### #######
                    """.trimIndent()
                    ), 18
                )
            )
            onData("ship %s ", with = *testData) { droid, expected ->
                val (map, oxygenLength) = droid.draw()
                it("should draw the map") {
                    map `should equal` droid.shipString
                }
                it("should result to $expected") {
                    oxygenLength `should equal` expected
                }
            }

        }
        describe("exercise") {
            val intCodesString = readResource("day15Input.txt")!!
            val intCodes = parseIntCodes09(intCodesString)
            it("should fill with oxygen") {
                val droid = Droid(intCodes)
                val (_, oxygenLength) = droid.draw()
                oxygenLength `should equal` 286
            }

        }
    }
})
