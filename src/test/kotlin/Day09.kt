import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.data_driven.data
import org.jetbrains.spek.data_driven.on as onData
import java.lang.IllegalArgumentException

/*
--- Day 9: Sensor Boost ---

You've just said goodbye to the rebooted rover and left Mars when you receive a faint distress signal
coming from the asteroid belt.
It must be the Ceres monitoring station!

In order to lock on to the signal, you'll need to boost your sensors.
The Elves send up the latest BOOST program - Basic Operation Of System Test.

While BOOST (your puzzle input) is capable of boosting your sensors, for tenuous safety reasons,
it refuses to do so until the computer it runs on passes some checks to demonstrate it is a complete Intcode computer.

Your existing Intcode computer is missing one key feature: it needs support for parameters in relative mode.

Parameters in mode 2, relative mode, behave very similarly to parameters in position mode:
the parameter is interpreted as a position.
Like position mode, parameters in relative mode can be read from or written to.

The important difference is that relative mode parameters don't count from address 0.
Instead, they count from a value called the relative base. The relative base starts at 0.

The address a relative mode parameter refers to is itself plus the current relative base.
When the relative base is 0, relative mode parameters and position mode parameters with the same value
refer to the same address.

For example, given a relative base of 50, a relative mode parameter of -7 refers to memory address 50 + -7 = 43.

The relative base is modified with the relative base offset instruction:

Opcode 9 adjusts the relative base by the value of its only parameter.
The relative base increases (or decreases, if the value is negative) by the value of the parameter.
For example, if the relative base is 2000, then after the instruction 109,19, the relative base would be 2019.
If the next instruction were 204,-34, then the value at address 1985 would be output.

Your Intcode computer will also need a few other capabilities:

The computer's available memory should be much larger than the initial program.
Memory beyond the initial program starts with the value 0 and can be read or written like any other memory.
(It is invalid to try to access memory at a negative address, though.)
The computer should have support for large numbers.
Some instructions near the beginning of the BOOST program will verify this capability.

Here are some example programs that use these features:

109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99 takes no input and produces a copy of itself as output.
1102,34915192,34915192,7,4,7,99,0 should output a 16-digit number.
104,1125899906842624,99 should output the large number in the middle.

The BOOST program will ask for a single input; run it in test mode by providing it the value 1.
It will perform a series of checks on each opcode, output any opcodes (and the associated parameter modes)
that seem to be functioning incorrectly, and finally output a BOOST keycode.

Once your Intcode computer is fully functional, the BOOST program should report no malfunctioning opcodes
when run in test mode; it should only output a single value, the BOOST keycode. What BOOST keycode does it produce?

--- Part Two ---

You now have a complete Intcode computer.

Finally, you can lock on to the Ceres distress signal! You just need to boost your sensors using the BOOST program.

The program runs in sensor boost mode by providing the input instruction the value 2.
Once run, it will boost the sensors automatically,
but it might take a few seconds to complete the operation on slower hardware.
In sensor boost mode, the program will output a single value: the coordinates of the distress signal.

Run the BOOST program in sensor boost mode. What are the coordinates of the distress signal?

 */

fun List<Long>.executeExtendedIntCodes09(input: List<Long>): List<Long> { // Even more intcodes and unlimited memory
    val currentState = mutableMapOf<Long, Long>() // Use map to emulate virtual infinite memory
    forEachIndexed { index, value -> currentState[index.toLong()] = value  }
    var currentIndex = 0L
    var currentBase = 0L
    val outputMutable = mutableListOf<Long>()
    val inputMutable = input.toMutableList()
    while(true) {
        val commandWithParameterModes = currentState.getOrDefault(currentIndex, 0L)
        val (command, parameterModes) = commandWithParameterModes.toCommand09()
            // Commands are small enough to fit into an int
        println("curentIndex=$currentIndex commandWithParameterModes=$commandWithParameterModes command=$command")
        when(command) {
            1L -> { // Add
                val indexes = getParameterIndexes09(currentIndex, parameterModes, currentState, 1..3, currentBase)
                currentState[indexes[2]] = currentState.getOrDefault(indexes[0], 0L) + currentState.getOrDefault(indexes[1], 0L)
                currentIndex += 4
            }
            2L -> { // Multiply
                val indexes = getParameterIndexes09(currentIndex, parameterModes, currentState, 1..3, currentBase)
                currentState[indexes[2]] = currentState.getOrDefault(indexes[0], 0L) * currentState.getOrDefault(indexes[1], 0L)
                currentIndex += 4
            }
            3L -> { // Input
                val inputInt = inputMutable.first()
                inputMutable.removeAt(0)
                val indexes = getParameterIndexes09(currentIndex, parameterModes, currentState, 1..1, currentBase)
                currentState[indexes[0]] = inputInt
                currentIndex += 2
            }
            4L -> { // Ouput
                val indexes = getParameterIndexes09(currentIndex, parameterModes, currentState, 1..1, currentBase)
                val outputInt = currentState.getOrDefault(indexes[0], 0L)
                outputMutable += outputInt
                currentIndex += 2
            }
            5L -> { // Jump if true
                val indexes = getParameterIndexes09(currentIndex, parameterModes, currentState, 1..2, currentBase)
                if (currentState.getOrDefault(indexes[0], 0L) != 0L)
                    currentIndex = currentState.getOrDefault(indexes[1], 0L)
                else
                    currentIndex += 3
            }
            6L -> { // Jump if false
                val indexes = getParameterIndexes09(currentIndex, parameterModes, currentState, 1..2, currentBase)
                if (currentState.getOrDefault(indexes[0], 0L) == 0L)
                    currentIndex = currentState.getOrDefault(indexes[1], 0L)
                else
                    currentIndex += 3
            }
            7L -> { // Less than
                val indexes = getParameterIndexes09(currentIndex, parameterModes, currentState, 1..3, currentBase)
                currentState[indexes[2]] = if (currentState.getOrDefault(indexes[0], 0L) < currentState.getOrDefault(indexes[1], 0L)) 1L else 0L
                currentIndex += 4
            }
            8L -> { // Equals
                val indexes = getParameterIndexes09(currentIndex, parameterModes, currentState, 1..3, currentBase)
                currentState[indexes[2]] = if (currentState.getOrDefault(indexes[0], 0L) == currentState.getOrDefault(indexes[1], 0L)) 1L else 0L
                currentIndex += 4
            }
            9L -> { // Add relative base
                val indexes = getParameterIndexes09(currentIndex, parameterModes, currentState, 1..1, currentBase)
                val incr = currentState.getOrDefault(indexes[0], 0L)
                currentBase += incr
                currentIndex += 2
            }
            99L -> return outputMutable
            else -> throw IllegalArgumentException("currentIndex=$currentIndex command=$command")
        }
    }
}

enum class ParameterMode { IMMEDIATE, POSITION, RELATIVE }

fun Long.toCommand09(): Pair<Long, MutableList<ParameterMode>> {
    val command = this % 100
    val commandInt = this
    val parameterModes = sequence {
        listOf(10_000, 1000, 100).fold(commandInt) { interim, current ->
            val parameterMode = when (val parameterModeId = interim / current) {
                0L -> ParameterMode.POSITION
                1L -> ParameterMode.IMMEDIATE
                2L -> ParameterMode.RELATIVE
                else -> throw IllegalArgumentException("Illegal parameter mode = $parameterModeId")
            }
            yield(parameterMode)
            interim % current
        }
    }.toMutableList()
    parameterModes.reverse()
    return command to parameterModes
}

fun getParameterIndexes09(currentIndex: Long, parameterModes: List<ParameterMode>, currentState: Map<Long, Long>, range: IntRange, currentBase: Long) =
    range.map { offset ->
        val index = currentIndex + offset
        when(parameterModes[offset - 1] ) {
            ParameterMode.IMMEDIATE -> index
            ParameterMode.POSITION -> currentState.getOrDefault(index, 0L)
            ParameterMode.RELATIVE -> currentBase + currentState.getOrDefault(index, 0L)
    }
}
fun parseIntCodes09(inputString: String): List<Long> = inputString.split(",").map { it.trim().toLong() }

class Day09Spec : Spek({

    describe("part 1") {
        given("input from day 5 should still work with extended int codes interpreter") {
            val intCodesString = readResource("day05Input.txt")!!
            on("parse and execute") {
                val intCodes = parseIntCodes09(intCodesString)
                val input = listOf(1L)
                val result = intCodes.executeExtendedIntCodes09(input)
                println(result)
                it("should have the right result") {
                    result.dropLast(1).forEach {
                        it `should equal` 0L
                    }
                }
                it("should have the right diagnostic code") {
                    val diagnosticCode = result.last()
                    diagnosticCode `should equal` 13294380L
                }
            }
        }
        describe("examples") {
            val testData = arrayOf(
                data("109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99", listOf<Long>(), listOf(109L, 1L, 204L, -1L, 1001L, 100L, 1L, 100L, 1008L, 100L, 16L, 101L, 1006L, 101L, 0L, 99L)),
                data("1102,34915192,34915192,7,4,7,99,0", listOf<Long>(), listOf(1219070632396864L)),
                data("104,1125899906842624,99", listOf<Long>(), listOf(1125899906842624L))
            )
            onData("intCodes %s input %s", with = *testData) { intCodesString, input, expected ->
                it("should calculate $expected") {
                    val intCodes = parseIntCodes09(intCodesString)
                    val result = intCodes.executeExtendedIntCodes09(input)
                    result `should equal` expected
                }
            }
        }
        describe("exercise") {
            val inputString = readResource("day09Input.txt")!!
            val intCodes = parseIntCodes09(inputString)
            val input = listOf(1L)
            val result = intCodes.executeExtendedIntCodes09(input)
            it("should return only one value") {
                result.size `should equal` 1
            }
            it("should have the right value") {
                result[0] `should equal` 2399197539L
            }
        }
    }
    describe("part 2") {
        describe("exercise input 2") {
            val inputString = readResource("day09Input.txt")!!
            val intCodes = parseIntCodes09(inputString)
            val input = listOf(2L)
            val result = intCodes.executeExtendedIntCodes09(input)
            it("should return only one value") {
                result.size `should equal` 1
            }
            it("should have the right value") {
                result[0] `should equal` 35106L
            }
        }
    }
})
