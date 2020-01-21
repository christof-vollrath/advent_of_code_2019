import org.amshove.kluent.`should equal`
import org.amshove.kluent.`should not equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.data_driven.data
import kotlin.math.abs
import org.jetbrains.spek.data_driven.on as onData

/*
--- Day 16: Flawed Frequency Transmission ---

You're 3/4ths of the way through the gas giants. Not only do roundtrip signals to Earth take five hours,
but the signal quality is quite bad as well.
You can clean up the signal with the Flawed Frequency Transmission algorithm, or FFT.

As input, FFT takes a list of numbers. In the signal you received (your puzzle input),
each number is a single digit: data like 15243 represents the sequence 1, 5, 2, 4, 3.

FFT operates in repeated phases. In each phase, a new list is constructed with the same length as the input list.
This new list is also used as the input for the next phase.

Each element in the new list is built by multiplying every value in the input list by a value in a repeating pattern
and then adding up the results.
So, if the input list were 9, 8, 7, 6, 5 and the pattern for a given element were 1, 2, 3,
the result would be 9*1 + 8*2 + 7*3 + 6*1 + 5*2
(with each input element on the left and each value in the repeating pattern on the right of each multiplication).
Then, only the ones digit is kept: 38 becomes 8, -17 becomes 7, and so on.

While each element in the output array uses all of the same input array elements,
the actual repeating pattern to use depends on which output element is being calculated.
The base pattern is 0, 1, 0, -1.
Then, repeat each value in the pattern a number of times equal to the position in the output list being considered.
Repeat once for the first element, twice for the second element, three times for the third element, and so on.
So, if the third element of the output list is being calculated, repeating the values would produce:
0, 0, 0, 1, 1, 1, 0, 0, 0, -1, -1, -1.

When applying the pattern, skip the very first value exactly once.
(In other words, offset the whole pattern left by one.)
So, for the second element of the output list, the actual pattern used would be:
0, 1, 1, 0, 0, -1, -1, 0, 0, 1, 1, 0, 0, -1, -1, ....

After using this process to calculate each element of the output list, the phase is complete,
and the output list of this phase is used as the new input list for the next phase, if any.

Given the input signal 12345678, below are four phases of FFT.
Within each phase, each output digit is calculated on a single line with the result at the far right;
each multiplication operation shows the input digit on the left and the pattern value on the right:

Input signal: 12345678

1*1  + 2*0  + 3*-1 + 4*0  + 5*1  + 6*0  + 7*-1 + 8*0  = 4
1*0  + 2*1  + 3*1  + 4*0  + 5*0  + 6*-1 + 7*-1 + 8*0  = 8
1*0  + 2*0  + 3*1  + 4*1  + 5*1  + 6*0  + 7*0  + 8*0  = 2
1*0  + 2*0  + 3*0  + 4*1  + 5*1  + 6*1  + 7*1  + 8*0  = 2
1*0  + 2*0  + 3*0  + 4*0  + 5*1  + 6*1  + 7*1  + 8*1  = 6
1*0  + 2*0  + 3*0  + 4*0  + 5*0  + 6*1  + 7*1  + 8*1  = 1
1*0  + 2*0  + 3*0  + 4*0  + 5*0  + 6*0  + 7*1  + 8*1  = 5
1*0  + 2*0  + 3*0  + 4*0  + 5*0  + 6*0  + 7*0  + 8*1  = 8

After 1 phase: 48226158

4*1  + 8*0  + 2*-1 + 2*0  + 6*1  + 1*0  + 5*-1 + 8*0  = 3
4*0  + 8*1  + 2*1  + 2*0  + 6*0  + 1*-1 + 5*-1 + 8*0  = 4
4*0  + 8*0  + 2*1  + 2*1  + 6*1  + 1*0  + 5*0  + 8*0  = 0
4*0  + 8*0  + 2*0  + 2*1  + 6*1  + 1*1  + 5*1  + 8*0  = 4
4*0  + 8*0  + 2*0  + 2*0  + 6*1  + 1*1  + 5*1  + 8*1  = 0
4*0  + 8*0  + 2*0  + 2*0  + 6*0  + 1*1  + 5*1  + 8*1  = 4
4*0  + 8*0  + 2*0  + 2*0  + 6*0  + 1*0  + 5*1  + 8*1  = 3
4*0  + 8*0  + 2*0  + 2*0  + 6*0  + 1*0  + 5*0  + 8*1  = 8

After 2 phases: 34040438

3*1  + 4*0  + 0*-1 + 4*0  + 0*1  + 4*0  + 3*-1 + 8*0  = 0
3*0  + 4*1  + 0*1  + 4*0  + 0*0  + 4*-1 + 3*-1 + 8*0  = 3
3*0  + 4*0  + 0*1  + 4*1  + 0*1  + 4*0  + 3*0  + 8*0  = 4
3*0  + 4*0  + 0*0  + 4*1  + 0*1  + 4*1  + 3*1  + 8*0  = 1
3*0  + 4*0  + 0*0  + 4*0  + 0*1  + 4*1  + 3*1  + 8*1  = 5
3*0  + 4*0  + 0*0  + 4*0  + 0*0  + 4*1  + 3*1  + 8*1  = 5
3*0  + 4*0  + 0*0  + 4*0  + 0*0  + 4*0  + 3*1  + 8*1  = 1
3*0  + 4*0  + 0*0  + 4*0  + 0*0  + 4*0  + 3*0  + 8*1  = 8

After 3 phases: 03415518

0*1  + 3*0  + 4*-1 + 1*0  + 5*1  + 5*0  + 1*-1 + 8*0  = 0
0*0  + 3*1  + 4*1  + 1*0  + 5*0  + 5*-1 + 1*-1 + 8*0  = 1
0*0  + 3*0  + 4*1  + 1*1  + 5*1  + 5*0  + 1*0  + 8*0  = 0
0*0  + 3*0  + 4*0  + 1*1  + 5*1  + 5*1  + 1*1  + 8*0  = 2
0*0  + 3*0  + 4*0  + 1*0  + 5*1  + 5*1  + 1*1  + 8*1  = 9
0*0  + 3*0  + 4*0  + 1*0  + 5*0  + 5*1  + 1*1  + 8*1  = 4
0*0  + 3*0  + 4*0  + 1*0  + 5*0  + 5*0  + 1*1  + 8*1  = 9
0*0  + 3*0  + 4*0  + 1*0  + 5*0  + 5*0  + 1*0  + 8*1  = 8

After 4 phases: 01029498

Here are the first eight digits of the final output list after 100 phases for some larger inputs:

80871224585914546619083218645595 becomes 24176176.
19617804207202209144916044189917 becomes 73745418.
69317163492948606335995924319873 becomes 52432133.

After 100 phases of FFT, what are the first eight digits in the final output list?

--- Part Two ---

Now that your FFT is working, you can decode the real signal.

The real signal is your puzzle input repeated 10000 times.
Treat this new signal as a single input list.
Patterns are still calculated as before, and 100 phases of FFT are still applied.

The first seven digits of your initial input signal also represent the message offset.
The message offset is the location of the eight-digit message in the final output list.
Specifically, the message offset indicates the number of digits to skip before reading the eight-digit message.
For example, if the first seven digits of your initial input signal were 1234567,
the eight-digit message would be the eight digits after skipping 1,234,567 digits of the final output list.
Or, if the message offset were 7 and your final output list were 98765432109876543210,
the eight-digit message would be 21098765.
(Of course, your real message offset will be a seven-digit number, not a one-digit number like 7.)

Here is the eight-digit message in the final output list after 100 phases.
The message offset given in each input has been highlighted.
(Note that the inputs given below are repeated 10000 times to find the actual starting input lists.)

03036732577212944063491565474664 becomes 84462026.
02935109699940807407585447034323 becomes 78725270.
03081770884921959731165446850517 becomes 53553731.

After repeating your input signal 10000 times and running 100 phases of FFT,
what is the eight-digit message embedded in the final output list?


 */


fun String.fftOptimized(times: Int, phases: Int): String = (0 until phases).fold(times(times)) { accu, phase ->
    println("phase=$phase accu.length=${accu.length}")
    accu.fftOnePhase()
}

/* TODO
fun String.fftOnePhaseOptimized(times: Int): String
{
    val lcm = lcm(length,  times * basePattern.size)
    if (lcm > length * times) {
        println("Cannot optimize times=$times length=$length")
    }
    val minimalTimes = lcm / length
    val multiplier = times / minimalTimes
    val minimalString = times(minimalTimes)
    println("length=$length times=$times phases=$phases lcm=$lcm minimalTimes=$minimalTimes multiplier=$multiplier minimalString=$minimalString")
    return (0 until phases).fold(minimalString) { accu, phase ->
        println("phase=$phase accu=$accu")
        accu.fftOnePhase(multiplier).times(multiplier)
    }
}
*/

fun String.fft(phases: Int): String = (0 until phases).fold(this) { accu, phase ->
    println("phase=$phase accu.length=${accu.length}")
    accu.fftOnePhase()
}

fun String.times(times: Int): String = if (times == 1) this else
    (1 until times).fold(this) { accu, phase ->
        accu + this
    }

fun String.fftOnePhase() = (0 until length).map { column ->
    /*
        val sum = toList().mapIndexed {row, c ->
            val h = c.toString().toInt()
            h * patternFactor(row, column)
        }.sum()
    */ // Here optimized to run four times faster:
    var sum = 0
    for (i in 0 until length) {
        sum += fftOneStep(i, column)
    }
    abs(sum) % 10
}.joinToString("")

private fun String.fftOneStep(row: Int, column: Int): Int {
    var sum1 = 0
    val c = get(row)
    val h = c - '0'
    sum1 += h * patternFactor(row, column)
    return sum1
}

fun patternFactor(row: Int, column: Int) =
    basePattern[((row + 1) / (column + 1)) % basePattern.size]

val basePattern = listOf(0, 1, 0, -1)

fun pattern(i: Int) = basePattern.flatMap { patternValue -> List(i + 1) { patternValue } }

class Day16Spec : Spek({

    describe("part 1") {
        describe("FFT") {
            val testData = arrayOf(
                data("1", 1, "1"),
                data("12", 1, "12"),
                data("12345678", 1, "48226158"),
                data("12345678", 2, "34040438"),
                data("12345678", 3, "03415518"),
                data("12345678", 4, "01029498"),
                data("80871224585914546619083218645595", 100, "24176176"),
                data("19617804207202209144916044189917", 100, "73745418"),
                data("69317163492948606335995924319873", 100, "52432133")
            )
            onData("fft %s phases %d ", with = *testData) { input, phases, expected ->
                it("should calculate fft") {
                    input.fft(phases).take(8) `should equal` expected
                }
            }
        }
        describe("exercise") {
            it("should calculate fft") {
                val input = readResource("day16Input.txt")!!
                println("input.length=${input.length}")
                input.fft(100).take(8) `should equal` "40580215"
            }
        }
    }
    describe("part 2") {
        describe("times") {
            val testData = arrayOf(
                data("1", 1, "1"),
                data("1", 2, "11"),
                data("12", 3, "121212")
            )
            onData("%s times %d ", with = *testData) { input, times, expected ->
                it("should calculate string times") {
                    input.times(times) `should equal` expected
                }
            }
        }
        describe("can we use a list as a hash key") {
            it("should calculate a different hash key when the list changes") {
                val list = mutableListOf(1, 2)
                val key1 = list.hashCode()
                list.add(3)
                val key2 = list.hashCode()
                println("key1=$key1 key2=$key2")
                key1 `should not equal` key2
            }
        }

        describe("what happens when an input of the same length as the pattern is duplicated") {
            given("given input with the lenght of the base pattern") {
                val input = "1234"
                it("should calculate fft with phase 1") {
                    input.fft(1) `should equal` "2574"
                }
                it("should calculate fft with phase 1 for doubled input") {
                    (input+input).fft(1) `should equal` "40800974"
                }
            }
        }
        describe("lcm of pattern and input") {
            it ("should calulate repeatable length") {
                for(i in 1..100) {
                    val inputLength = "03036732577212944063491565474664".length
                    val patternLength = 4 * i
                    println("i=$i inputLength=${inputLength} patternLength=${patternLength} lcm(inputLength, patternLength)=${lcm(inputLength, patternLength)}")
                }
                for(i in 1..100) {
                    val inputLength = 650
                    val patternLength = 4 * i
                    println("i=$i inputLength=${inputLength} patternLength=${patternLength} lcm(inputLength, patternLength)=${lcm(inputLength, patternLength)}")
                }
            }
        }
        describe("fft for repeated strings") {
            describe("FFT") {
                val testData = arrayOf(
                    data("12345678", 3, 1)//,
                    /*
                    data("12345678", 3, 2),
                    data("12345678", 3, 3),
                    data("12345678", 10, 4),
                    data("12345678", 2, 4),
                    data("12345678", 80, 4)
                     */
                )
                onData("fft %s repeated %d phase %d ", with = *testData) { input, repeated, phases ->
                    it("should calculate fft") {
                        val fftpRepeatedString = input.times(repeated).fft(phases)
                        val fftpOptimized = input.fftOptimized(repeated, phases)
                        fftpOptimized `should equal` fftpRepeatedString
                    }
                }
            }

        }
        /*
        describe("decode") {
            val testData = arrayOf(
                data("03036732577212944063491565474664", 100, "84462026"),
                data("02935109699940807407585447034323", 100, "78725270"),
                data("03081770884921959731165446850517", 100, "53553731")
            )
            onData("decode %s phases %d ", with = *testData) { input, phases, expected ->
                it("should decode") {
                    val fftResult = input.times(10000).fft(phases)
                    val offsetString = fftResult.take(7)
                    val offsetInt = offsetString.toInt()
                    println("offset=$offsetInt")
                    val result = fftResult.drop(offsetInt).take(8)
                    println("result=$result")
                    result `should equal` expected
                    input.fftOptimized(10000, phases).take(8) `should equal` expected
                }
            }
        }
        */
    }
})


