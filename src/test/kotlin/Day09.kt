import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
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
 */

fun List<Int>.executeExtendedIntCodes09(input: List<Int>): List<Int> { // Even more intcodes and unlimited memory
    val currentState = toMutableList()
    var currentIndex = 0
    val outputMutable = mutableListOf<Int>()
    val inputMutable = input.toMutableList()
    while(true) {
        val commandWithParameterModes = currentState[currentIndex]
        val (command, parameterModes) = commandWithParameterModes.toCommand()
        println("curentIndex=$currentIndex commandWithParameterModes=$commandWithParameterModes command=$command")
        when(command) {
            1 -> { // Add
                val indexes = getParameterIndexes(currentIndex, parameterModes, currentState, 1..3)
                currentState[indexes[2]] = currentState[indexes[0]] + currentState[indexes[1]]
                currentIndex += 4
            }
            2 -> { // Multiply
                val indexes = getParameterIndexes(currentIndex, parameterModes, currentState, 1..3)
                currentState[indexes[2]] = currentState[indexes[0]] * currentState[indexes[1]]
                currentIndex += 4
            }
            3 -> { // Input
                val inputInt = inputMutable.first()
                inputMutable.removeAt(0)
                val indexes = getParameterIndexes(currentIndex, parameterModes, currentState, 1..1)
                currentState[indexes[0]] = inputInt
                currentIndex += 2
            }
            4 -> { // Input
                val indexes = getParameterIndexes(currentIndex, parameterModes, currentState, 1..1)
                val outputInt = currentState[indexes[0]]
                outputMutable += outputInt
                currentIndex += 2
            }
            5 -> { // Jump if true
                val indexes = getParameterIndexes(currentIndex, parameterModes, currentState, 1..2)
                if (currentState[indexes[0]] != 0)
                    currentIndex = currentState[indexes[1]]
                else
                    currentIndex += 3
            }
            6 -> { // Jump if false
                val indexes = getParameterIndexes(currentIndex, parameterModes, currentState, 1..2)
                if (currentState[indexes[0]] == 0)
                    currentIndex = currentState[indexes[1]]
                else
                    currentIndex += 3
            }
            7 -> { // Less than
                val indexes = getParameterIndexes(currentIndex, parameterModes, currentState, 1..3)
                currentState[indexes[2]] = if (currentState[indexes[0]] < currentState[indexes[1]]) 1 else 0
                currentIndex += 4
            }
            8 -> { // Equals
                val indexes = getParameterIndexes(currentIndex, parameterModes, currentState, 1..3)
                currentState[indexes[2]] = if (currentState[indexes[0]] == currentState[indexes[1]]) 1 else 0
                currentIndex += 4
            }
            99 -> return outputMutable
            else -> throw IllegalArgumentException("currentIndex=$currentIndex command=$command")
        }
    }
}

class Day09Spec : Spek({

    describe("part 1") {
        given("exercise input from day 5 should still work") {
            val intCodesString = readResource("day05Input.txt")!!
            on("parse and execute") {
                val intCodes = parseIntCodes(intCodesString)
                val input = listOf(1)
                val result = intCodes.executeExtendedIntCodes09(input)
                println(result)
                it("should have the right result") {
                    result.dropLast(1).forEach {
                        it `should equal` 0
                    }
                }
                it("should have the right diagnostic code") {
                    val diagnosticCode = result.last()
                    diagnosticCode `should equal` 13294380
                }
            }
        }
    }
})
